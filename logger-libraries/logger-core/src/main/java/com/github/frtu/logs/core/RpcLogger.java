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
     * Generic key equivalent to {@link io.opentracing.tag.Tags#SPAN_KIND}
     */
    public static final String KEY_KIND = "kind";

    /**
     * Generic key equivalent to {@link io.opentracing.tag.Tags#HTTP_URL} but for ANY business interface name
     */
    public static final String KEY_URI = "uri";

    /**
     * Generic key equivalent to {@link io.opentracing.tag.Tags#HTTP_METHOD} usually to categorize (Query or Mutation)
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
     * Generic key equivalent to {@link io.opentracing.tag.Tags#HTTP_STATUS} for response code (usually number but can be String)
     */
    public static final String KEY_STATUS_CODE = "response_code";

    /**
     * Generic key equivalent to {@link io.opentracing.log.Fields#MESSAGE} for response message.
     */
    public static final String KEY_ERROR_MESSAGE = "error_message";

    public static RpcLogger create(Class<?> clazz) {
        return create(LoggerFactory.getLogger(clazz));
    }

    public static RpcLogger create(String loggerName) {
        return create(LoggerFactory.getLogger(loggerName));
    }

    public static RpcLogger create(Logger logger) {
        return new RpcLogger(logger);
    }

    protected RpcLogger(Logger logger) {
        super(logger);
    }

    /**
     * Specify this is the Client log (one to one)
     *
     * @return
     */
    public static Map.Entry<String, String> client() {
        return kind(io.opentracing.tag.Tags.SPAN_KIND_CLIENT);
    }

    /**
     * Specify this is the Server log (one to one)
     *
     * @return
     */
    public static Map.Entry<String, String> server() {
        return kind(io.opentracing.tag.Tags.SPAN_KIND_SERVER);
    }

    /**
     * Specify this is the Producer log (one to many)
     *
     * @return
     */
    public static Map.Entry<String, String> producer() {
        return kind(io.opentracing.tag.Tags.SPAN_KIND_PRODUCER);
    }

    /**
     * Specify this is the Consumer log (one to many)
     *
     * @return
     */
    public static Map.Entry<String, String> consumer() {
        return kind(io.opentracing.tag.Tags.SPAN_KIND_CONSUMER);
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
     * @param requestBody
     * @return
     */
    public static Map.Entry<String, Object> requestBody(String requestBody) {
        return requestBody(requestBody, true);
    }

    /**
     * Log the request body and allow to inline the JSON directly as an object
     *
     * @param requestBody
     * @param inline
     * @return
     */
    public static Map.Entry<String, Object> requestBody(String requestBody, boolean inline) {
        if (inline) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode actualObj = mapper.readTree(requestBody);
                return requestBody(actualObj);
            } catch (IOException e) {
                LOGGER.warn("Impossible to inline JSON, use the encapsulation instead for {}", requestBody, e);
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
     * @param responseBody
     * @return
     */
    public static Map.Entry<String, Object> responseBody(String responseBody) {
        return responseBody(responseBody, true);
    }

    /**
     * Log the response body and allow to inline the JSON directly as an object
     *
     * @param responseBody
     * @param inline
     * @return
     */
    public static Map.Entry<String, Object> responseBody(String responseBody, boolean inline) {
        if (inline) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode actualObj = mapper.readTree(responseBody);
                return responseBody(actualObj);
            } catch (IOException e) {
                LOGGER.warn("Impossible to inline JSON, use the encapsulation instead for {}", responseBody, e);
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
