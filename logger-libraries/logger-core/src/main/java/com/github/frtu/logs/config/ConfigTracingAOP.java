package com.github.frtu.logs.config;

import com.github.frtu.logs.config.condition.AopConditionalOnClass;
import com.github.frtu.logs.tracing.annotation.ExecutionSpanAspect;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {ConfigTracingOnly.class, ExecutionSpanAspect.class})
@Conditional(value = AopConditionalOnClass.class)
public class ConfigTracingAOP {
}
