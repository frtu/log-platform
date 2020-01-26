package com.github.frtu.logs.config.condition;

import com.github.frtu.spring.conditional.LightConditionalOnClass;

public class AopConditionalOnClass extends LightConditionalOnClass {
    private static final String ASPECT_CANONICAL_NAME = "org.aspectj.lang.annotation.Aspect";

    public AopConditionalOnClass() {
        super(ASPECT_CANONICAL_NAME);
    }
}
