package com.github.frtu.logs.tracing.core;

import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Helper to add Span tags or logs into the current {@link Span}.
 *
 * @author fred
 * @since 0.9.1
 */
@Lazy
@Slf4j
@Component
public class TraceHelper extends OpenTelemetryHelper {
    public static final String MDC_KEY_TRACE_ID = "TRACE_ID";

    public void setTraceId(String traceId) {
        MDC.put(MDC_KEY_TRACE_ID, traceId);
    }

    /**
     * Add tag to active span
     *
     * @param key   Tag key
     * @param value Tag value
     * @since 1.0.2
     */
    public void addTag(String key, String value) {
        addTag(Span.current(), key, value);
    }

    /**
     * Add tag to a span
     *
     * @param span  A span
     * @param key   Tag key
     * @param value Tag value
     * @since 1.0.2
     */
    public static void addTag(Span span, String key, String value) {
        setAttribute(span, key, value);
    }

    /**
     * Add log to a active span
     *
     * @param key   Log key
     * @param value Log value
     */
    public void addLog(String key, String value) {
        addLog(Span.current(), key, value);
    }

    /**
     * Add log to a span
     *
     * @param span  A span
     * @param key   Log key
     * @param value Log value
     * @since 1.0.2
     */
    public static void addLog(Span span, String key, String value) {
        addEvent(span, "no_name", key, value);
    }
}
