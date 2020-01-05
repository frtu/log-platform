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
    public static final String LOG_KEY_ERROR_MSG = "error";

    @Autowired
    Tracer tracer;

    public void addLog(String key, String value) {
        LOGGER.debug("Adding to current span: {}={}", key, value);
        tracer.activeSpan().log(ImmutableMap.of(key, value));
    }

    public void flagError(String errorMsg) {
        flagError(tracer.activeSpan(), errorMsg);
    }

    public void flagError(final Span activeSpan, final String errorMsg) {
        LOGGER.error("Adding ERROR to current span. Error message: {}", errorMsg, new Exception(errorMsg));
        if (activeSpan != null) {
            activeSpan.setTag("error", true);
            if (errorMsg != null) {
                activeSpan.log(ImmutableMap.of(LOG_KEY_ERROR_MSG, errorMsg));
            }
        } else {
            LOGGER.error("Span MUST NOT be null ! Loosing span info !!");
        }
    }
}
