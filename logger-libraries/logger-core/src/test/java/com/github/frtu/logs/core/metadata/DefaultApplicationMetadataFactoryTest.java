package com.github.frtu.logs.core.metadata;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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