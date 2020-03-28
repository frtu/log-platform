package com.github.frtu.logs.config;

import com.github.frtu.logs.core.DefaultApplicationMetadataFactory;
import com.github.frtu.logs.tracing.core.TraceHelper;
import com.github.frtu.logs.tracing.core.jaeger.JaegerConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Enable {@link TraceHelper}, {@link JaegerConfiguration} and {@link DefaultApplicationMetadataFactory}.
 *
 * @author Frédéric TU
 * @since 1.0.2
 */
@Configuration
@Import(DefaultApplicationMetadataFactory.class)
@ComponentScan(basePackageClasses = {TraceHelper.class})
public class LogConfigTracingOnly {
}
