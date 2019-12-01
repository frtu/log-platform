package com.github.frtu.logs.infra.fluentd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Allow to handle Fluentd Configuration
 *
 * @author fred
 * @since 0.9.2
 */
@Configuration
public class FluentdConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(FluentdConfiguration.class);

    @Value("#{environment.FLUENTD_HOST ?: 'localhost'}")
    private String fluentdHost;

    @Value("#{environment.FLUENTD_PORT ?: '24224'}")
    private String fluentdPort;

    @Value("#{environment.FLUENTD_HEALTH_CHECK_PORT ?: null}")
    private String fluentdHealthCheckPort;

    @PostConstruct
    public void logs() {
        LOGGER.info("fluentdHost:{}", fluentdHost);
        LOGGER.info("fluentdPort:{}", fluentdPort);
        final boolean isHealthCheckActivated = fluentdHealthCheckPort != null;
        if (isHealthCheckActivated) {
            LOGGER.info("Activate fluentd HealthCheck HTTP using port:{}", fluentdHealthCheckPort);
        }
    }
}
