package com.github.frtu.logs.tracing.core;

import io.opentelemetry.api.trace.Tracer;

/**
 * Utility class for Trace
 *
 * @author fred
 * @since 0.9.0
 */
public interface TraceUtil {
    /**
     * Allow to get {@link Tracer} when you already have {@link TraceHelper}
     *
     * @return the current Tracer
     * @since 0.9.5
     */
    Tracer getTracer();
}
