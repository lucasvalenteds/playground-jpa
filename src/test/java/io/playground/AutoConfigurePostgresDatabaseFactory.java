package io.playground;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

public final class AutoConfigurePostgresDatabaseFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
        if (!AnnotatedElementUtils.hasAnnotation(testClass, AutoConfigurePostgresDatabase.class)) {
            return null;
        }

        return (context, mergedConfig) -> {
            final var container = new PostgreSQLContainer<>(DockerImageName.parse("postgres"));
            container.start();

            final var propertySource = new MapPropertySource("default", Map.ofEntries(
                    Map.entry("spring.datasource.url", container.getJdbcUrl()),
                    Map.entry("spring.datasource.username", container.getUsername()),
                    Map.entry("spring.datasource.password", container.getPassword())
            ));

            context.getEnvironment()
                    .getPropertySources()
                    .addFirst(propertySource);
        };
    }
}
