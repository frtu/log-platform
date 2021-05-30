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
import java.util.HashSet;
import java.util.Set;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogExchangeFilterFunctionTest {
    private WebClient webClient() {
        return webClient(null);
    }

    private WebClient webClient(Set<String> authorizedHeaderFilter) {
        return WebClient.builder()
                .baseUrl("http://localhost:" + mockWebServer.getPort())
                .filter(new LogExchangeFilterFunction(authorizedHeaderFilter))
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
    void testFilterWithFilteredHeaders() {
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
        // ONLY one header authorized for security reason
        HashSet authorizedHeaderFilter = new HashSet<>();
        authorizedHeaderFilter.add("header1");

        String result = webClient(authorizedHeaderFilter).get()
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