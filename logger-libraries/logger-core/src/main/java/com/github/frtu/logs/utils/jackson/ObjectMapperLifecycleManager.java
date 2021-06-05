package com.github.frtu.logs.utils.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Central place to build and manage {@link ObjectMapper} instance. Can return the same instance to all usage or use other strategy like ThreadLocal.
 *
 * @author Frédéric TU
 * @since 1.1.1
 */
public class ObjectMapperLifecycleManager {
    public static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // ignore the null fields globally
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    public ObjectMapperHolder getObjectMapperHolder() {
        return _INSTANCE;
    }

    private static final ObjectMapperHolder _INSTANCE = new BaseObjectMapperHolder(buildObjectMapper());
}
