package com.github.frtu.logs.tracing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tag the span using {@link io.opentracing.Span#setTag(String, String)}.
 * <p>
 * MUST BE used in conjunction with {@link ExecutionSpan}
 *
 * @author fred
 * @since 0.9.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tag {
    /**
     * @return Tag name
     */
    String tagName();

    /**
     * @return Tag value
     */
    String tagValue();
}
