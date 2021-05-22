package com.github.frtu.logs.tracing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Flag for method parameter to append to {@link io.opentracing.Span#setTag(String, String)}.
 * <p>
 * MUST BE used in conjunction with {@link ExecutionSpan}
 *
 * @author fred
 * @since 1.0.2
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToTag {
    /**
     * @return Name for this Tag
     */
    String value();
}
