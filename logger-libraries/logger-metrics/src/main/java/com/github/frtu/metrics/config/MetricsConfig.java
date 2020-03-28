package com.github.frtu.metrics.config;

import com.github.frtu.metrics.micrometer.config.MeterRegistryConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Enable {@link MeterRegistryConfig}.
 *
 * @author Frédéric TU
 * @since 1.0.2
 */
@Configuration
@Import(MeterRegistryConfig.class)
public class MetricsConfig {
}
