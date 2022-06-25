package com.github.frtu.logs.tracing.core.jaeger;

import com.github.frtu.logs.tracing.core.OpenTelemetryHelper;
import io.opentelemetry.api.trace.Span;

public final class JaegerExample {
    private final OpenTelemetryHelper openTelemetryHelper;

    public JaegerExample(String applicationName, String jaegerHostName, int jaegerPort) {
        final JaegerConfiguration jaegerConfiguration = new JaegerConfiguration(
                () -> applicationName,
                jaegerHostName,
                jaegerPort,
                "UNKNOWN",
                30000,
                false,
                null
        );
        jaegerConfiguration.initialize();
        openTelemetryHelper = new OpenTelemetryHelper(jaegerConfiguration.getTracer());
    }

    private void myWonderfulUseCase() {
        // Generate a span
        Span span = openTelemetryHelper.startSpan("Start my wonderful use case");
        openTelemetryHelper.addEvent(span, "Event 0");
        // execute my use case - here we simulate a wait
        doWork();
        openTelemetryHelper.addEvent(span, "Event 1");
        openTelemetryHelper.end(span);
    }

    private void doWork() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // do the right thing here
        }
    }

    public static void main(String[] args) {
        final String applicationName = "test-helper";
        String jaegerHostName = "localhost";
        int jaegerPort = 14250;

        // Start the example
        JaegerExample example = new JaegerExample(applicationName, jaegerHostName, jaegerPort);
        // generate a few sample spans
        for (int i = 0; i < 10; i++) {
            example.myWonderfulUseCase();
        }
        System.out.println("Bye");
    }
}