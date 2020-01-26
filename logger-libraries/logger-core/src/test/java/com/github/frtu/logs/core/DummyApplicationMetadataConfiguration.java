package com.github.frtu.logs.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyApplicationMetadataConfiguration {
    public static class DummyApplicationMetadata implements ApplicationMetadata {
        @Override
        public String getApplicationName() {
            return "DummyApplicationMetadataConfiguration";
        }
    }

    @Bean(DefaultApplicationMetadataFactory.BEAN_NAME)
    public ApplicationMetadata applicationMetadata() {
        return new DummyApplicationMetadata();
    }
}
