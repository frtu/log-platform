package com.github.frtu.metrics.micrometer.config;

import com.github.frtu.logs.core.ApplicationMetadata;
import com.github.frtu.spring.conditional.commons.AopConditionalOnClass;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer registry configuration
 *
 * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics">Sprint-boot Metrics</a>
 * @see <a href="https://spring.io/blog/2018/03/16/micrometer-spring-boot-2-s-new-application-metrics-collector">MeterRegistry</a>
 * @since 0.9.5
 */
@Configuration
public class MeterRegistryConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeterRegistryConfig.class);

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(ApplicationMetadata applicationMetadata) {
        final String applicationName = applicationMetadata.getApplicationName();
        LOGGER.debug("Adding app.name='{}' to MeterRegistry", applicationName);
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
        LOGGER.info("Activate @Annotation io.micrometer.core.annotation.Timed using TimedAspect");
        return new TimedAspect(registry);
    }
}