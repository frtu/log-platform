package com.github.frtu.logs.utils.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Holder to access {@link ObjectMapper}
 *
 * @author Frédéric TU
 * @since 1.1.1
 */
public interface ObjectMapperHolder {
    ObjectMapper getObjectMapper();
}
