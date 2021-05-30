package com.github.frtu.logs.webflux.interceptors;

import com.github.frtu.logs.core.RpcLogger;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import static com.github.frtu.logs.core.RpcLogger.*;

public class LogExchangeFilterFunction implements ExchangeFilterFunction {
    private RpcLogger rpcLogger;

    public LogExchangeFilterFunction() {
        this(LoggerFactory.getLogger(LogExchangeFilterFunction.class));
    }

    public LogExchangeFilterFunction(Logger logger) {
        this.rpcLogger = RpcLogger.create(logger);
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        val entries = entries(
                client(),
                method(request.method().toString()),
                uri(request.url()),
                requestHeaders(request.headers())
        );
        rpcLogger.info(entries);
        return next.exchange(request);
    }
}