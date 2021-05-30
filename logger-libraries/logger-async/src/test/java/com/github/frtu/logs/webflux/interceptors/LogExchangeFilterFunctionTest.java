package com.github.frtu.logs.webflux.interceptors;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogExchangeFilterFunctionTest {
    private WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:" + mockWebServer.getPort())
                .filter(new LogExchangeFilterFunction())
                .build();
    }

    @Test
    void testFilterWithoutHeaders() {
        //--------------------------------------
        // 1. Prepare server data & Init client
        //--------------------------------------
        val responseBody = "{\"message\":\"response\"}";
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(responseBody)
                        .addHeader("Content-Type", "application/json")
        );

        //--------------------------------------
        // 2. Execute
        //--------------------------------------
        String result = webClient().get()
                .uri("/resources/1234")
                .retrieve().bodyToMono(String.class).block();

        logger.debug(result);
        //--------------------------------------
        // 3. Validate
        //--------------------------------------
    }


    @Test
    void testFilterWithHeaders() {
        //--------------------------------------
        // 1. Prepare server data & Init client
        //--------------------------------------
        val responseBody = "{\"message\":\"response\"}";
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(responseBody)
                        .addHeader("Content-Type", "application/json")
        );

        //--------------------------------------
        // 2. Execute
        //--------------------------------------
        String result = webClient().get()
                .uri("/resources/1234")
                .header("header1", "value1", "value2")
                .header("header2", "value3", "value4")
                .retrieve().bodyToMono(String.class).block();

        logger.debug(result);
        //--------------------------------------
        // 3. Validate
        //--------------------------------------
    }

    private MockWebServer mockWebServer = new MockWebServer();

    @BeforeAll
    void setup() throws IOException {
        logger.debug("=== {} starting mockWebServer:{}", getClass().getSimpleName(), mockWebServer);
        mockWebServer.start();
    }

    @AfterAll
    void tearDown() throws IOException {
        logger.debug("=== {} stopping mockWebServer:{}", getClass().getSimpleName(), mockWebServer);
        mockWebServer.shutdown();
    }
}