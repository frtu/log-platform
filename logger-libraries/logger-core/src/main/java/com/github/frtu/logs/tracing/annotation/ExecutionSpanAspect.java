package com.github.frtu.logs.tracing.annotation;

import com.github.frtu.logs.tracing.core.TraceUtil;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionSpanAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionSpanAspect.class);

    public static final String MDC_KEY_TRACE_ID = "trace.id";

    @Autowired
    private Tracer tracer;

    @Autowired
    private TraceUtil traceUtil;

    @Around("@annotation(ExecutionSpan)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final Signature signature = joinPoint.getSignature();

        String signatureName = new StringBuilder()
                .append(signature.getDeclaringTypeName())
                .append('.')
                .append(signature.getName()).toString();

        try (Scope scope = tracer.buildSpan(signatureName).startActive(true)) {
            final String traceId = traceUtil.getTraceId(scope.span());
            MDC.put(MDC_KEY_TRACE_ID, traceId);
            LOGGER.debug("Creating span around signature={} and traceId={}", signatureName, traceId);
            return joinPoint.proceed();
        }
    }
}
