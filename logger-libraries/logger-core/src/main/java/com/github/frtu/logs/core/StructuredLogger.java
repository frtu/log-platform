package com.github.frtu.logs.core;

import ch.qos.logback.more.appenders.marker.MapMarker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Highly inspired from {@link net.logstash.logback.argument.StructuredArguments},
 * but adapt it to {@link ch.qos.logback.more.appenders.DataFluentAppender}.
 *
 * @author Frédéric TU
 * @since 1.0.2
 */
@Slf4j
public class StructuredLogger {
    public static final String BASE_FORMAT = "{}";

    private ObjectMapper mapper = new ObjectMapper();
    private Logger logger;

    /**
     * Create a KV {@link Map.Entry} using parameters.
     *
     * @param key   Key for this entry (support null)
     * @param value Value for this entry (support null)
     * @param <K>   Key type
     * @param <V>   Value type
     * @return null if one of key or value is null
     */
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        LOGGER.trace("Add key:{} value:{{}", key, value);
        AbstractMap.SimpleImmutableEntry<K, V> entry = new AbstractMap.SimpleImmutableEntry<>(key, value);
        return entry;
    }

    /**
     * Create a {@link Map} from the
     *
     * @param entries
     * @return return null if entries is null
     */
    public static <K, V> Map<K, V> map(Map.Entry<K, V>... entries) {
        if (entries == null) {
            LOGGER.trace("entries is null");
            return null;
        }
        LOGGER.trace("Adding entries size:{}", entries.length);

        Set<K> nullValues = new HashSet<>();
        final Map<K, V> result = Stream.of(entries)
                .filter(entry -> entry != null)
                .filter(kvEntry -> {
                    // Attention map.merge() cannot support null value
                    if (kvEntry.getValue() == null) {
                        nullValues.add(kvEntry.getKey());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        // Add null value for key back here
        nullValues.forEach(key -> result.put(key, null));
        return result;
    }

    public static <K, V> Map<K, V> unmodifiableMap(Map.Entry<K, V>... entries) {
        if (entries == null) {
            LOGGER.trace("entries is null");
            return null;
        }
        return Collections.unmodifiableMap(map(entries));
    }

    private String getJson(Map map) {
        String data;
        try {
            data = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            data = map.toString();
        }
        return data;
    }

    public static StructuredLogger create(Class<?> clazz) {
        return create(LoggerFactory.getLogger(clazz));
    }

    public static StructuredLogger create(Logger logger) {
        return new StructuredLogger(logger);
    }

    protected StructuredLogger(Logger logger) {
        this.logger = logger;
    }

    public void trace(Map.Entry... entries) {
        trace(BASE_FORMAT, entries);
    }

    public void trace(String format, Map.Entry... entries) {
        if (this.logger.isTraceEnabled()) {
            final Map map = unmodifiableMap(entries);
            this.logger.trace(new MapMarker("", map), format, getJson(map));
        }
    }

    public void debug(Map.Entry... entries) {
        debug(BASE_FORMAT, entries);
    }

    public void debug(String format, Map.Entry... entries) {
        if (this.logger.isDebugEnabled()) {
            final Map map = unmodifiableMap(entries);
            this.logger.debug(new MapMarker("", map), format, getJson(map));
        }
    }

    public void info(Map.Entry... entries) {
        info(BASE_FORMAT, entries);
    }

    public void info(String format, Map.Entry... entries) {
        if (this.logger.isInfoEnabled()) {
            final Map map = unmodifiableMap(entries);
            this.logger.info(new MapMarker("", map), format, getJson(map));
        }
    }

    public void warn(Map.Entry... entries) {
        warn(BASE_FORMAT, entries);
    }

    public void warn(String format, Map.Entry... entries) {
        if (this.logger.isWarnEnabled()) {
            final Map map = unmodifiableMap(entries);
            this.logger.warn(new MapMarker("", map), format, getJson(map));
        }
    }

    public void error(Map.Entry... entries) {
        error(BASE_FORMAT, entries);
    }

    public void error(String format, Map.Entry... entries) {
        if (this.logger.isErrorEnabled()) {
            final Map map = unmodifiableMap(entries);
            this.logger.error(new MapMarker("", map), format, getJson(map));
        }
    }
}
