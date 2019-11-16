package com.github.frtu.logs.tracing.annotation;

import com.github.frtu.logs.tracing.core.TraceUtil;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Aspect that create a {@link io.opentracing.Span} around the annotated method using {@link ExecutionSpan}.
 *
 * @author fred
 * @since 0.9.0
 */
@Aspect
@Component
public class ExecutionSpanAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionSpanAspect.class);

    public static final String MDC_KEY_TRACE_ID = "trace.id";

    @Value("${trace.full.classname:#{environment.TRACE_FULL_CLASSNAME ?: false}}")
    boolean isFullClassName;

    @Autowired
    private Tracer tracer;

    @Autowired
    private TraceUtil traceUtil;

    @Around("@annotation(ExecutionSpan)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final Class declaringType = joinPoint.getSignature().getDeclaringType();
        String signatureName = getName(declaringType, joinPoint.getSignature().getName());

        try (Scope scope = tracer.buildSpan(signatureName).startActive(true)) {
            final String traceId = traceUtil.getTraceId(scope.span());
            MDC.put(MDC_KEY_TRACE_ID, traceId);

            LOGGER.debug("Creating span around signature={} and traceId={}", signatureName, traceId);
            return joinPoint.proceed();
        }
    }

    String getName(Class declaringType, String methodName) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (isFullClassName) {
            stringBuilder.append(declaringType.getCanonicalName());
        } else {
            stringBuilder.append(declaringType.getSimpleName());
        }
        return stringBuilder.append('.').append(methodName).toString();
    }
}
