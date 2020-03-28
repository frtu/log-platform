package com.github.frtu.logs.tracing.core;

import com.google.common.collect.ImmutableMap;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Helper to add {@link Span#log(java.util.Map)} into the current {@link Span}.
 *
 * @author fred
 * @since 0.9.1
 */
@Lazy
@Component
public class TraceHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceHelper.class);

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
     * Add tag to active span
     *
     * @param key   Tag key
     * @param value Tag value
     * @since 1.0.2
     */
    public void addTag(String key, String value) {
        addTag(tracer.activeSpan(), key, value);
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
        LOGGER.debug("Adding tag to span: {}={}", key, value);
        span.setTag(key, value);
    }

    /**
     * Add log to a active span
     *
     * @param key   Log key
     * @param value Log value
     */
    public void addLog(String key, String value) {
        addLog(tracer.activeSpan(), key, value);
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
        LOGGER.debug("Adding log to span: {}={}", key, value);
        span.log(ImmutableMap.of(key, value));
    }

    /**
     * Flag this span as error. OPTIONALLY add an error msg.
     *
     * @param errorMsg can be null
     * @since 0.9.4
     */
    public void flagError(String errorMsg) {
        flagError(tracer.activeSpan(), errorMsg);
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
            flagError(span);
            if (errorMsg != null) {
                span.log(ImmutableMap.of(Fields.EVENT, Tags.ERROR.getKey(), Fields.MESSAGE, errorMsg));
            }
        } else {
            LOGGER.warn("Span MUST NOT be null ! Loosing span info !!");
        }
    }

    /**
     * Tag this span as error.
     *
     * @since 1.0.2
     */
    public void flagError() {
        flagError(tracer.activeSpan());
    }

    /**
     * Flag this span as error.
     *
     * @param span A span
     * @since 1.0.2
     */
    public static void flagError(final Span span) {
        Tags.ERROR.set(span, true);
    }
}
