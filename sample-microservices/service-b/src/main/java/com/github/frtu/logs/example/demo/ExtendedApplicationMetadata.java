package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.core.ApplicationMetadata;
import com.github.frtu.logs.core.DefaultApplicationMetadataFactory;
import org.springframework.stereotype.Component;

@Component(DefaultApplicationMetadataFactory.BEAN_NAME)
public class ExtendedApplicationMetadata implements ApplicationMetadata {
    @Override
    public String getApplicationName() {
        return "ExtendedApplicationMetadata";
    }
}