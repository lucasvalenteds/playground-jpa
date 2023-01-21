import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.gradle.api.tasks.testing.logging.TestLogEvent
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
