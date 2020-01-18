package com.github.frtu.logs.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ApplicationMetadata {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMetadata.class);

    public static final String SYSTEM_PROPERTY_SERVICE_NAME = "SERVICE_NAME";
    @Value("${application.name:#{environment." + SYSTEM_PROPERTY_SERVICE_NAME + " ?: 'UNKNOWN'}}")
    private String applicationName;

    public String getApplicationName() {
        return applicationName;
    }

    @PostConstruct
    public void logs() {
        LOGGER.info("applicationName:{}", applicationName);
    }
}
