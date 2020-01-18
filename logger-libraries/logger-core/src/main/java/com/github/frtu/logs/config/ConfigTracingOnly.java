package com.github.frtu.logs.config;

import com.github.frtu.logs.core.ApplicationMetadata;
import com.github.frtu.logs.tracing.core.TraceHelper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {ApplicationMetadata.class, TraceHelper.class})
public class ConfigTracingOnly {
}
