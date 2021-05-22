package com.github.frtu.logs.core;

import org.junit.Test;

import java.util.UUID;

import static com.github.frtu.logs.core.RpcLogger.*;
import static org.junit.Assert.assertNotNull;

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
        String requestId = UUID.randomUUID().toString();

        // Debugging steps
        rpcLogger.debug(client(),
                method(query),
                uri(uri),
                requestId(requestId),
                phase("INIT")
        );

        // Debugging steps
        rpcLogger.debug(client(),
                method(query),
                uri(uri),
                phase("SENDING")
        );

        // Info success or Warn or Error result
        rpcLogger.warn(client(),
                method(query),
                uri(uri),
                phase("SENT"),
                statusCode("123"),
                errorMessage("The invitation has expired, please request a new one")
        );
    }
}