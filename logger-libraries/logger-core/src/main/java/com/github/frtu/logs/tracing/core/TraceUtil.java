package com.github.frtu.logs.tracing.core;

import io.opentracing.Span;

/**
 * Utility class for Trace
 *
 * @author fred
 * @since 0.9.0
 */
public interface TraceUtil {
    /**
     * Extract a Trace ID from a {@link Span}
     *
     * @param span the current span
     * @return Trace ID
     */
    String getTraceId(Span span);
}
