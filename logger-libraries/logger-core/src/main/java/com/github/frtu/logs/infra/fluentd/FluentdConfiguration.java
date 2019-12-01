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

    @Value("#{environment.FLUENTD_HOST ?: 'UNKNOWN'}")
    private String fluentdHost;

    @Value("#{environment.FLUENTD_PORT ?: 'UNKNOWN'}")
    private String fluentdPort;

    @PostConstruct
    public void logs() {
        LOGGER.info("fluentdHost:{}", fluentdHost);
        LOGGER.info("fluentdPort:{}", fluentdPort);
    }
}
