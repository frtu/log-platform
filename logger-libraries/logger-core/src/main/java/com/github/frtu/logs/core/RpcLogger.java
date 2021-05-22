package com.github.frtu.logs.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Specialized version of {@link StructuredLogger} to normalize RPC calls.
 */
@Slf4j
public class RpcLogger extends StructuredLogger {
    /**
     * Generic key equivalent to Tags#SPAN_KIND
     */
    public static final String KEY_KIND = "kind";

    /**
     * Generic key equivalent to Tags#HTTP_URL but for ANY business interface name
     */
    public static final String KEY_URI = "uri";

    /**
     * Generic key equivalent to Tags#HTTP_METHOD usually to categorize (Query or Mutation)
     */
    public static final String KEY_METHOD = "method";

    /**
     * Generic key for Request body
     */
    public static final String KEY_REQUEST_BODY = "request";

    /**
     * Generic key for Response body
     */
    public static final String KEY_RESPONSE_BODY = "response";

    /**
     * Generic key equivalent to Tags#HTTP_STATUS for response code (usually number but can be String)
     */
    public static final String KEY_STATUS_CODE = "response_code";

    /**
     * Generic key equivalent to Fields#MESSAGE for response message.
     */
    public static final String KEY_ERROR_MESSAGE = "error_message";

    /**
     * A constant for setting the span kind to indicate that it represents a server span.
     */
    public static final String SPAN_KIND_SERVER = "server";

    /**
     * A constant for setting the span kind to indicate that it represents a client span.
     */
    public static final String SPAN_KIND_CLIENT = "client";

    /**
     * A constant for setting the span kind to indicate that it represents a producer span, in a messaging scenario.
     */
    public static final String SPAN_KIND_PRODUCER = "producer";

    /**
     * A constant for setting the span kind to indicate that it represents a consumer span, in a messaging scenario.
     */
    public static final String SPAN_KIND_CONSUMER = "consumer";

    public static RpcLogger create(Class<?> clazz) {
        return create(clazz, null);
    }

    public static RpcLogger create(Class<?> clazz, String... prefixes) {
        return create(LoggerFactory.getLogger(clazz), prefixes);
    }

    public static RpcLogger create(String loggerName) {
        return create(loggerName, null);
    }

    public static RpcLogger create(String loggerName, String... prefixes) {
        return create(LoggerFactory.getLogger(loggerName), prefixes);
    }

    public static RpcLogger create(Logger logger) {
        return create(logger, null);
    }

    public static RpcLogger create(Logger logger, String... prefixes) {
        return new RpcLogger(logger, prefixes);
    }

    protected RpcLogger(Logger logger, String... prefixes) {
        super(logger, prefixes);
    }

    /**
     * Specify this is the Client log (one to one)
     *
     * @return
     */
    public static Map.Entry<String, String> client() {
        return kind(SPAN_KIND_CLIENT);
    }

    /**
     * Specify this is the Server log (one to one)
     *
     * @return
     */
    public static Map.Entry<String, String> server() {
        return kind(SPAN_KIND_SERVER);
    }

    /**
     * Specify this is the Producer log (one to many)
     *
     * @return
     */
    public static Map.Entry<String, String> producer() {
        return kind(SPAN_KIND_PRODUCER);
    }

    /**
     * Specify this is the Consumer log (one to many)
     *
     * @return
     */
    public static Map.Entry<String, String> consumer() {
        return kind(SPAN_KIND_CONSUMER);
    }

    /**
     * Usually to specify the direction side (uptstream or downstream) and One to One/Many
     *
     * @param kind
     * @return
     */
    public static Map.Entry<String, String> kind(String kind) {
        return entry(KEY_KIND, kind);
    }

    /**
     * Business operation name
     *
     * @param uri
     * @return
     */
    public static Map.Entry<String, String> uri(String uri) {
        return entry(KEY_URI, uri);
    }

    /**
     * To categorize Query (read without modification) or Mutation (modification)
     *
     * @param method
     * @return
     */
    public static Map.Entry<String, String> method(String method) {
        return entry(KEY_METHOD, method);
    }

    /**
     * Log the request body
     *
     * @param requestBody request payload (if NOT JSON please pass false {@link #responseBody(String, boolean)})
     * @return
     */
    public static Map.Entry<String, Object> requestBody(String requestBody) {
        return requestBody(requestBody, true);
    }

    /**
     * Log the request body and allow to inline the JSON directly as an object
     *
     * @param requestBody request payload
     * @param inlineJson  If is JSON and if we want to inline it
     * @return
     */
    public static Map.Entry<String, Object> requestBody(String requestBody, boolean inlineJson) {
        if (inlineJson) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode actualObj = mapper.readTree(requestBody);
                return requestBody(actualObj);
            } catch (IOException e) {
                LOGGER.trace("Input parameter is not be a well-format JSON : {}", requestBody, e);
            }
        }
        return entry(KEY_REQUEST_BODY, requestBody);
    }

    /**
     * Log the request body as an JSON object
     *
     * @param jsonNode
     * @return
     */
    public static Map.Entry<String, Object> requestBody(JsonNode jsonNode) {
        return StructuredLogger.entry(KEY_REQUEST_BODY, jsonNode);
    }

    /**
     * Log the response body
     *
     * @param responseBody response payload (if NOT JSON please pass false {@link #responseBody(String, boolean)})
     * @return
     */
    public static Map.Entry<String, Object> responseBody(String responseBody) {
        return responseBody(responseBody, true);
    }

    /**
     * Log the response body and allow to inline the JSON directly as an object
     *
     * @param responseBody response payload
     * @param inlineJson   If is JSON and if we want to inline it
     * @return
     */
    public static Map.Entry<String, Object> responseBody(String responseBody, boolean inlineJson) {
        if (inlineJson) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode actualObj = mapper.readTree(responseBody);
                return responseBody(actualObj);
            } catch (IOException e) {
                LOGGER.trace("Input parameter is not be a well-format JSON : {}", responseBody, e);
            }
        }
        return entry(KEY_RESPONSE_BODY, responseBody);
    }

    /**
     * Log the response body as a JSON object
     *
     * @param jsonNode
     * @return
     */
    public static Map.Entry<String, Object> responseBody(JsonNode jsonNode) {
        return StructuredLogger.entry(KEY_RESPONSE_BODY, jsonNode);
    }

    /**
     * Mark the response code (number or enum)
     *
     * @param statusCode
     * @return
     */
    public static Map.Entry<String, String> statusCode(int statusCode) {
        return statusCode(Integer.toString(statusCode));
    }

    /**
     * Mark the response code (number or enum)
     *
     * @param statusCode
     * @return
     */
    public static Map.Entry<String, String> statusCode(String statusCode) {
        return entry(KEY_STATUS_CODE, statusCode);
    }

    /**
     * Log the error message
     *
     * @param errorMessage
     * @return
     */
    public static Map.Entry<String, String> errorMessage(String errorMessage) {
        return entry(KEY_ERROR_MESSAGE, errorMessage);
    }
}
