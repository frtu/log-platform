package com.github.frtu.logs.tracing.core;

import io.opentracing.Span;

public interface TraceUtil {
    String getTraceId(Span span);
}
