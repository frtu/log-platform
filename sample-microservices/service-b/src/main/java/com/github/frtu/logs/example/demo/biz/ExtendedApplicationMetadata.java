package com.github.frtu.logs.example.demo.biz;

import com.github.frtu.logs.core.metadata.ApplicationMetadata;
import com.github.frtu.logs.core.metadata.DefaultApplicationMetadataFactory;
import org.springframework.stereotype.Component;

@Component(DefaultApplicationMetadataFactory.BEAN_NAME)
public class ExtendedApplicationMetadata implements ApplicationMetadata {
    @Override
    public String getApplicationName() {
        return "service-b-extended";
    }
}