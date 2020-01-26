package com.github.frtu.logs.config;

import com.github.frtu.logs.core.DefaultApplicationMetadataFactory;
import com.github.frtu.logs.tracing.core.TraceHelper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DefaultApplicationMetadataFactory.class)
@ComponentScan(basePackageClasses = {TraceHelper.class})
public class ConfigTracingOnly {
}
