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
    public static final String SYSTEM_PROPERTY_OBJECTMAPPER_LIFECYCLE_STRATEGY = "OBJECTMAPPER_LIFECYCLE_STRATEGY";
    public static final String LIFECYCLE_STRATEGY_SINGLETON = "SINGLETON";
    public static final String LIFECYCLE_STRATEGY_THREAD_LOCAL = "THREAD_LOCAL";
    public static final String LIFECYCLE_STRATEGY_PER_CALL = "PER_CALL";

    private final static ObjectMapperLifecycleManager _INSTANCE = new ObjectMapperLifecycleManager();

    private ObjectMapperHolder HOLDER;

    public static ObjectMapperLifecycleManager objectMapperLifecycleManager() {
        return _INSTANCE;
    }

    protected ObjectMapperLifecycleManager() {
        // Many thread on singleton or ThreadLocal https://stackoverflow.com/questions/3907929/should-i-declare-jacksons-objectmapper-as-a-static-field
        String objectMapperLifecycleStrategy;
        try {
            objectMapperLifecycleStrategy = System.getProperty(SYSTEM_PROPERTY_OBJECTMAPPER_LIFECYCLE_STRATEGY, LIFECYCLE_STRATEGY_SINGLETON);
        } catch (SecurityException e) {
            objectMapperLifecycleStrategy = LIFECYCLE_STRATEGY_SINGLETON;
        }

        switch (objectMapperLifecycleStrategy) {
            case LIFECYCLE_STRATEGY_THREAD_LOCAL:
                // Allow to create one ObjectMapper per Thread (stored in ThreadLocal)
                HOLDER = new ThreadLocalObjectMapperHolder();
                break;
            case LIFECYCLE_STRATEGY_PER_CALL:
                // Allow to create a new instance every time getObjectMapperHolder() is called
                HOLDER = null;
                break;
            case LIFECYCLE_STRATEGY_SINGLETON:
            default:
                // Allow to create a single instance
                HOLDER = new BaseObjectMapperHolder(buildObjectMapper());
        }
    }

    public ObjectMapperHolder getObjectMapperHolder() {
        if (HOLDER != null) {
            return HOLDER;
        } else {
            return new BaseObjectMapperHolder(buildObjectMapper());
        }
    }

    public static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // ignore the null fields globally
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }
}
