package com.github.frtu.logs.core.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generic Method parameter annotation to flag as Tag
 * <p>
 * MUST BE used in conjunction with {@link ExecutionSpan}
 *
 * @author Frédéric TU
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToTag {
    /**
     * @return Name for this Tag
     */
    String value();
}
