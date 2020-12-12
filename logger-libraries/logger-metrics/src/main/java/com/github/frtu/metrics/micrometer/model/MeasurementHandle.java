package com.github.frtu.metrics.micrometer.model;

import io.micrometer.core.instrument.Meter;
import org.slf4j.MDC;

import static com.github.frtu.metrics.micrometer.model.Measurement.MEASUREMENT_PREFIX;
import static io.micrometer.core.aop.TimedAspect.EXCEPTION_TAG;

public class MeasurementHandle implements AutoCloseable {
    private Measurement measurement;

    public MeasurementHandle(Measurement measurement) {
        this.measurement = measurement;
        measurement.startExecution();
    }

    public static boolean isMeasurement(Meter.Id id) {
        return id.getType() == Meter.Type.TIMER
                && id.getName().matches("^(" + MEASUREMENT_PREFIX + "){1}.*");
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
