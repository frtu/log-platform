package com.github.frtu.logs.config;

import com.github.frtu.logs.tracing.annotation.ExecutionSpanAspect;
import com.github.frtu.spring.conditional.commons.AopConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Enable {@link LogConfigTracingOnly} and {@link ExecutionSpanAspect}.
 *
 * @author Frédéric TU
 * @since 1.0.2
 */
@Configuration
@ComponentScan(basePackageClasses = {LogConfigTracingOnly.class, ExecutionSpanAspect.class})
@Conditional(value = AopConditionalOnClass.class)
public class LogConfigTracingAOP {
}
