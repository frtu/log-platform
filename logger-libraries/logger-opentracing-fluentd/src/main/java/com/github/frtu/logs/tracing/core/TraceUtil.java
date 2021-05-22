package com.github.frtu.logs.tracing.core;

import io.opentracing.Span;
import io.opentracing.Tracer;

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

    /**
     * Allow to get {@link Tracer} when you already have {@link TraceHelper}
     *
     * @return the current Tracer
     * @since 0.9.5
     */
    Tracer getTracer();

}
