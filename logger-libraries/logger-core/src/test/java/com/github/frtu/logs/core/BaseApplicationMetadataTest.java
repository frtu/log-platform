package com.github.frtu.logs.core;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.github.frtu.logs.core.BaseApplicationMetadata.SYSTEM_PROPERTY_SERVICE_NAME;
import static org.junit.Assert.*;

public class BaseApplicationMetadataTest {
    @Test
    public void getApplicationName() {
        final String serviceName = "service-a";
        System.setProperty(SYSTEM_PROPERTY_SERVICE_NAME, serviceName);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BaseApplicationMetadata.class);
        ctx.registerShutdownHook();

        final BaseApplicationMetadata applicationMetadata = ctx.getBean(BaseApplicationMetadata.class);

        assertEquals(serviceName, applicationMetadata.getApplicationName());
        System.clearProperty(SYSTEM_PROPERTY_SERVICE_NAME);
    }
}