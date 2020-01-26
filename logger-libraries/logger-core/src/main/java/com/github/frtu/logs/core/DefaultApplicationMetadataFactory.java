package com.github.frtu.logs.core;

import com.github.frtu.spring.conditional.LightConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultApplicationMetadataFactory {
    public static final String BEAN_NAME = "applicationMetadata";

    @Bean(BEAN_NAME)
    @Conditional(ApplicationMetadataMissingBean.class)
    public ApplicationMetadata applicationMetadata() {
        return new BaseApplicationMetadata();
    }

    /**
     * Allow to create a new default bean.
     */
    public static class ApplicationMetadataMissingBean extends LightConditionalOnMissingBean {
        public ApplicationMetadataMissingBean() {
            super(BEAN_NAME);
        }
    }
}
