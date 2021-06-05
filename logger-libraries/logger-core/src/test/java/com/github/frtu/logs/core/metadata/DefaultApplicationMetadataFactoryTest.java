package com.github.frtu.logs.core.metadata;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class DefaultApplicationMetadataFactoryTest {
    @Test
    public void defaultApplicationMetadata() {
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(DefaultApplicationMetadataFactory.class);
        final ApplicationMetadata applicationMetadata = applicationContext.getBean(ApplicationMetadata.class);
        assertNotNull(applicationMetadata);
        assertEquals(BaseApplicationMetadata.class, applicationMetadata.getClass());
    }

    @Test
    public void extendedApplicationMetadata() {
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(DummyApplicationMetadataConfiguration.class, DefaultApplicationMetadataFactory.class);
        final ApplicationMetadata applicationMetadata = applicationContext.getBean(ApplicationMetadata.class);
        assertNotNull(applicationMetadata);
        assertEquals(DummyApplicationMetadataConfiguration.DummyApplicationMetadata.class, applicationMetadata.getClass());
    }
}