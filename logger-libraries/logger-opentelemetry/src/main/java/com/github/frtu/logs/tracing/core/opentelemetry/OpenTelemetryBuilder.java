package com.github.frtu.logs.tracing.core.opentelemetry;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.OpenTelemetrySdkBuilder;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.MetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

/**
 * Help to build OpenTelemetry instrumentation classes
 *
 * @author fred
 * @see <a href="https://opentelemetry.io/docs/instrumentation/java/manual/">OpenTelemetry Manual Instrumentation</a>
 * @since 1.1.5
 */
public class OpenTelemetryBuilder {

    public static SdkTracerProvider buildSdkTracerProvider(String serviceName, SpanProcessor spanProcessor) {
        Resource serviceNameResource = Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, serviceName));

        final SdkTracerProviderBuilder sdkTracerProviderBuilder = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .setResource(Resource.getDefault().merge(serviceNameResource));

        return sdkTracerProviderBuilder.build();
    }

    public static SdkMeterProvider buildSdkMeterProvider(MetricReader reader) {
        final SdkMeterProviderBuilder sdkMeterProviderBuilder = SdkMeterProvider.builder()
                .registerMetricReader(reader);
        return sdkMeterProviderBuilder.build();
    }

    public static OpenTelemetrySdk buildOpenTelemetrySdk(TextMapPropagator propagator, SdkTracerProvider tracerProvider) {
        OpenTelemetrySdkBuilder openTelemetrySdkBuilder = OpenTelemetrySdk.builder()
                .setPropagators(ContextPropagators.create(propagator));

        if (tracerProvider != null) {
            openTelemetrySdkBuilder.setTracerProvider(tracerProvider);
        }

        return openTelemetrySdkBuilder.buildAndRegisterGlobal();
    }
}
