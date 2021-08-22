package com.github.frtu.logs.webflux.interceptors;

import com.github.frtu.metrics.micrometer.model.MeasurementHandle;
import com.github.frtu.metrics.micrometer.model.MeasurementRepository;
import io.micrometer.core.instrument.Tag;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTagsProvider;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * For webflux, a {@link ExchangeFilterFunction} that logs all requests calls.
 * <p>
 * Inspired by org.springframework.boot.actuate.metrics.web.reactive.client.MetricsWebClientFilterFunction
 * but creating metrics by URL
 * </p>
 *
 * @author Frédéric TU
 * @since 1.1.3
 */
public class MetricsExchangeFilterFunction implements ExchangeFilterFunction {
    private final static String CONTEXT_KEY_TIMER = "MEASUREMENT_HANDLE";

    private MeasurementRepository measurementRepository;

    public MetricsExchangeFilterFunction(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        MeasurementHandle measurementHandle = measurementRepository.getMeasurementHandle(buildMetricName(request));
        return next.exchange(request)
                .as((responseMono) -> instrumentResponse(request, responseMono))
                .contextWrite(context -> putStartTime(context, measurementHandle));
    }

    private MeasurementHandle getMeasurementHandle(ContextView context) {
        return context.get(CONTEXT_KEY_TIMER);
    }

    private Context putStartTime(Context context, MeasurementHandle measurementHandle) {
        return context.put(CONTEXT_KEY_TIMER, measurementHandle);
    }

    String buildMetricName(ClientRequest request) {
        StringBuilder metricNameBuilder = new StringBuilder();
        metricNameBuilder.append(request.method()).append(":").append(request.url());
        return metricNameBuilder.toString();
    }


    private Mono<ClientResponse> instrumentResponse(ClientRequest request, Mono<ClientResponse> responseMono) {
        final AtomicBoolean responseReceived = new AtomicBoolean();
        return Mono.deferContextual((ctx) -> responseMono.doOnEach((signal) -> {
            if (signal.isOnNext() || signal.isOnError()) {
                responseReceived.set(true);
                getMeasurementHandle(ctx).close();
            }
        }).doFinally((signalType) -> {
            if (!responseReceived.get() && SignalType.CANCEL.equals(signalType)) {
                getMeasurementHandle(ctx).close();
            }
        }));
    }
}