package com.github.frtu.logs.core.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A generic execution slice (Span) tied to a method.
 *
 * @author Frédéric TU
 * @since 1.1.3 : Refactored to remove outside dependencies
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ExecutionSpan {
    /**
     * Enrich the span with Tags using annotation {@link Tag}
     *
     * @return Array of Tag(K,V)
     */
    Tag[] value() default {};

    /**
     * Explicit name for this Span
     *
     * @return name for this span
     */
    String name() default "";

    /**
     * Description for this Span
     *
     * @return description for this span
     */
    String description() default "";
}
