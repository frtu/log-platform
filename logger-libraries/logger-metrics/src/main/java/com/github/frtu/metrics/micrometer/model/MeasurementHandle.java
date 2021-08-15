package com.github.frtu.metrics.micrometer.model;

import org.slf4j.MDC;

import static io.micrometer.core.aop.TimedAspect.EXCEPTION_TAG;

/**
 * Adapter for {@link AutoCloseable} class.
 *
 * @author Frédéric TU
 * @since 1.1.3
 */
public class MeasurementHandle implements AutoCloseable {
    private MeasurementSet measurement;

    // start
    public MeasurementHandle(MeasurementSet measurement) {
        this.measurement = measurement;
        measurement.startExecution();
    }

    public static Throwable flagError(Throwable ex) {
        flagError(ex.getClass().getSimpleName());
        return ex;
    }

    public static void flagError(String exceptionName) {
        // Flag an error into MCD so that close would know
        MDC.put(EXCEPTION_TAG, exceptionName);
    }

    @Override
    // stop
    public void close() {
        final String exceptionName = MDC.get(EXCEPTION_TAG);
        measurement.stopExecution(exceptionName);
    }
}
