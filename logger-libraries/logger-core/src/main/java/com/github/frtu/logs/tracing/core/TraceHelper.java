package com.github.frtu.logs.tracing.core;

import com.google.common.collect.ImmutableMap;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Helper to add {@link Span#log(Map)} into the current {@link Span}.
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

    public void addLog(String key, String value) {
        LOGGER.debug("Adding to current span: {}={}", key, value);
        tracer.activeSpan().log(ImmutableMap.of(key, value));
    }
}
