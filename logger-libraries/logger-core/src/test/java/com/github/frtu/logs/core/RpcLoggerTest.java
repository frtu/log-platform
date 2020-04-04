package com.github.frtu.logs.core;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import static com.github.frtu.logs.core.RpcLogger.*;
import static org.junit.Assert.assertNotNull;

public class RpcLoggerTest {

    @Test
    public void rest() {
        final RpcLogger rpcLogger = RpcLogger.create(LoggerFactory.getLogger("rest"));
        assertNotNull(rpcLogger);

        rpcLogger.info(client(),
                method("POST"),
                uri("/v1/users/"),
                requestBody("{ \"user\": { \"name\": \"Fred\" }}"),
                responseBody("{ \"id\": \"1234\" }"),
                statusCode("201")
        );
    }

    @Test
    public void graphQl() {
        final RpcLogger rpcLogger = RpcLogger.create(LoggerFactory.getLogger("graphQL"));
        assertNotNull(rpcLogger);

        // https://graphql.org/learn/queries/#operation-name
        rpcLogger.warn(client(),
                method("Query"),
                uri("/HeroNameAndFriends"),
                statusCode("123"),
                errorMessage("The invitation has expired, please request a new one")
        );
    }
}