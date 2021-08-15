package com.github.frtu.metrics.micrometer.model;

import io.micrometer.core.instrument.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import static io.micrometer.core.aop.TimedAspect.EXCEPTION_TAG;

/**
 * @see <a href="https://ordina-jworks.github.io/microservices/2017/09/17/monitoring-your-microservices-with-micrometer.html">Micrometer API</a>
 */
@Slf4j
public class Measurement {
    public static final String MEASUREMENT_PREFIX = "span";

    public static final String COUNTER_SUFFIX_EXEC = "count";
    public static final String COUNTER_SUFFIX_FAILURE = "failure";

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
    private final Counter totalExecutionCounter;
    private Timer.Sample timerSample;

    public Measurement(MeterRegistry registry, String operationName) {
        this(registry, operationName, null);
    }

    public Measurement(MeterRegistry registry, String operationName, String metricName) {
        this.registry = registry;
        this.metricName = metricName;
        this.operationName = operationName;
        // Build total number counter
        totalExecutionCounter = registry.counter(buildMetricName(COUNTER_SUFFIX_EXEC));
    }

    /**
     * Creating metric name based on Data Model [a-zA-Z_:][a-zA-Z0-9_:]*.
     *
     * @param suffixes List of suffixes for this metric
     * @return Built metric name
     * @see <a href="https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels">Prometheus data_model</a>
     * @see <a href="https://prometheus.io/docs/practices/naming/">Prometheus naming</a>
     */
    public String buildMetricName(String... suffixes) {
        StringBuilder metricNameBuilder = new StringBuilder();
        metricNameBuilder.append(MEASUREMENT_PREFIX).append(".");
        if (StringUtils.hasText(metricName)) {
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
            calculateRatio(exceptionName);

            if (timerSample != null) {
                final long durationInNS = this.timerSample.stop(timer(exceptionName, tags));
                LOGGER.trace("Added duration:{}", durationInNS);
            }
        } catch (Exception e) {
            // ignoring on purpose
            LOGGER.debug("Error in stopExecution msg:{}", e.getMessage(), e);
        }
    }

    protected void calculateRatio(String exceptionName) {
        // Increment total. Ex : span_<operation_name>_count_total
        this.totalExecutionCounter.increment();

        if (StringUtils.hasText(exceptionName)) {
            // Increment failure. Ex : span_<operation_name>_failure_total
            registry.counter(buildMetricName(COUNTER_SUFFIX_FAILURE),
                    Tags.of(EXCEPTION_TAG, exceptionName))
                    .increment();
        }
    }

    protected Timer timer(String exceptionName, Iterable<Tag> tags) {
        return Timer.builder(buildMetricName())
                .description(operationDescription)
                .tags(tags)
                .tags(EXCEPTION_TAG, (exceptionName != null) ? exceptionName : "success")
                .register(registry);
    }
}
