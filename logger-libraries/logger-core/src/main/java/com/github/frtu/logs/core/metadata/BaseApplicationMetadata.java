package com.github.frtu.logs.core.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * Base {@link ApplicationMetadata} provider
 *
 * @author Frédéric TU
 * @since 1.0.2
 */
public class BaseApplicationMetadata implements ApplicationMetadata {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseApplicationMetadata.class);

    public static final String SYSTEM_PROPERTY_SERVICE_NAME = "SERVICE_NAME";
    @Value("${application.name:#{environment." + SYSTEM_PROPERTY_SERVICE_NAME + " ?: 'UNKNOWN'}}")
    private String applicationName;

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @PostConstruct
    public void logs() {
        LOGGER.debug("applicationName:{}", applicationName);
    }
}
