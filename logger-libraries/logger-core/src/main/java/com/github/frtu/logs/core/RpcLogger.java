package com.github.frtu.logs.core;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Specialized version of {@link StructuredLogger} to normalize RPC calls.
 *
 * @author Frédéric TU
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
     * Generic key for Request ID
     *
     * @since 1.1.0
     */
    public static final String KEY_REQUEST_ID = "request_id";

    /**
     * Generic key for Request headers
     */
    public static final String KEY_REQUEST_HEADERS = "request_headers";

    /**
     * Generic key for Response headers
     */
    public static final String KEY_RESPONSE_HEADERS = "response_headers";

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
     * @return log entry pair
     */
    public static Map.Entry<String, String> client() {
        return kind(SPAN_KIND_CLIENT);
    }

    /**
     * Specify this is the Server log (one to one)
     *
     * @return log entry pair
     */
    public static Map.Entry<String, String> server() {
        return kind(SPAN_KIND_SERVER);
    }

    /**
     * Specify this is the Producer log (one to many)
     *
     * @return log entry pair
     */
    public static Map.Entry<String, String> producer() {
        return kind(SPAN_KIND_PRODUCER);
    }

    /**
     * Specify this is the Consumer log (one to many)
     *
     * @return log entry pair
     */
    public static Map.Entry<String, String> consumer() {
        return kind(SPAN_KIND_CONSUMER);
    }

    /**
     * Usually to specify the direction side (uptstream or downstream) and One to One/Many
     *
     * @param kind {@link #client()}, {@link #server()}, {@link #producer()}, {@link #consumer()}
     * @return log entry pair
     */
    public static Map.Entry<String, String> kind(String kind) {
        return entry(KEY_KIND, kind);
    }

    /**
     * Business operation name
     *
     * @param requestId unique id for this request (especially for post)
     * @return log entry pair
     * @since 1.1.2
     */
    public static Map.Entry<String, String> requestId(UUID requestId) {
        return requestId(requestId.toString());
    }


    /**
     * Business operation name
     *
     * @param requestId unique id for this request (especially for post)
     * @return log entry pair
     * @since 1.1.0
     */
    public static Map.Entry<String, String> requestId(String requestId) {
        return entry(KEY_REQUEST_ID, requestId);
    }

    /**
     * Business operation name
     *
     * @param uri Service URI
     * @return log entry pair
     */
    public static Map.Entry<String, String> uri(String uri) {
        return entry(KEY_URI, uri);
    }

    /**
     * Business operation name
     *
     * @param uri Service URI
     * @return log entry pair
     */
    public static Map.Entry<String, String> uri(URI uri) {
        return uri(uri.toString());
    }

    /**
     * To categorize Query (read without modification) or Mutation (modification)
     *
     * @param method (GET, POST, ...) or (Query or Mutation)
     * @return log entry pair
     */
    public static Map.Entry<String, String> method(String method) {
        return entry(KEY_METHOD, method);
    }

    /**
     * Log the request header as a JSON object
     *
     * @param headers KV pair for request headers
     * @return log entry pair
     */
    public static Map.Entry<String, Object> requestHeaders(Map<String, List<String>> headers) {
        if (headers.isEmpty()) return null;
        return entryJsonNode(KEY_REQUEST_HEADERS, headers);
    }

    /**
     * Log the request header as a JSON object
     *
     * @param jsonNode JsonNode representation for the request headers
     * @return log entry pair
     */
    public static Map.Entry<String, Object> requestHeaders(JsonNode jsonNode) {
        return entry(KEY_REQUEST_HEADERS, jsonNode);
    }

    /**
     * Log the response header as a JSON object
     *
     * @param headers KV pair for response headers
     * @return log entry pair
     */
    public static Map.Entry<String, Object> responseHeaders(Map<String, List<String>> headers) {
        if (headers.isEmpty()) return null;
        return entryJsonNode(KEY_RESPONSE_HEADERS, headers);
    }

    /**
     * Log the response header as a JSON object
     *
     * @param jsonNode JsonNode representation for the response headers
     * @return log entry pair
     */
    public static Map.Entry<String, Object> responseHeaders(JsonNode jsonNode) {
        return entry(KEY_RESPONSE_HEADERS, jsonNode);
    }

    /**
     * Log the request body
     *
     * @param <T>         Ability to specify the Type of request
     * @param requestBody request payload (if NOT JSON please pass false to {link #requestBody(T, boolean)})
     * @return log entry pair
     * @since 1.1.0
     */
    public static <T> Map.Entry<String, Object> requestBody(T requestBody) {
        return requestBody(requestBody, true);
    }

    /**
     * Log the request body and allow to inline the JSON directly as an object
     *
     * @param <T>         Ability to specify the Type of request
     * @param requestBody request payload
     * @param inlineJson  If is JSON and if we want to inline it
     * @return log entry pair
     * @since 1.1.0
     */
    public static <T> Map.Entry<String, Object> requestBody(T requestBody, boolean inlineJson) {
        if (inlineJson) {
            return entryJsonNode(KEY_REQUEST_BODY, requestBody);
        }
        return entry(KEY_REQUEST_BODY, requestBody);
    }

    /**
     * Log the request body as an JSON object
     *
     * @param jsonNode JsonNode representation for the payload
     * @return log entry pair
     */
    public static Map.Entry<String, Object> requestBody(JsonNode jsonNode) {
        return entry(KEY_REQUEST_BODY, jsonNode);
    }

    /**
     * Log the response body
     *
     * @param <T>          Ability to specify the Type of request
     * @param responseBody response payload (if NOT JSON please pass false to {link #responseBody(T, boolean)})
     * @return log entry pair
     * @since 1.1.0
     */
    public static <T> Map.Entry<String, Object> responseBody(T responseBody) {
        return responseBody(responseBody, true);
    }

    /**
     * Log the response body and allow to inline the JSON directly as an object
     *
     * @param <T>          Ability to specify the Type of request
     * @param responseBody response payload
     * @param inlineJson   If is JSON and if we want to inline it
     * @return log entry pair
     * @since 1.1.0
     */
    public static <T> Map.Entry<String, Object> responseBody(T responseBody, boolean inlineJson) {
        if (inlineJson) {
            return entryJsonNode(KEY_RESPONSE_BODY, responseBody);
        }
        return entry(KEY_RESPONSE_BODY, responseBody);
    }

    /**
     * Log the response body as a JSON object
     *
     * @param jsonNode JsonNode representation for the payload
     * @return log entry pair
     */
    public static Map.Entry<String, Object> responseBody(JsonNode jsonNode) {
        return entry(KEY_RESPONSE_BODY, jsonNode);
    }

    /**
     * Mark the response code (number or enum)
     *
     * @param statusCode Status code returned from the response
     * @return log entry pair
     * @since 1.1.2
     */
    public static Map.Entry<String, String> statusCode(@Nullable Integer statusCode) {
        if (statusCode == null) {
            // No null check needed for statusCode, as it will be just skipped here
            return null;
        }
        return statusCode(Integer.toString(statusCode));
    }

    /**
     * Mark the response code (number or enum)
     *
     * @param statusCode Status code returned from the response
     * @return log entry pair
     */
    public static Map.Entry<String, String> statusCode(String statusCode) {
        return entry(KEY_STATUS_CODE, statusCode);
    }

    /**
     * Log the error message
     *
     * @param errorMessage Error message returned
     * @return log entry pair
     */
    public static Map.Entry<String, String> errorMessage(String errorMessage) {
        return entry(KEY_ERROR_MESSAGE, errorMessage);
    }
}
