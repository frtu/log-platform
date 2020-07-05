package com.github.frtu.metrics.micrometer.model;

import org.slf4j.MDC;

import static io.micrometer.core.aop.TimedAspect.EXCEPTION_TAG;

public class MeasurementHandle implements AutoCloseable {
    private Measurement measurement;

    public MeasurementHandle(Measurement measurement) {
        this.measurement = measurement;
        measurement.startExecution();
    }

    public static Throwable flagError(Throwable ex) {
        flagError(ex.getClass().getSimpleName());
        return ex;
    }

    public static void flagError(String exceptionName) {
        MDC.put(EXCEPTION_TAG, exceptionName);
    }

    @Override
    public void close() {
        final String exceptionName = MDC.get(EXCEPTION_TAG);
        measurement.stopExecution(exceptionName);
    }
}
