package com.github.frtu.metrics.micrometer.model;

import io.micrometer.core.instrument.Timer;
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
    private Timer.Sample timerSample;
    private String exceptionName;

    // start
    public MeasurementHandle(MeasurementSet measurement) {
        this.measurement = measurement;
        this.timerSample = measurement.startExecution();
    }

    public Throwable flagError(Throwable ex) {
        flagError(ex.getClass().getSimpleName());
        return ex;
    }

    public void flagError(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    @Override
    // stop
    public void close() {
        final String exceptionName = (this.exceptionName != null) ? this.exceptionName : readError();
        measurement.stopExecution(exceptionName, this.timerSample);
    }

    public static String readError() {
        return MDC.get(EXCEPTION_TAG);
    }
}
