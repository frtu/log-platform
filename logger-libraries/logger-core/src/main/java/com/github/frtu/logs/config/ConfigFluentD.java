package com.github.frtu.logs.config;

import com.github.frtu.logs.core.ApplicationMetadata;
import com.github.frtu.logs.infra.fluentd.FluentdConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {ApplicationMetadata.class, FluentdConfiguration.class})
public class ConfigFluentD {
}
