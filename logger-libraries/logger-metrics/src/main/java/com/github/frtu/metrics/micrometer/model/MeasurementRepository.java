package com.github.frtu.metrics.micrometer.model;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.frtu.metrics.micrometer.model.MeasurementSet.MEASUREMENT_PREFIX;

/**
 * Entrypoint for all Measurement classes for micrometer
 *
 * @author Frédéric TU
 * @since 1.1.3
 */
public class MeasurementRepository {
    public static final int DEFAULT_MAX_CACHE_SIZE = 100;
    private Integer maxCacheSize;
    private Map<String, MeasurementSet> repository = new ConcurrentHashMap();

    private MeterRegistry registry;

    /**
     * Check if the current Meter Id is a timer
     *
     * @param id A specific {@link Meter} ID
     * @return If is a Measurement
     */
    public static boolean isMeasurement(Meter.Id id) {
        return id.getType() == Meter.Type.TIMER
                && id.getName().matches("^(" + MEASUREMENT_PREFIX + "){1}.*");
    }

    public MeasurementRepository(MeterRegistry registry) {
        this(registry, DEFAULT_MAX_CACHE_SIZE);
    }

    public MeasurementRepository(MeterRegistry registry, Integer maxCacheSize) {
        this.registry = registry;
        this.maxCacheSize = maxCacheSize;
    }

    /**
     * Create a measurement from operationName.
     *
     * @param operationName Unique identifier for a particular measurement
     * @return Specific measurement
     */
    public MeasurementSet getMeasurement(String operationName) {
        return getMeasurement(operationName, null, null);
    }

    /**
     * Create a measurement from operationName.
     *
     * @param operationName        Unique identifier for a particular measurement
     * @param operationDescription Optional description for this measurement
     * @param tags                 Optional static tags for this measurement
     * @return Specific measurement
     */
    public MeasurementSet getMeasurement(String operationName, String operationDescription, Iterable<Tag> tags) {
        MeasurementSet measurement = repository.get(operationName);
        if (measurement == null) {
            measurement = new MeasurementSet(registry, operationName);
            if (operationDescription != null) {
                measurement.setOperationDescription(operationDescription);
            }
            if (tags != null) {
                measurement.setTags(tags);
            }
            if (repository.size() <= maxCacheSize) {
                repository.put(operationName, measurement);
            }
        }
        return measurement;
    }

    public MeasurementHandle getMeasurementHandle(String operationName) {
        return getMeasurementHandle(operationName, null, null);
    }

    public MeasurementHandle getMeasurementHandle(String operationName, String operationDescription, Iterable<Tag> tags) {
        MeasurementSet measurement = getMeasurement(operationName, operationDescription, tags);
        return new MeasurementHandle(measurement);
    }
}
