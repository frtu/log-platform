package com.github.frtu.logs.tracing.annotation;

import com.github.frtu.logs.core.metadata.*;
import com.github.frtu.logs.tracing.core.OpenTelemetryHelper;
import com.github.frtu.spring.annotation.AnnotationMethodScan;
import com.github.frtu.spring.annotation.AnnotationMethodScanner;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Aspect that create a {@link Span} around the annotated method using ExecutionSpan.
 *
 * @author fred
 * @since 0.9.0
 */
@Slf4j
@Aspect
@Component
public class ExecutionSpanAspect {
    private static ExecutionHelper executionHelper = new ExecutionHelper();

    @Value("${trace.full.classname:#{environment.TRACE_FULL_CLASSNAME ?: false}}")
    boolean isFullClassName;

    @Autowired
    private OpenTelemetryHelper traceHelper;

//    @Autowired
//    private TraceUtil traceUtil;

    private final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToLog>> scannerToLog = AnnotationMethodScanner.of(ExecutionSpan.class, ToLog.class);
    private final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToTag>> scannerToTag = AnnotationMethodScanner.of(ExecutionSpan.class, ToTag.class);

    @Around("@annotation(com.github.frtu.logs.core.metadata.ExecutionSpan)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final Signature joinPointSignature = joinPoint.getSignature();
        String signatureName = getName(joinPointSignature, isFullClassName);

        final Span span = traceHelper.startSpan(signatureName);
        // https://opentelemetry.io/docs/java/manual_instrumentation/
        try (Scope scope = span.makeCurrent()) {
//            final String traceId = traceUtil.getTraceId(span);
//            traceHelper.setTraceId(traceId);
//            try (var ignored = MDC.putCloseable(MDC_KEY_TRACE_ID, traceId)) {
            LOGGER.trace("Creating span around signature={}", signatureName);
            if (joinPointSignature instanceof MethodSignature) {
                final Method method = ((MethodSignature) joinPointSignature).getMethod();
                final Object[] args = joinPoint.getArgs();

                enrichSpanWithLogs(span, method, args);
                enrichSpanWithTags(span, method, args);
            }

            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                // Non intrusive : log and propagate
                traceHelper.flagError(e.getMessage());
                throw e;
            } finally {
                // TODO : Clean up MDC when Root Trace
//                MDC.remove(MDC_KEY_TRACE_ID);
            }
        } finally {
            span.end();
        }
    }

    /**
     * Enrich span using annotation ToLog from {@link Method}.
     * For ToLog also use method runtime argument.
     *
     * @param span   the current span
     * @param method the run method
     * @param args   its arguments
     * @since 0.9.3
     */
    public void enrichSpanWithLogs(Span span, Method method, Object[] args) {
        if (ArrayUtils.isNotEmpty(args)) {
            final AnnotationMethodScan annotationMethodScan = scannerToLog.scan(method);
            if (!annotationMethodScan.isEmpty()) {
                final Annotation[] scanParamAnnotations = annotationMethodScan.getParamAnnotations();
                if (args.length >= scanParamAnnotations.length) {
                    for (int i = 0; i < scanParamAnnotations.length; i++) {
                        final ToLog scanParamAnnotation = (ToLog) scanParamAnnotations[i];
                        // Skip not annotated parameters
                        if (scanParamAnnotation != null) {
                            final String logName = scanParamAnnotation.value();
                            final Object logValue = args[i];
                            LOGGER.trace("Add logs name={} and value={}", logName, logValue);
                            traceHelper.addEvent(span,
                                    annotationMethodScan.getMethod().getName(),
                                    logName, logValue.toString()
                            );
                        }
                    }
                } else {
                    LOGGER.warn("scanParamAnnotations name:{} size:{} not equal args size:{}", method.getName(), scanParamAnnotations.length, args.length);
                }
            }
        }
    }

    /**
     * Enrich span using annotation Tag or ToTag from {@link Method}.
     * For ToTag also use method runtime argument.
     *
     * @param span   the current span
     * @param method the run method
     * @param args   its arguments
     * @since 1.0.2
     */
    public void enrichSpanWithTags(Span span, Method method, Object[] args) {
        final AnnotationMethodScan annotationMethodScan = scannerToTag.scan(method);
        if (!annotationMethodScan.isEmpty()) {
            final Tag[] tagsArray = (Tag[]) annotationMethodScan.getAnnotationValueArray();
            final Annotation[] scanParamAnnotations = annotationMethodScan.getParamAnnotations();

            for (Tag tag : tagsArray) {
                final String tagName = tag.tagName();
                final String tagValue = tag.tagValue();
                LOGGER.debug("Add static tags name={} and value={}", tagName, tagValue);
                traceHelper.setAttribute(span, tagName, tagValue);
            }
            if (ArrayUtils.isNotEmpty(args)) {
                if (args.length >= scanParamAnnotations.length) {
                    for (int i = 0; i < scanParamAnnotations.length; i++) {
                        final ToTag scanParamAnnotation = (ToTag) scanParamAnnotations[i];
                        // Skip not annotated parameters
                        if (scanParamAnnotation != null) {
                            final String tagName = scanParamAnnotation.value();
                            final String tagValue = args[i].toString();
                            LOGGER.debug("Add tags name={} and parameter value={}", tagName, tagValue);
                            traceHelper.setAttribute(span, tagName, tagValue);
                        }
                    }
                } else {
                    LOGGER.warn("scanParamAnnotations name:{} size:{} not equal args size:{}", method.getName(), scanParamAnnotations.length, args.length);
                }
            }
        }
    }

    /**
     * Get span name based on joinPointSignature
     *
     * @param joinPointSignature AOP method {@link Signature}
     * @param isFullClassName    if should return class canonical name or short name
     * @return String signature name
     * @since 0.9.5
     */
    public static String getName(Signature joinPointSignature, boolean isFullClassName) {
        return executionHelper.getName(joinPointSignature.getDeclaringType(), joinPointSignature.getName(), isFullClassName);
    }
}
