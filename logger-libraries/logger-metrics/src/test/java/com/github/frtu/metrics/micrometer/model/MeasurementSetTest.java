package com.github.frtu.metrics.micrometer.model;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MeasurementSetTest {
    private MeterRegistry registry = new CompositeMeterRegistry().add(new SimpleMeterRegistry());

    @BeforeEach
    public void beforeEach() {
        registry.clear();
    }

    @Test
    public void testExecution() {
        MeasurementSet measurementSet = new MeasurementSet(registry, "test");
        Timer.Sample execution = measurementSet.startExecution();
        measurementSet.stopExecution(execution);
        Map<String, Meter> meterMap = registry.getMeters().stream().collect(Collectors.toMap(meter -> meter.getId().getName(), Function.identity()));
//        assertThat(meterMap.get("span.test").measure().).isEqualTo()
    }

    @Test
    public void testExecutionFailure() {
        MeasurementSet measurementSet = new MeasurementSet(registry, "test");
        Timer.Sample execution = measurementSet.startExecution();
        measurementSet.stopExecution("IllegalArgumentException", execution);
        List<Meter> meters = registry.getMeters();
        for (Meter meter : meters) {
            System.out.println(meter.getId() + "->" + meter.measure());
        }
    }
}