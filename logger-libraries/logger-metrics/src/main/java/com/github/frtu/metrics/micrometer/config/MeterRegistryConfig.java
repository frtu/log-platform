package com.github.frtu.metrics.micrometer.config;

import com.github.frtu.logs.config.condition.AopConditionalOnClass;
import com.github.frtu.logs.core.ApplicationMetadata;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer registry configuration
 */
@Configuration
public class MeterRegistryConfig {
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(ApplicationMetadata applicationMetadata) {
        final String applicationName = applicationMetadata.getApplicationName();
        return registry -> registry.config().commonTags("app.name", applicationName);
    }

    /**
     * Creating AOP aspect watching for io.micrometer.core.annotation.Timed
     *
     * @param registry {@link MeterRegistry}
     * @return TimedAspect
     */
    @Bean
    @Conditional(AopConditionalOnClass.class)
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}