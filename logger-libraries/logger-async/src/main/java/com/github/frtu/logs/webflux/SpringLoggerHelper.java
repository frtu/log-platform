package com.github.frtu.logs.webflux;

import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.frtu.logs.core.RpcLogger.method;
import static com.github.frtu.logs.core.RpcLogger.uri;
import static com.github.frtu.logs.core.StructuredLogger.entries;
import static com.github.frtu.logs.core.StructuredLogger.join;

public class SpringLoggerHelper {
    public static Map.Entry[] serverRequest(ServerRequest serverRequest) {
        return serverRequest(serverRequest, null);
    }

    public static Map.Entry[] serverRequest(ServerRequest serverRequest, Map.Entry... entries) {
        Map.Entry[] requestEntries = entries(
                method(serverRequest.methodName()),
                uri(serverRequest.uri())
        );
        if (entries == null) {
            return requestEntries;
        } else {
            return join(requestEntries, entries);
        }
    }
}
