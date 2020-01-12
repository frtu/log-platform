package com.github.frtu.metrics.prometheus;

import com.github.frtu.logs.ApplicationMetadata;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer registry configuration
 */
@Configuration
public class RegistryConfig {
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(ApplicationMetadata applicationMetadata) {
        return registry -> registry.config().commonTags("app.name", applicationMetadata.getApplicationName());
    }

    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}