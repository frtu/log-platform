package com.github.frtu.logs.tracing.core;

import com.google.common.collect.ImmutableMap;
import io.opentracing.Span;
import io.opentracing.Tracer;
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

    /**
     * @see <a href="https://github.com/opentracing/specification/blob/master/semantic_conventions.md#span-tags-table">OpenTracing Tag Semantic</a>
     */
    public static final String TAG_KEY_ERROR_FLAG = "error";

    /**
     * @see <a href="https://github.com/opentracing/specification/blob/master/semantic_conventions.md#captured-errors">OpenTracing Error Semantic</a>
     */
    public static final String LOG_KEY_EVENT = "event";
    public static final String LOG_KEY_ERROR_MSG = "message";

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

    public void addLog(String key, String value) {
        LOGGER.info("Adding to current span: {}={}", key, value);
        tracer.activeSpan().log(ImmutableMap.of(key, value));
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
     * @param activeSpan the active span
     * @param errorMsg   can be null
     * @since 0.9.4
     */
    public void flagError(final Span activeSpan, final String errorMsg) {
        LOGGER.error("Adding ERROR to current span. Error message: {}", errorMsg, new Exception(errorMsg));
        if (activeSpan != null) {
            activeSpan.setTag(TAG_KEY_ERROR_FLAG, true);
            if (errorMsg != null) {
                activeSpan.log(ImmutableMap.of(LOG_KEY_EVENT, TAG_KEY_ERROR_FLAG, LOG_KEY_ERROR_MSG, errorMsg));
            }
        } else {
            LOGGER.error("Span MUST NOT be null ! Loosing span info !!");
        }
    }
}
