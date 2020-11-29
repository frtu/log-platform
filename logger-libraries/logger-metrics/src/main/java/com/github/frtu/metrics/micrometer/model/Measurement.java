package com.github.frtu.metrics.micrometer.model;

import io.micrometer.core.instrument.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import static io.micrometer.core.aop.TimedAspect.EXCEPTION_TAG;

@Slf4j
public class Measurement {
    public static final String COUNTER_SUFFIX_EXEC = "_count";
    public static final String COUNTER_SUFFIX_FAILURE = "_failure";

    @Getter
    private final String metricName;

    @Getter
    private final String operationName;

    @Getter
    @Setter
    private String operationDescription;

    @Getter
    @Setter
    private Iterable<Tag> tags;

    private final MeterRegistry registry;
    private final Counter executionCounter;
    private Timer.Sample timerSample;

    public Measurement(MeterRegistry registry, String operationName) {
        this(registry, operationName, null);
    }

    public Measurement(MeterRegistry registry, String operationName, String metricName) {
        this.registry = registry;
        this.metricName = metricName;
        this.operationName = operationName;
        executionCounter = registry.counter(buildMetricName(COUNTER_SUFFIX_EXEC));
    }

    public String buildMetricName(String... suffixes) {
        StringBuilder metricNameBuilder = new StringBuilder();
        if (!StringUtils.isEmpty(metricName)) {
            metricNameBuilder.append(metricName);
        } else {
            metricNameBuilder.append(operationName);
        }
        if (suffixes != null) {
            for (String suffix : suffixes) {
                metricNameBuilder.append('.').append(suffix);
            }
        }
        return metricNameBuilder.toString();
    }

    public void startExecution() {
        this.timerSample = Timer.start(registry);
    }


    public void stopExecution() {
        stopExecution(null);
    }

    public void stopExecution(String exceptionName) {
        try {
            LOGGER.trace("Stop Timer for {} with exception:{}", operationName, exceptionName);
            counter(exceptionName).increment();

            if (timerSample != null) {
                final long durationInNS = this.timerSample.stop(timer(exceptionName, tags));
                LOGGER.trace("Added duration:{}", durationInNS);
            }
        } catch (Exception e) {
            // ignoring on purpose
            LOGGER.debug("Error in stopExecution msg:{}", e.getMessage(), e);
        }
    }

    protected Counter counter(String exceptionName) {
        if (StringUtils.isEmpty(exceptionName)) {
            return this.executionCounter;
        } else {
            return registry.counter(
                    buildMetricName(COUNTER_SUFFIX_FAILURE),
                    Tags.of("type", exceptionName));
        }
    }

    protected Timer timer(String exceptionName, Iterable<Tag> tags) {
        final Timer.Builder builder = Timer.builder(buildMetricName()).description(operationDescription);
        if (!StringUtils.isEmpty(metricName)) {
            builder.tag("operation", operationName);
        }
        return builder
                .tags(EXCEPTION_TAG, (exceptionName != null) ? exceptionName : "none")
                .tags(tags)
                .register(registry);
    }
}
