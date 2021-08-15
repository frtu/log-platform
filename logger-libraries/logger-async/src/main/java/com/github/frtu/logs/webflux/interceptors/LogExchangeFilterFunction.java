package com.github.frtu.logs.webflux.interceptors;

import com.github.frtu.logs.core.RpcLogger;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.frtu.logs.core.RpcLogger.*;

/**
 * For webflux, a {@link ExchangeFilterFunction} that logs all requests calls.
 *
 * @author Frédéric TU
 * @since 1.1.2
 */
public class LogExchangeFilterFunction implements ExchangeFilterFunction {
    private RpcLogger rpcLogger;
    private Set<String> authorizedHeaderFilter;

    public LogExchangeFilterFunction() {
        this((Set<String>) null);
    }

    public LogExchangeFilterFunction(Set<String> authorizedHeaderFilter) {
        this(LoggerFactory.getLogger(LogExchangeFilterFunction.class), authorizedHeaderFilter);
    }

    public LogExchangeFilterFunction(Logger logger) {
        this(logger, null);
    }

    public LogExchangeFilterFunction(Logger logger, Set<String> authorizedHeaderFilter) {
        this.rpcLogger = RpcLogger.create(logger);
        this.authorizedHeaderFilter = authorizedHeaderFilter;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        Map<String, List<String>> filteredHeaders = filteredHeaders(request.headers());
        val entries = entries(
                client(),
                method(request.method().toString()),
                uri(request.url()),
                requestHeaders(filteredHeaders)
        );
        rpcLogger.info(entries);
        return next.exchange(request);
    }

    private Map<String, List<String>> filteredHeaders(Map<String, List<String>> headers) {
        if (authorizedHeaderFilter == null) {
            return headers;
        }
        Map<String, List<String>> multiValueMap = headers.entrySet().stream()
                .filter(entry -> authorizedHeaderFilter.contains(entry.getKey()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
        return multiValueMap;
    }
}