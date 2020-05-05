package com.github.frtu.logs.core;

import ch.qos.logback.more.appenders.marker.MapMarker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.frtu.logs.tracing.core.TraceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
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
    private String prefix;

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
        LOGGER.trace("Add key:{} value:{}", key, value);
        AbstractMap.SimpleImmutableEntry<K, V> entry = new AbstractMap.SimpleImmutableEntry<>(key, value);
        return entry;
    }

    public static Map.Entry[] entries(Map.Entry... entries) {
        return entries;
    }

    /**
     * Create a {@link Map} from the
     *
     * @param entries
     * @return return null if entries is null
     */
    public static <V> Map<String, V> map(Map.Entry<String, V>... entries) {
        return map(null, entries);
    }

    public static <V> Map<String, V> map(String prefix, Map.Entry<String, V>... entries) {
        if (entries == null) {
            LOGGER.trace("entries is null");
            return null;
        }
        LOGGER.trace("Adding entries size:{}", entries.length);

        Set<String> nullValues = new HashSet<>();
        final Map<String, V> result = Stream.of(entries)
                .filter(entry -> entry != null)
                .filter(kvEntry -> {
                    // Attention map.merge() cannot support null value
                    if (kvEntry.getValue() == null) {
                        nullValues.add(kvEntry.getKey());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toMap(entry -> {
                            if (prefix == null) {
                                return entry.getKey();
                            } else {
                                return prefix + entry.getKey();
                            }
                        },
                        Map.Entry::getValue,
                        (val1, val2) -> val2,
                        LinkedHashMap::new));
        // Add null value for key back here
        nullValues.forEach(key -> result.put(key, null));
        return result;
    }

    public static <V> Map<String, V> unmodifiableMap(Map.Entry<String, V>... entries) {
        return unmodifiableMap(null, entries);
    }

    public static <V> Map<String, V> unmodifiableMap(String prefix, Map.Entry<String, V>... entries) {
        if (entries == null) {
            LOGGER.trace("entries is null");
            return null;
        }
        return Collections.unmodifiableMap(map(prefix, entries));
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
        return create(clazz, null);
    }

    public static StructuredLogger create(Class<?> clazz, String... prefixes) {
        return create(LoggerFactory.getLogger(clazz), prefixes);
    }

    public static StructuredLogger create(String loggerName) {
        return create(loggerName, null);
    }

    public static StructuredLogger create(String loggerName, String... prefixes) {
        return create(LoggerFactory.getLogger(loggerName), prefixes);
    }

    public static StructuredLogger create(Logger logger) {
        return create(logger, null);
    }

    public static StructuredLogger create(Logger logger, String... prefixes) {
        return new StructuredLogger(logger, prefixes);
    }

    protected StructuredLogger(Logger logger, String... prefixes) {
        this.logger = logger;
        if (!ArrayUtils.isEmpty(prefixes)) {
            this.prefix = String.join(".", prefixes) + ".";
        }
    }

    public void trace(Map.Entry... entries) {
        trace((TraceHelper) null, entries);
    }

    public void trace(TraceHelper traceHelper, Map.Entry... entries) {
        trace(traceHelper, BASE_FORMAT, entries);
    }

    public void trace(Map.Entry[] entryArray, Map.Entry... entries) {
        trace((TraceHelper) null, entryArray, entries);
    }

    public void trace(TraceHelper traceHelper, Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        trace(traceHelper, allEntries);
    }

    public void trace(TraceHelper traceHelper, String format, Map.Entry... entries) {
        if (this.logger.isTraceEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            logTracer(traceHelper, map);
            this.logger.trace(new MapMarker("", map), format, getJson(map));
        }
    }

    public void debug(Map.Entry... entries) {
        debug((TraceHelper) null, entries);
    }

    public void debug(TraceHelper traceHelper, Map.Entry... entries) {
        debug(traceHelper, BASE_FORMAT, entries);
    }

    public void debug(Map.Entry[] entryArray, Map.Entry... entries) {
        debug((TraceHelper) null, entryArray, entries);
    }

    public void debug(TraceHelper traceHelper, Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        debug(traceHelper, allEntries);
    }

    public void debug(TraceHelper traceHelper, String format, Map.Entry... entries) {
        if (this.logger.isDebugEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            logTracer(traceHelper, map);
            this.logger.debug(new MapMarker("", map), format, getJson(map));
        }
    }

    public void info(Map.Entry... entries) {
        info((TraceHelper) null, entries);
    }

    public void info(TraceHelper traceHelper, Map.Entry... entries) {
        info(traceHelper, BASE_FORMAT, entries);
    }

    public void info(Map.Entry[] entryArray, Map.Entry... entries) {
        info((TraceHelper) null, entryArray, entries);
    }

    public void info(TraceHelper traceHelper, Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        info(traceHelper, allEntries);
    }

    public void info(TraceHelper traceHelper, String format, Map.Entry... entries) {
        if (this.logger.isInfoEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            logTracer(traceHelper, map);
            this.logger.info(new MapMarker("", map), format, getJson(map));
        }
    }

    public void warn(Map.Entry... entries) {
        warn((TraceHelper) null, entries);
    }

    public void warn(TraceHelper traceHelper, Map.Entry... entries) {
        warn(traceHelper, BASE_FORMAT, entries);
    }

    public void warn(Map.Entry[] entryArray, Map.Entry... entries) {
        warn((TraceHelper) null, entryArray, entries);
    }

    public void warn(TraceHelper traceHelper, Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        warn(traceHelper, allEntries);
    }

    public void warn(TraceHelper traceHelper, String format, Map.Entry... entries) {
        if (this.logger.isWarnEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            logTracer(traceHelper, map);
            this.logger.warn(new MapMarker("", map), format, getJson(map));
        }
    }

    public void error(Map.Entry... entries) {
        error((TraceHelper) null, entries);
    }

    public void error(TraceHelper traceHelper, Map.Entry... entries) {
        error(traceHelper, BASE_FORMAT, entries);
    }

    public void error(Map.Entry[] entryArray, Map.Entry... entries) {
        error((TraceHelper) null, entryArray, entries);
    }

    public void error(TraceHelper traceHelper, Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        error(traceHelper, allEntries);
    }

    public void error(TraceHelper traceHelper, String format, Map.Entry... entries) {
        if (this.logger.isErrorEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            logTracer(traceHelper, map);
            this.logger.error(new MapMarker("", map), format, getJson(map));
        }
    }

    public void logTracer(TraceHelper traceHelper, Map map) {
        if (traceHelper != null && map != null) {
            map.forEach((k, v) -> {
                if (k instanceof String) {
                    if (v instanceof String) {
                        String stringValue = (String) v;
                        traceHelper.addLog((String) k, stringValue);
                    } else if (v instanceof JsonNode) {
                        JsonNode jsonNodeValue = (JsonNode) v;
                        traceHelper.addLog((String) k, jsonNodeValue.asText());
                    } else {
                        LOGGER.info("Impossible to call traceHelper.addLog with VALUE type:[{}]", v.getClass());
                    }
                } else {
                    LOGGER.info("Impossible to call traceHelper.addLog with KEY type:[{}]", k.getClass());
                }
            });
        }
    }
}
