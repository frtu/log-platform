package com.github.frtu.logs.tracing.annotation;

import com.github.frtu.logs.tracing.core.TraceUtil;
import com.github.frtu.spring.annotation.AnnotationMethodScan;
import com.github.frtu.spring.annotation.AnnotationMethodScanner;
import com.google.common.collect.ImmutableMap;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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

    public static final String MDC_KEY_TRACE_ID = "TRACE_ID";

    @Value("${trace.full.classname:#{environment.TRACE_FULL_CLASSNAME ?: false}}")
    boolean isFullClassName;

    @Autowired
    private Tracer tracer;

    @Autowired
    private TraceUtil traceUtil;

    private final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToLog>> scanner = AnnotationMethodScanner.of(ExecutionSpan.class, ToLog.class);

    @Around("@annotation(ExecutionSpan)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final Signature joinPointSignature = joinPoint.getSignature();
        String signatureName = getName(joinPointSignature, isFullClassName);

        try (Scope scope = tracer.buildSpan(signatureName).startActive(true)) {
            final Span span = scope.span();
            if (joinPointSignature instanceof MethodSignature) {
                final Method method = ((MethodSignature) joinPointSignature).getMethod();
                final Object[] args = joinPoint.getArgs();

                enrichSpanWithTagsAndLogs(span, method, args);
            }
            final String traceId = traceUtil.getTraceId(span);
            MDC.put(MDC_KEY_TRACE_ID, traceId);

            LOGGER.debug("Creating span around signature={} and traceId={}", signatureName, traceId);
            return joinPoint.proceed();
        }
    }

    /**
     * Enrich span using annotation {@link Tag} or {@link ToLog} from {@link Method}.
     * For {@link ToLog} also use method runtime argument.
     *
     * @param span   the current span
     * @param method the run method
     * @param args   its arguments
     * @since 0.9.3
     */
    public void enrichSpanWithTagsAndLogs(Span span, Method method, Object[] args) {
        final AnnotationMethodScan annotationMethodScan = scanner.scan(method);
        if (isAnnotationFound(annotationMethodScan)) {
            final Tag[] tagsArray = (Tag[]) annotationMethodScan.getAnnotationValueArray();
            final Annotation[] scanParamAnnotations = annotationMethodScan.getParamAnnotations();

            for (Tag tag : tagsArray) {
                final String tagName = tag.tagName();
                final String tagValue = tag.tagValue();
                LOGGER.trace("Add tags name={} and value={}", tagName, tagValue);
                span.setTag(tagName, tagValue);
            }
            if (args != null) {
                if (args.length == scanParamAnnotations.length) {
                    for (int i = 0; i < scanParamAnnotations.length; i++) {
                        final ToLog scanParamAnnotation = (ToLog) scanParamAnnotations[i];
                        // Skip not annotated parameters
                        if (scanParamAnnotation != null) {
                            final String logName = scanParamAnnotation.value();
                            final Object logValue = args[i];
                            LOGGER.info("Add logs name={} and value={}", logName, logValue);
                            span.log(ImmutableMap.of(logName, logValue));
                        }
                    }
                } else {
                    LOGGER.warn("scanParamAnnotations size:{} not equal args size:{}", scanParamAnnotations.length, args.length);
                }
            } else {
                LOGGER.trace("args is NULL");
            }
        }
    }

    public boolean isAnnotationFound(AnnotationMethodScan annotationMethodScan) {
        return !AnnotationMethodScan.EMPTY.equals(annotationMethodScan);
    }

    public static String getName(Signature joinPointSignature, boolean isFullClassName) {
        return getName(joinPointSignature.getDeclaringType(), joinPointSignature.getName(), isFullClassName);
    }

    public static String getName(Class declaringType, String methodName, boolean isFullClassName) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (isFullClassName) {
            stringBuilder.append(declaringType.getCanonicalName());
        } else {
            stringBuilder.append(declaringType.getSimpleName());
        }
        return stringBuilder.append('.').append(methodName).toString();
    }
}
