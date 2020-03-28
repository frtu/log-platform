package com.github.frtu.logs.config;

import com.github.frtu.logs.core.DefaultApplicationMetadataFactory;
import com.github.frtu.logs.infra.fluentd.FluentdConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Enable {@link FluentdConfiguration} and {@link DefaultApplicationMetadataFactory}.
 *
 * @author Frédéric TU
 * @since 1.0.2
 */
@Configuration
@Import(DefaultApplicationMetadataFactory.class)
@ComponentScan(basePackageClasses = {FluentdConfiguration.class})
public class LogConfigFluentD {
}
