package com.github.frtu.logs.core;

import ch.qos.logback.more.appenders.marker.MapMarker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Highly inspired from {@link net.logstash.logback.argument.StructuredArguments},
 * but adapt it to {@link ch.qos.logback.more.appenders.DataFluentAppender}.
 *
 * @author Frédéric TU
 * @since 1.0.2
 */
public class StructuredLogger {
    public static final String BASE_FORMAT = "{}";

    private ObjectMapper mapper = new ObjectMapper();
    private Logger logger;

    public static <K, V> Map.Entry<K, V> entries(K key, V value) {
        final AbstractMap.SimpleImmutableEntry<K, V> entry = new AbstractMap.SimpleImmutableEntry<>(key, value);
        return entry;
    }

    public static Map map(Map.Entry... entries) {
        return Stream.of(entries)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String getData(Map map) {
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

    public StructuredLogger(Logger logger) {
        this.logger = logger;
    }

    public void trace(Map.Entry... entries) {
        trace(BASE_FORMAT, entries);
    }

    public void trace(String format, Map.Entry... entries) {
        if (this.logger.isTraceEnabled()) {
            final Map map = map(entries);
            this.logger.trace(new MapMarker("", map), format, getData(map));
        }
    }

    public void debug(Map.Entry... entries) {
        debug(BASE_FORMAT, entries);
    }

    public void debug(String format, Map.Entry... entries) {
        if (this.logger.isDebugEnabled()) {
            final Map map = map(entries);
            this.logger.debug(new MapMarker("", map), format, getData(map));
        }
    }

    public void info(Map.Entry... entries) {
        info(BASE_FORMAT, entries);
    }

    public void info(String format, Map.Entry... entries) {
        if (this.logger.isInfoEnabled()) {
            final Map map = map(entries);
            this.logger.info(new MapMarker("", map), format, getData(map));
        }
    }

    public void warn(Map.Entry... entries) {
        warn(BASE_FORMAT, entries);
    }

    public void warn(String format, Map.Entry... entries) {
        if (this.logger.isWarnEnabled()) {
            final Map map = map(entries);
            this.logger.warn(new MapMarker("", map), format, getData(map));
        }
    }

    public void error(Map.Entry... entries) {
        error(BASE_FORMAT, entries);
    }

    public void error(String format, Map.Entry... entries) {
        if (this.logger.isErrorEnabled()) {
            final Map map = map(entries);
            this.logger.error(new MapMarker("", map), format, getData(map));
        }
    }
}
