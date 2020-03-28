package com.github.frtu.logs.tracing.annotation;

import com.github.frtu.logs.tracing.core.TraceHelper;
import com.github.frtu.logs.tracing.core.TraceUtil;
import com.github.frtu.spring.annotation.AnnotationMethodScan;
import com.github.frtu.spring.annotation.AnnotationMethodScanner;
import com.google.common.collect.ImmutableMap;
import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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
@Slf4j
@Aspect
@Component
public class ExecutionSpanAspect {
    public static final String MDC_KEY_TRACE_ID = "TRACE_ID";

    @Value("${trace.full.classname:#{environment.TRACE_FULL_CLASSNAME ?: false}}")
    boolean isFullClassName;

    @Autowired
    private TraceHelper traceHelper;

    @Autowired
    private TraceUtil traceUtil;

    private final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToLog>> scanner = AnnotationMethodScanner.of(ExecutionSpan.class, ToLog.class);

    @Around("@annotation(ExecutionSpan)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final Signature joinPointSignature = joinPoint.getSignature();
        String signatureName = getName(joinPointSignature, isFullClassName);

        try (Scope scope = traceHelper.getTracer().buildSpan(signatureName).startActive(true)) {
            final String traceId = traceUtil.getTraceId(scope.span());
            LOGGER.debug("Creating span around signature={} and traceId={}", signatureName, traceId);
            try (var ignored = MDC.putCloseable(MDC_KEY_TRACE_ID, traceId)) {
                if (joinPointSignature instanceof MethodSignature) {
                    final Method method = ((MethodSignature) joinPointSignature).getMethod();
                    final Object[] args = joinPoint.getArgs();

                    enrichSpanWithTagsAndLogs(scope.span(), method, args);
                }

                try {
                    return joinPoint.proceed();
                } catch (Exception e) {
                    // Non intrusive : log and propagate
                    traceHelper.flagError(e.getMessage());
                    throw e;
                }
            }
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
        if (!annotationMethodScan.isEmpty()) {
            final Tag[] tagsArray = (Tag[]) annotationMethodScan.getAnnotationValueArray();
            final Annotation[] scanParamAnnotations = annotationMethodScan.getParamAnnotations();

            for (Tag tag : tagsArray) {
                final String tagName = tag.tagName();
                final String tagValue = tag.tagValue();
                LOGGER.trace("Add tags name={} and value={}", tagName, tagValue);
                span.setTag(tagName, tagValue);
//                span.setBaggageItem("transaction", tagValue);
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
                    LOGGER.warn("scanParamAnnotations name:{} size:{} not equal args size:{}", method.getName(), scanParamAnnotations.length, args.length);
                }
            } else {
                LOGGER.trace("args is NULL");
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
        return getName(joinPointSignature.getDeclaringType(), joinPointSignature.getName(), isFullClassName);
    }

    /**
     * Get span name based on declaringType and methodName
     *
     * @param declaringType   Classname
     * @param methodName      method name
     * @param isFullClassName if should return class canonical name or short name
     * @return String signature name
     * @since 0.9.5
     */
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
