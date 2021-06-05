package com.github.frtu.logs.core;

import ch.qos.logback.more.appenders.marker.MapMarker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.frtu.logs.utils.jackson.ObjectMapperLifecycleManager;
import com.github.frtu.logs.utils.jackson.ObjectMapperHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Highly inspired from {@link net.logstash.logback.argument.StructuredArguments}.
 *
 * @author Frédéric TU
 * @since 1.0.2
 */
@Slf4j
public class StructuredLogger {
    public static final String BASE_FORMAT = "{}";

    /**
     * Generic key for message
     *
     * @since 1.1.1
     */
    public static final String KEY_MESSAGE = "message";

    /**
     * Generic key for any unique ID that characterize the current execution (order ID)
     *
     * @since 1.1.1
     */
    public static final String KEY_FLOW_ID = "flow_id";

    /**
     * Generic key for which execution flow it is relevant (order pizza)
     *
     * @since 1.1.1
     */
    public static final String KEY_FLOW = "flow";

    /**
     * Indicate the current phase the RPC call is at (init, sending, sent, ..)
     *
     * @since 1.1.1
     */
    public static final String KEY_PHASE = "phase";

    /**
     * A specific message
     *
     * @param message any human readable text
     * @return log entry pair
     * @since 1.1.1
     */
    public static Map.Entry<String, String> message(String message) {
        return entry(KEY_MESSAGE, message);
    }

    /**
     * Any unique ID that characterize the current execution (order ID)
     *
     * @param flowId any unique ID that characterize the current execution (order ID)
     * @return log entry pair
     * @since 1.1.1
     */
    public static Map.Entry<String, String> flowId(String flowId) {
        return entry(KEY_FLOW_ID, flowId);
    }

    /**
     * Current flow the execution is relevant. Means to be always the same during a phase
     *
     * @param flow current execution flow (init, sending, sent, ..)
     * @return log entry pair
     * @since 1.1.1
     */
    public static Map.Entry<String, String> flow(String flow) {
        return entry(KEY_FLOW, flow);
    }

    /**
     * Mark the current phase
     *
     * @param phase Phase on which the current execution is at (init, sending, sent, ..)
     * @return log entry pair
     * @since 1.1.1
     */
    public static Map.Entry<String, String> phase(String phase) {
        return entry(KEY_PHASE, phase);
    }

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
     * @param <V>     Ability to specify the Type for all the log entries
     * @param entries All the log entry pair
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
        trace(BASE_FORMAT, entries);
    }

    public void trace(Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        trace(BASE_FORMAT, allEntries);
    }

    public void trace(String format, Map.Entry... entries) {
        if (this.logger.isTraceEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            this.logger.trace(new MapMarker("", map), format, getJson(map));
        }
    }

    public void debug(Map.Entry... entries) {
        debug(BASE_FORMAT, entries);
    }

    public void debug(Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        debug(BASE_FORMAT, allEntries);
    }

    public void debug(String format, Map.Entry... entries) {
        if (this.logger.isDebugEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            this.logger.debug(new MapMarker("", map), format, getJson(map));
        }
    }

    public void info(Map.Entry... entries) {
        info(BASE_FORMAT, entries);
    }

    public void info(Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        info(BASE_FORMAT, allEntries);
    }

    public void info(String format, Map.Entry... entries) {
        if (this.logger.isInfoEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            this.logger.info(new MapMarker("", map), format, getJson(map));
        }
    }

    public void warn(Map.Entry... entries) {
        warn(BASE_FORMAT, entries);
    }

    public void warn(Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        warn(BASE_FORMAT, allEntries);
    }

    public void warn(String format, Map.Entry... entries) {
        if (this.logger.isWarnEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            this.logger.warn(new MapMarker("", map), format, getJson(map));
        }
    }

    public void error(Map.Entry... entries) {
        error(BASE_FORMAT, entries);
    }

    public void error(Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        error(BASE_FORMAT, allEntries);
    }

    public void error(String format, Map.Entry... entries) {
        if (this.logger.isErrorEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            this.logger.error(new MapMarker("", map), format, getJson(map));
        }
    }

    private String getJson(Map map) {
        String data;
        try {
            data = HOLDER.getObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            data = map.toString();
        }
        return data;
    }

    /**
     * Create a KV {@link Map.Entry} for JSON value
     *
     * @param key   Key for this entry (support null)
     * @param value Json value for this entry (support null)
     * @param <V>   Value type
     * @return null if one of key or value is null
     * @since 1.1.1
     */
    // Following https://github.com/FasterXML/jackson-docs/wiki/Presentation:-Jackson-Performance
    public static <V> Map.Entry<String, Object> entryJsonNode(String key, V value) {
        try {
            JsonNode jsonNode = HOLDER.getObjectMapper().convertValue(value, JsonNode.class);
            return entry(key, jsonNode);
        } catch (IllegalArgumentException e) {
            // purpose is to generate a log, so should only log at a DEBUG level if need to troubleshoot
            LOGGER.debug("Input parameter cannot be transformed to JsonNode : {}", value, e);
            return entry(key, value);
        }
    }

    /**
     * Holder to access {@link ObjectMapper}
     *
     * @since 1.1.1
     */
    private static ObjectMapperHolder HOLDER = new ObjectMapperLifecycleManager().getObjectMapperHolder();

    protected Logger logger;
    protected String prefix;
}
