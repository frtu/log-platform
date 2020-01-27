package com.github.frtu.logs.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Enable {@link ConfigTracingAOP} and {@link ConfigFluentD}.
 *
 * @author Frédéric TU
 * @since 0.9.5
 */
@Configuration
@ComponentScan(basePackageClasses = {ConfigTracingAOP.class, ConfigFluentD.class})
public class ConfigAll {
}
