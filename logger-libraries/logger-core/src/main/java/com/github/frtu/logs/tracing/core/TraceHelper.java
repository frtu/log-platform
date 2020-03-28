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
        LOGGER.debug("Adding ERROR to current span. Error message: {}", errorMsg, new Exception(errorMsg));
        if (activeSpan != null) {
            flagError(activeSpan);
            if (errorMsg != null) {
                activeSpan.log(ImmutableMap.of(Fields.EVENT, Tags.ERROR.getKey(), Fields.MESSAGE, errorMsg));
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
     * @param activeSpan the active span
     * @since 1.0.2
     */
    public void flagError(final Span activeSpan) {
        Tags.ERROR.set(activeSpan, true);
    }
}
