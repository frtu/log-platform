package com.github.frtu.logs.infra.fluentd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Allow to handle Fluentd Configuration
 *
 * @author fred
 * @since 0.9.2
 */
@Component
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
        LOGGER.info("LOGGING - fluentdHost:'{}', fluentdPort:'{}'", fluentdHost, fluentdPort);
        final boolean isHealthCheckActivated = fluentdHealthCheckPort != null;
        if (isHealthCheckActivated) {
            LOGGER.debug("Activate fluentd HealthCheck HTTP using port:{}", fluentdHealthCheckPort);
            // TBD create empty <appender> if healthcheck cannot be found.
        }
    }
}
