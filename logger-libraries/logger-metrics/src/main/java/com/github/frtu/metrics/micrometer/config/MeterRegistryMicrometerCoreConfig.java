package com.github.frtu.metrics.micrometer.config;

import com.github.frtu.spring.conditional.commons.AopConditionalOnClass;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer registry configuration
 *
 * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics">Sprint-boot Metrics</a>
 * @see <a href="https://spring.io/blog/2018/03/16/micrometer-spring-boot-2-s-new-application-metrics-collector">MeterRegistry</a>
 * @since 1.1.3
 */
@Configuration
public class MeterRegistryMicrometerCoreConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeterRegistryMicrometerCoreConfig.class);

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
}