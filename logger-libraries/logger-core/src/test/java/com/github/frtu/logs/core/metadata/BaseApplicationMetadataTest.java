package com.github.frtu.logs.core.metadata;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.github.frtu.logs.core.metadata.BaseApplicationMetadata.SYSTEM_PROPERTY_SERVICE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

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