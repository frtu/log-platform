package com.github.frtu.logs.core.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generic Method Tag the span with static metadata
 * <p>
 * MUST BE used in conjunction with {@link ExecutionSpan}
 *
 * @author Frédéric TU
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
