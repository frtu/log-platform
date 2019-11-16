package com.github.frtu.logs.tracing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add a new {@link io.opentracing.Span} into current {@link io.opentracing.Tracer}.
 *
 * @author fred
 * @since 0.9.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ExecutionSpan {
    /**
     * Enrich the span with Tags using annotation {@link Tag}
     *
     * @return Array of Tag(K,V)
     * @since 0.9.1
     */
    Tag[] value() default {};
}
