package com.github.frtu.logs.tracing.core;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Helper to add Span attribute or event into the current {@link Span}.
 *
 * @author fred
 * @since 1.1.4
 */
@Slf4j
public class OpenTelemetryHelper {
    @Autowired
    Tracer tracer;

    /**
     * Allow to get {@link Tracer} when you already have {@link TraceHelper}
     *
     * @return the current Tracer
     * @since 0.9.5
     */
    public Tracer getTracer() {
        return tracer;
    }

    /**
     * Start a new Span
     *
     * @param spanName
     * @return
     */
    public Span startSpan(String spanName) {
        return getTracer().spanBuilder(spanName).startSpan();
    }

    /**
     * Add attribute to the current span
     *
     * @param key   Tag key
     * @param value Tag value
     * @since 1.1.4
     */
    public static void setAttribute(String key, String value) {
        setAttribute(Span.current(), key, value);
    }

    /**
     * Add attribute to a span
     *
     * @param span  A span
     * @param key   Tag key
     * @param value Tag value
     * @since 1.1.4
     */
    public static void setAttribute(Span span, String key, String value) {
        LOGGER.debug("Adding tag to span: {}={}", key, value);
        span.setAttribute(key, value);
    }

    /**
     * Add event to the current span
     *
     * @param eventName
     * @param key
     * @param value
     * @since 1.1.4
     */
    public static void addEvent(String eventName, String key, String value) {
        addEvent(Span.current(), eventName, key, value);
    }

    /**
     * Add event to a span
     *
     * @param span
     * @param eventName
     * @param key
     * @param value
     * @since 1.1.4
     */
    public static void addEvent(Span span, String eventName, String key, String value) {
        LOGGER.debug("Adding log to span: {}={}", key, value);
        span.addEvent(eventName, Attributes.of(AttributeKey.stringKey(key), value));
    }

    /**
     * Tag this span as error.
     *
     * @since 1.0.2
     */
    public void flagError() {
        flagError(Span.current());
    }

    /**
     * Flag this span as error.
     *
     * @param span A span
     * @since 1.0.2
     */
    public static void flagError(final Span span) {
        span.setStatus(StatusCode.ERROR);
    }

    /**
     * Flag this span as error. OPTIONALLY add an error msg.
     *
     * @param errorMsg can be null
     * @since 0.9.4
     */
    public void flagError(String errorMsg) {
        flagError(Span.current(), errorMsg);
    }

    /**
     * Flag this span as error. OPTIONALLY add an error msg.
     *
     * @param span     A span
     * @param errorMsg can be null
     * @since 0.9.4
     */
    public static void flagError(final Span span, final String errorMsg) {
        LOGGER.debug("Adding ERROR to current span. Error message: {}", errorMsg, new Exception(errorMsg));
        if (span != null) {
            if (errorMsg != null) {
                span.setStatus(StatusCode.ERROR, errorMsg);
            } else {
                flagError(span);
            }
        } else {
            LOGGER.warn("Span MUST NOT be null ! Loosing span info !!");
        }
    }
}
