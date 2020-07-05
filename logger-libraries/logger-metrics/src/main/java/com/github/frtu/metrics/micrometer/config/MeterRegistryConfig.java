package com.github.frtu.metrics.micrometer.config;

import com.github.frtu.logs.core.metadata.ApplicationMetadata;
import com.github.frtu.metrics.micrometer.aop.TimerSpanAspect;
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

        // Aligned with https://grafana.com/grafana/dashboards/4701
        LOGGER.info("METRICS - Adding application='{}' to MeterRegistry", applicationName);
        return registry -> registry.config()
                .commonTags("application", applicationName);
    }

    /**
     * Creating AOP aspect watching for {@link io.micrometer.core.annotation.Timed}
     *
     * @param registry {@link MeterRegistry}
     * @return TimedAspect
     */
    @Bean
    @Conditional(AopConditionalOnClass.class)
    TimedAspect timedAspect(MeterRegistry registry) {
        LOGGER.debug("Activate @Annotation io.micrometer.core.annotation.Timed using TimedAspect");
        return new TimedAspect(registry);
    }

    /**
     * Creating AOP aspect watching for {@link com.github.frtu.logs.tracing.annotation.ExecutionSpan}
     *
     * @param registry {@link MeterRegistry}
     * @return TimerSpanAspect
     * @since 0.9.6
     */
    @Bean
    @Conditional(AopConditionalOnClass.class)
    TimerSpanAspect timerSpanAspect(MeterRegistry registry) {
        LOGGER.debug("Activate @Annotation com.github.frtu.logs.tracing.annotation.ExecutionSpan using TimerSpanAspect");
        return new TimerSpanAspect(registry);
    }
}