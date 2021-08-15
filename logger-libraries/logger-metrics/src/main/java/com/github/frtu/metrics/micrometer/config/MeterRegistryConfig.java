package com.github.frtu.metrics.micrometer.config;

import com.github.frtu.logs.core.metadata.ApplicationMetadata;
import com.github.frtu.metrics.micrometer.aop.TimerSpanAspect;
import com.github.frtu.metrics.micrometer.model.MeasurementRepository;
import com.github.frtu.spring.conditional.commons.AopConditionalOnClass;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

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

    @Autowired(required = false)
    private MeterRegistry registry;

    @Value("${application.measurement.cache.max_size:100}")
    private Integer measurementCacheMaxSize;

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(ApplicationMetadata applicationMetadata) {
        final String applicationName = applicationMetadata.getApplicationName();

        // Aligned with https://grafana.com/grafana/dashboards/4701
        LOGGER.info("METRICS - Adding application='{}' to MeterRegistry", applicationName);
        return registry -> registry.config()
                .commonTags("application", applicationName)
                .meterFilter(
                        new MeterFilter() {
                            @Override
                            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                                if (MeasurementRepository.isMeasurement(id)) {
                                    return DistributionStatisticConfig.builder()
                                            .percentilesHistogram(true)
                                            .percentiles(0.5, 0.95, 0.99)
                                            .sla(Duration.ofMillis(50).toNanos(),
                                                    Duration.ofMillis(200).toNanos(),
                                                    Duration.ofSeconds(1).toNanos())
                                            .minimumExpectedValue(Duration.ofMillis(1).toNanos())
                                            .maximumExpectedValue(Duration.ofSeconds(1).toNanos())
                                            .build()
                                            .merge(config);
                                } else {
                                    return config;
                                }
                            }
                        });
    }

    /**
     * Creating AOP aspect watching for {@link com.github.frtu.logs.core.metadata.ExecutionSpan}
     *
     * @return TimerSpanAspect
     * @since 0.9.6
     */
    @Bean
    @Conditional(AopConditionalOnClass.class)
    TimerSpanAspect timerSpanAspect() {
        LOGGER.debug("Activate @Annotation com.github.frtu.logs.tracing.annotation.ExecutionSpan using TimerSpanAspect");
        final MeasurementRepository measurementRepository;
        if (registry == null) {
            measurementRepository = new MeasurementRepository(Metrics.globalRegistry, measurementCacheMaxSize);
        } else {
            measurementRepository = new MeasurementRepository(registry, measurementCacheMaxSize);
        }
        return new TimerSpanAspect(measurementRepository);
    }
}