package com.github.frtu.logs.core;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.github.frtu.logs.core.RpcLogger.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RpcLoggerTest {

    @Test
    public void rest() {
        final RpcLogger rpcLogger = RpcLogger.create("rest");
        assertNotNull(rpcLogger);

        rpcLogger.info(client(),
                method("POST"),
                uri("/v1/users/"),
                requestBody("{ \"user\": { \"name\": \"Fred\" }}"),
                responseBody("{ \"id\": \"1234\" }", false),
                statusCode("201")
        );
    }

    @Test
    public void restPrefix() {
        final RpcLogger rpcLogger = RpcLogger.create("rest", "com", "frtu");
        assertNotNull(rpcLogger);

        rpcLogger.info(client(),
                method("POST"),
                uri("/v1/users/"),
                requestBody("{ \"user\": { \"name\": \"Fred\" }}"),
                responseBody("{ \"id\": \"1234\" }", false),
                statusCode("201")
        );
    }

    @Test
    public void graphQl() {
        final RpcLogger rpcLogger = RpcLogger.create("graphQL");
        assertNotNull(rpcLogger);

        // https://graphql.org/learn/queries/#operation-name
        rpcLogger.warn(client(),
                method("Query"),
                uri("/HeroNameAndFriends"),
                statusCode("123"),
                errorMessage("The invitation has expired, please request a new one")
        );
    }


    @Test
    public void phaseEntry() {
        final RpcLogger rpcLogger = RpcLogger.create("graphQL");
        assertNotNull(rpcLogger);

        String uri = "/HeroNameAndFriends";
        String query = "Query";
        UUID requestId = UUID.randomUUID();

        // Debugging steps
        rpcLogger.debug(client(),
                method(query),
                uri(uri),
                requestId(requestId),
                phase("INIT")
        );

        // Debugging steps
        rpcLogger.debug(client(),
                requestId(requestId),
                phase("SENDING")
        );

        // Info success or Warn or Error result
        rpcLogger.warn(client(),
                requestId(requestId),
                phase("SENT"),
                statusCode("123"),
                errorMessage("The invitation has expired, please request a new one")
        );
    }

    @Test
    public void nullStatusCode() {
        final RpcLogger rpcLogger = RpcLogger.create("graphQL");
        assertNotNull(rpcLogger);

        Integer emptyStatusCode = null;

        // Info success or Warn or Error result
        rpcLogger.warn(client(),
                method("Query"),
                uri("/HeroNameAndFriends"),
                requestId(UUID.randomUUID()),
                phase("SENT"),
                statusCode(emptyStatusCode),
                errorMessage("The invitation has expired, please request a new one")
        );
    }
}