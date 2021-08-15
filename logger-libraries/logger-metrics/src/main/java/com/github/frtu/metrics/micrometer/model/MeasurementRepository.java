package com.github.frtu.metrics.micrometer.model;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

/**
 * Entrypoint for all Measurement classes for micrometer
 *
 * @author Frédéric TU
 * @since 1.1.3
 */
public class MeasurementRepository {
    private MeterRegistry registry;

    public MeasurementRepository(MeterRegistry registry) {
        this.registry = registry;
    }

    public Measurement getMeasurement(String operationName, String operationDescription, Iterable<Tag> tags) {
        final Measurement measurement = new Measurement(registry, operationName);
        measurement.setOperationDescription(operationDescription);
        measurement.setTags(tags);
        return measurement;
    }

    public MeasurementHandle getMeasurementHandle(String operationName, String operationDescription, Iterable<Tag> tags) {
        Measurement measurement = getMeasurement(operationName, operationDescription, tags);
        return new MeasurementHandle(measurement);
    }
}
