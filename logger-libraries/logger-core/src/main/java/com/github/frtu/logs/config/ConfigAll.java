package com.github.frtu.logs.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {ConfigTracingAOP.class, ConfigFluentD.class})
public class ConfigAll {
}
