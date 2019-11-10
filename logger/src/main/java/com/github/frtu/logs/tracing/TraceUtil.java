package com.github.frtu.logs.tracing;

import io.opentracing.Span;

public interface TraceUtil {
    String getTraceId(Span span);
}
