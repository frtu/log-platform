package com.github.frtu.logs.config.condition;

import com.github.frtu.spring.conditional.LightConditionalOnClass;
import org.aspectj.lang.annotation.Aspect;

public class AopConditionalOnClass extends LightConditionalOnClass {
    private static final String ASPECT_CANONICAL_NAME = Aspect.class.getCanonicalName();

    public AopConditionalOnClass() {
        super(ASPECT_CANONICAL_NAME);
    }
}
