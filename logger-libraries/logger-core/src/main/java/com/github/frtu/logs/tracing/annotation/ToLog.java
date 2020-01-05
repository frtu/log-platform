package com.github.frtu.logs.tracing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flag for method parameter to append to {@link io.opentracing.Span#log(java.util.Map)}
 * <p>
 * MUST BE used in conjunction with {@link ExecutionSpan}
 *
 * @author fred
 * @since 0.9.1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToLog {
    /**
     * @return Name for this Log
     */
    String value();
}
