package com.github.frtu.logs.tracing.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.frtu.logs.core.StructuredLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Highly inspired from {@link net.logstash.logback.argument.StructuredArguments},
 * but adapt it to {@link ch.qos.logback.more.appenders.DataFluentAppender}.
 *
 * @author Frédéric TU
 * @since 1.1.0
 */
@Slf4j
public class StructuredLoggerTracer extends StructuredLogger {
    public static StructuredLoggerTracer create(Class<?> clazz) {
        return create(clazz, null);
    }

    public static StructuredLoggerTracer create(Class<?> clazz, String... prefixes) {
        return create(LoggerFactory.getLogger(clazz), prefixes);
    }

    public static StructuredLoggerTracer create(String loggerName) {
        return create(loggerName, null);
    }

    public static StructuredLoggerTracer create(String loggerName, String... prefixes) {
        return create(LoggerFactory.getLogger(loggerName), prefixes);
    }

    public static StructuredLoggerTracer create(Logger logger) {
        return create(logger, null);
    }

    public static StructuredLoggerTracer create(Logger logger, String... prefixes) {
        return new StructuredLoggerTracer(logger, prefixes);
    }

    protected StructuredLoggerTracer(Logger logger, String... prefixes) {
        super(logger, prefixes);
    }

    public void trace(TraceHelper traceHelper, Map.Entry... entries) {
        trace(traceHelper, BASE_FORMAT, entries);
    }

    public void trace(TraceHelper traceHelper, Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        trace(traceHelper, allEntries);
    }

    public void trace(TraceHelper traceHelper, String format, Map.Entry... entries) {
        if (logger.isTraceEnabled()) {
            final Map map = unmodifiableMap(prefix, entries);
            logTracer(traceHelper, map);
            super.trace(format, entries);
        }
    }

    public void debug(TraceHelper traceHelper, Map.Entry... entries) {
        debug(traceHelper, BASE_FORMAT, entries);
    }

    public void debug(TraceHelper traceHelper, Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        debug(traceHelper, allEntries);
    }

    public void debug(TraceHelper traceHelper, String format, Map.Entry... entries) {
        if (this.logger.isDebugEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            logTracer(traceHelper, map);
            super.debug(format, entries);
        }
    }

    public void info(TraceHelper traceHelper, Map.Entry... entries) {
        info(traceHelper, BASE_FORMAT, entries);
    }

    public void info(TraceHelper traceHelper, Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        info(traceHelper, allEntries);
    }

    public void info(TraceHelper traceHelper, String format, Map.Entry... entries) {
        if (this.logger.isInfoEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            logTracer(traceHelper, map);
            super.info(format, entries);
        }
    }

    public void warn(TraceHelper traceHelper, Map.Entry... entries) {
        warn(traceHelper, BASE_FORMAT, entries);
    }

    public void warn(TraceHelper traceHelper, Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        warn(traceHelper, allEntries);
    }

    public void warn(TraceHelper traceHelper, String format, Map.Entry... entries) {
        if (this.logger.isWarnEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            if (traceHelper != null) {
                traceHelper.flagError();
                logTracer(traceHelper, map);
            }
            super.warn(format, entries);
        }
    }

    public void error(TraceHelper traceHelper, Map.Entry... entries) {
        error(traceHelper, BASE_FORMAT, entries);
    }

    public void error(TraceHelper traceHelper, Map.Entry[] entryArray, Map.Entry... entries) {
        final Map.Entry[] allEntries = ArrayUtils.addAll(entryArray, entries);
        error(traceHelper, allEntries);
    }

    public void error(TraceHelper traceHelper, String format, Map.Entry... entries) {
        if (this.logger.isErrorEnabled()) {
            final Map map = unmodifiableMap(this.prefix, entries);
            traceHelper.flagError();
            logTracer(traceHelper, map);
            super.error(format, entries);
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
                        logger.info("Impossible to call traceHelper.addLog with VALUE type:[{}]", v.getClass());
                    }
                } else {
                    logger.info("Impossible to call traceHelper.addLog with KEY type:[{}]", k.getClass());
                }
            });
        }
    }
}
