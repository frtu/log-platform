package com.github.frtu.metrics.micrometer.aop;

import com.github.frtu.logs.core.metadata.ExecutionHelper;
import com.github.frtu.logs.core.metadata.ExecutionSpan;
import com.github.frtu.metrics.micrometer.model.Measurement;
import com.github.frtu.metrics.micrometer.model.MeasurementHandle;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Same as {@link TimedAspect} but allow to run metrics based on {@link ExecutionSpan}.
 *
 * @since 0.9.6
 */
@Aspect
@Slf4j
public class TimerSpanAspect {
    private static ExecutionHelper executionHelper = new ExecutionHelper();

    /**
     * Configure using proprty : metric.full.classname
     * <p>
     * Else use TRACE_FULL_CLASSNAME system env or 'false'
     */
    @Value("${metric.full.classname:#{environment.TRACE_FULL_CLASSNAME ?: false}}")
    boolean isFullClassName;

    private MeterRegistry registry;
    private final Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint;

    /**
     * Create a {@code TimerSpanAspect} instance with {@link Metrics#globalRegistry}.
     */
    public TimerSpanAspect() {
        this(Metrics.globalRegistry);
    }

    public TimerSpanAspect(MeterRegistry registry) {
        this(registry, pjp ->
                Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringTypeName(),
                        "method", pjp.getStaticPart().getSignature().getName())
        );
    }

    public TimerSpanAspect(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint) {
        this.registry = registry;
        this.tagsBasedOnJoinPoint = tagsBasedOnJoinPoint;
    }

    @PostConstruct
    public void init() {
        LOGGER.debug("Init {} with TimerSpanAspect:{}", TimerSpanAspect.class, isFullClassName);
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

    @Around("@annotation(com.github.frtu.logs.tracing.annotation.ExecutionSpan)")
    public Object timedExecutionSpan(ProceedingJoinPoint joinPoint) throws Throwable {
        final Signature joinPointSignature = joinPoint.getSignature();

        String operationName = null;
        String operationDescription = null;
        // Get Name & Description from annotation @ExecutionSpan
        if (joinPointSignature instanceof MethodSignature) {
            final Method method = ((MethodSignature) joinPointSignature).getMethod();
            final ExecutionSpan methodAnnotation = method.getAnnotation(ExecutionSpan.class);
            operationName = methodAnnotation.name();
            operationDescription = methodAnnotation.description();
        }
        // Get Name & Description from method name
        if (StringUtils.isEmpty(operationName)) {
            operationDescription = getName(joinPointSignature, true);
            if (isFullClassName) {
                operationName = operationDescription;
            } else {
                operationName = getName(joinPointSignature, false);
            }
        }
        final Measurement measurement = new Measurement(registry, operationName);
        measurement.setOperationDescription(operationDescription);
        measurement.setTags(tagsBasedOnJoinPoint.apply(joinPoint));

        try (MeasurementHandle handle = new MeasurementHandle(measurement)) {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            throw MeasurementHandle.flagError(ex);
        }
    }
}
