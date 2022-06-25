package com.github.frtu.metrics.micrometer.aop;

import com.github.frtu.logs.core.metadata.ExecutionHelper;
import com.github.frtu.logs.core.metadata.ExecutionSpan;
import com.github.frtu.metrics.micrometer.model.MeasurementHandle;
import com.github.frtu.metrics.micrometer.model.MeasurementRepository;
import io.micrometer.core.aop.TimedAspect;
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
 * Allow to integrate with AOP and capture execution steps. Delegate couting to {@link MeasurementHandle}
 * <p>
 * Similar with {@link TimedAspect} but allow to run metrics based on {@link ExecutionSpan}.
 *
 * @since 0.9.6
 */
@Aspect
@Slf4j
public class TimerSpanAspect {
    /**
     * Configure using proprty : metric.full.classname
     * <p>
     * Else use TRACE_FULL_CLASSNAME system env or 'false'
     */
    @Value("${metric.full.classname:#{environment.TRACE_FULL_CLASSNAME ?: false}}")
    boolean isFullClassName;
    private ExecutionHelper executionHelper;
    private final Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint;

    private MeasurementRepository measurementRepository;

    public TimerSpanAspect(MeasurementRepository measurementRepository) {
        this(measurementRepository, pjp ->
                Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringTypeName(),
                        "method", pjp.getStaticPart().getSignature().getName())
        );
    }

    public TimerSpanAspect(MeasurementRepository measurementRepository, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint) {
        this.measurementRepository = measurementRepository;
        this.tagsBasedOnJoinPoint = tagsBasedOnJoinPoint;
    }

    @PostConstruct
    public void init() {
        LOGGER.debug("Init {} with TimerSpanAspect:{}", TimerSpanAspect.class, isFullClassName);
        executionHelper = new ExecutionHelper(isFullClassName);
    }

    @Around("@annotation(com.github.frtu.logs.core.metadata.ExecutionSpan)")
    public Object timedExecutionSpan(ProceedingJoinPoint joinPoint) throws Throwable {
        //-------------------------------
        // Fetch all metadata
        //-------------------------------
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
        if (!StringUtils.hasText(operationName)) {
            operationName = executionHelper.getName(joinPointSignature.getDeclaringType(), joinPointSignature.getName());
        }
        // Get Tags
        Iterable<Tag> tags = tagsBasedOnJoinPoint.apply(joinPoint);

        //-------------------------------
        // Execution
        //-------------------------------
        MeasurementHandle handle = measurementRepository.getMeasurementHandle(operationName, operationDescription, tags);
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            throw handle.flagError(ex);
        } finally {
            handle.close();
        }
        //-------------------------------
    }
}
