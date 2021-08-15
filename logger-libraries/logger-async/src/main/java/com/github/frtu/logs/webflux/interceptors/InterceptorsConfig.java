package com.github.frtu.logs.webflux.interceptors;

import com.github.frtu.metrics.config.MetricsConfig;
import com.github.frtu.metrics.micrometer.model.MeasurementRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Enable webflux interceptor and metrics
 *
 * @author Frédéric TU
 * @since 1.1.3
 */
@Configuration
public class InterceptorsConfig {
    @Configuration
    @ConditionalOnBean(MeasurementRepository.class)
    @Import(MetricsConfig.class)
    public class MeasurementInterceptorsConfig {
        @Bean
        MetricsExchangeFilterFunction metricsExchangeFilterFunction(MeasurementRepository measurementRepository) {
            return new MetricsExchangeFilterFunction(measurementRepository);
        }
    }
}
