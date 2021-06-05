package com.github.frtu.logs.utils.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Basic holder that always return the same {@link ObjectMapper}
 *
 * @author Frédéric TU
 * @since 1.1.1
 */
public class BaseObjectMapperHolder implements ObjectMapperHolder {
    private ObjectMapper objectMapper;

    public BaseObjectMapperHolder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }
}
