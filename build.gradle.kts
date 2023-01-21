import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.configurationcache.extensions.capitalized
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
}

allprojects {
    apply<JavaPlugin>()
    apply<SpringBootPlugin>()
    apply<DependencyManagementPlugin>()

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        runtimeOnly("org.postgresql:postgresql")
        implementation("org.flywaydb:flyway-core")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:postgresql")
    }

    dependencyManagement {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
            mavenBom("org.testcontainers:testcontainers-bom:1.17.6")
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(19))
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }
}

subprojects {
    sourceSets {
        test {
            compileClasspath += rootProject.sourceSets.main.get().output
            runtimeClasspath += rootProject.sourceSets.main.get().output
            compileClasspath += rootProject.sourceSets.test.get().output
            runtimeClasspath += rootProject.sourceSets.test.get().output
        }
    }
}

val createModule by tasks.creating<Task> {
    doFirst {
        val module: String by properties
        val testSuiteName = module.split("-")
                .joinToString(separator = "", postfix = "Test", transform = String::capitalized)
        val testSuiteFilePath = "$module/src/test/java/io/playground/$testSuiteName.java"

        val directories = setOf(
                "$module/src/main/java/io/playground",
                "$module/src/main/resources/db/migration",
                "$module/src/test/java/io/playground")
        val files = setOf(
                "$module/src/main/resources/db/migration/V1__create_schema.sql",
                testSuiteFilePath
        )

        logger.info("Creating module named $module")
        directories.forEach(::mkdir)
        files.forEach { file(it).createNewFile() }

        with(file(testSuiteFilePath)) {
            appendText("""
                package io.playground;

                import org.junit.jupiter.api.Test;
                import org.springframework.boot.test.context.SpringBootTest;

                import static org.assertj.core.api.Assertions.assertThat;

                @SpringBootTest
                @AutoConfigurePostgresDatabase
                class $testSuiteName {

                    @Test
                    void test() {
                        
                    }
                }

            """.trimIndent())
        }

        with(file("settings.gradle.kts")) {
            appendText("""include(":$module")""")
            appendBytes(System.lineSeparator().toByteArray())
        }

        (directories + files).sorted().forEach(logger::info)
        logger.info("Module named $module created successfully")
    }
}

project(":monetary-amount") {
    dependencies {
        implementation("org.javamoney:moneta:1.4.2")
        implementation("io.hypersistence:hypersistence-utils-hibernate-60:3.1.1")
        implementation("com.fasterxml.jackson.module:jackson-module-jakarta-xmlbind-annotations")
    }
}
