package com.github.frtu.logs.tracing;

public interface LogHelper {
    LogHelper addTraceId(final io.opentracing.Span span);

    /**
     * Add a key corresponding to a field of a schema.
     *
     * @param keyName name of that field
     * @param value value of the field
     * @param <FT> Field value type
     * @return itself
     */
    <FT> LogHelper addKey(String keyName, FT value, Class<FT> valueType);
    LogHelper addKey(String keyName, Boolean value);
    LogHelper addKey(String keyName, Short value);
    LogHelper addKey(String keyName, Integer value);
    LogHelper addKey(String keyName, Long value);
    LogHelper addKey(String keyName, Float value);
    LogHelper addKey(String keyName, Double value);
    LogHelper addKey(String keyName, Byte value);
    LogHelper addKey(String keyName, Character value);
    LogHelper addKey(String keyName, String value);

    /**
     * Add a data corresponding to a field of a schema.
     *
     * @param fieldName name of that field
     * @param value value of the field
     * @param <FT> Field value type
     * @return itself
     */
    <FT> LogHelper addData(String fieldName, FT value, Class<FT> valueType);
    LogHelper addData(String keyName, Boolean value);
    LogHelper addData(String keyName, Short value);
    LogHelper addData(String keyName, Integer value);
    LogHelper addData(String keyName, Long value);
    LogHelper addData(String keyName, Float value);
    LogHelper addData(String keyName, Double value);
    LogHelper addData(String keyName, Byte value);
    LogHelper addData(String keyName, Character value);
    LogHelper addData(String keyName, String value);
}
