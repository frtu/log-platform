package com.github.frtu.logs;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.github.frtu.logs.ApplicationMetadata.SYSTEM_PROPERTY_SERVICE_NAME;
import static org.junit.Assert.*;

public class ApplicationMetadataTest {
    @Test
    public void getApplicationName() {
        final String serviceName = "service-a";
        System.setProperty(SYSTEM_PROPERTY_SERVICE_NAME, serviceName);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationMetadata.class);
        ctx.registerShutdownHook();

        final ApplicationMetadata applicationMetadata = ctx.getBean(ApplicationMetadata.class);

        assertEquals(serviceName, applicationMetadata.getApplicationName());
        System.clearProperty(SYSTEM_PROPERTY_SERVICE_NAME);
    }
}