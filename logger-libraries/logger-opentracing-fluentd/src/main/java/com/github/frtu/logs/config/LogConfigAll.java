package com.github.frtu.logs.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Enable {@link LogConfigTracingAOP} and {@link LogConfigFluentD}.
 *
 * @author Frédéric TU
 * @since 1.0.2
 */
@Configuration
@ComponentScan(basePackageClasses = {LogConfigTracingAOP.class, LogConfigFluentD.class})
public class LogConfigAll {
}
