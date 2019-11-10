package com.github.frtu.logs.tracing;

/**
 * Fluent Interface to enrich logs with Request unique ID (TraceID), key or data.
 *
 * @author frdtu
 * @see <a href="https://martinfowler.com/bliki/FluentInterface.html">FluentInterface by Martin Fowler</a>
 */
public interface LogEnricher {
    /**
     * Enrich log with a Trace ID
     *
     * @param span
     * @return
     */
    LogEnricher addTraceId(final io.opentracing.Span span);

    /**
     * Enrich log with a key corresponding to a field of a schema.
     *
     * @param keyName name of that field
     * @param value   value of the field
     * @param <FT>    Field value type
     * @return itself
     */
    <FT> LogEnricher addKey(String keyName, FT value, Class<FT> valueType);

    LogEnricher addKey(String keyName, Boolean value);

    LogEnricher addKey(String keyName, Short value);

    LogEnricher addKey(String keyName, Integer value);

    LogEnricher addKey(String keyName, Long value);

    LogEnricher addKey(String keyName, Float value);

    LogEnricher addKey(String keyName, Double value);

    LogEnricher addKey(String keyName, Byte value);

    LogEnricher addKey(String keyName, Character value);

    LogEnricher addKey(String keyName, String value);

    /**
     * Enrich log with a data corresponding to a field of a schema.
     *
     * @param fieldName name of that field
     * @param value     value of the field
     * @param <FT>      Field value type
     * @return itself
     */
    <FT> LogEnricher addData(String fieldName, FT value, Class<FT> valueType);

    LogEnricher addData(String keyName, Boolean value);

    LogEnricher addData(String keyName, Short value);

    LogEnricher addData(String keyName, Integer value);

    LogEnricher addData(String keyName, Long value);

    LogEnricher addData(String keyName, Float value);

    LogEnricher addData(String keyName, Double value);

    LogEnricher addData(String keyName, Byte value);

    LogEnricher addData(String keyName, Character value);

    LogEnricher addData(String keyName, String value);
}
