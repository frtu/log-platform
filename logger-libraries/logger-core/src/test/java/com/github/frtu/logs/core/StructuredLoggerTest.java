package com.github.frtu.logs.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static com.github.frtu.logs.core.RpcLogger.*;
import static com.github.frtu.logs.core.StructuredLogger.entry;
import static com.github.frtu.logs.core.StructuredLogger.phase;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class StructuredLoggerTest {
    final static StructuredLogger STRUCTURED_LOGGER = StructuredLogger.create("usage");

    @Test
    public void mapNull() {
        final Map<String, String> map = StructuredLogger.unmodifiableMap(null);
        LOGGER.debug("Result map:{}", map);
        assertNull(map);
    }

    @Test
    public void entryAndMap() {
        //--------------------------------------
        // 1. Prepare data
        //--------------------------------------
        final String key = "key";
        final String value = "value";

        //--------------------------------------
        // 2. Execute
        //--------------------------------------
        final Map.Entry<String, String> entry = entry(key, value);

        //--------------------------------------
        // 3. Validate
        //--------------------------------------
        assertEquals(key, entry.getKey());
        assertEquals(value, entry.getValue());

        //--------------------------------------
        // 4. Test map
        //--------------------------------------
        final Map<String, String> map = StructuredLogger.unmodifiableMap(entry);
        LOGGER.debug("Result map:{}", map);
        assertEquals(value, map.get(key));
    }

    @Test
    public void entryAndMapNullValue() {
        //--------------------------------------
        // 1. Prepare data
        //--------------------------------------
        final String key = "key";

        //--------------------------------------
        // 2. Execute
        //--------------------------------------
        final Map.Entry<String, String> entry = entry(key, null);

        //--------------------------------------
        // 3. Validate
        //--------------------------------------
        assertEquals(key, entry.getKey());
        assertNull(entry.getValue());

        //--------------------------------------
        // 4. Test map
        //--------------------------------------
        final Map<String, String> map = StructuredLogger.unmodifiableMap(entry);
        LOGGER.debug("Result map:{}", map);
        assertNull(map.get(key));
    }

    @Test
    public void entryAndMapNullKey() {
        //--------------------------------------
        // 1. Prepare data
        //--------------------------------------
        final String value = "value";

        //--------------------------------------
        // 2. Execute
        //--------------------------------------
        final Map.Entry<String, String> entry = entry(null, value);

        //--------------------------------------
        // 3. Validate
        //--------------------------------------
        assertNull(entry.getKey());
        assertEquals(value, entry.getValue());

        //--------------------------------------
        // 4. Test map
        //--------------------------------------
        final Map<String, String> map = StructuredLogger.unmodifiableMap(entry);
        LOGGER.debug("Result map:{}", map);
        assertEquals(value, map.get(null));
    }

    @Test
    public void entryAndExceptionNull() {
        //--------------------------------------
        // 1. Prepare data
        //--------------------------------------
        final String value = "value";

        //--------------------------------------
        // 2. Execute
        //--------------------------------------
        // SHOULD NEVER RAISE EXCEPTION
        final Map.Entry entry = errorStackTrace(null);

        //--------------------------------------
        // 3. Validate
        //--------------------------------------
        LOGGER.debug("Result entry:{}", entry);
        assertNull(entry);

        final Map<String, String> map = StructuredLogger.unmodifiableMap(entry);
        LOGGER.debug("Result map:{}", map);
        // SHOULD NEVER RAISE EXCEPTION
        assertEquals(null, map.get(null));
    }

    @Test
    public void usageStringValue() {
        assertNotNull(STRUCTURED_LOGGER);
        STRUCTURED_LOGGER.info(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    public void usageIntValue() {
        assertNotNull(STRUCTURED_LOGGER);
        STRUCTURED_LOGGER.info(entry("key1", 123), entry("key2", 456));
    }

    @Test
    public void usageStringValuePrefix() {
        final StructuredLogger STRUCTURED_LOGGER_PREFIX = StructuredLogger.create("usage", "com", "frtu");
        assertNotNull(STRUCTURED_LOGGER_PREFIX);
        STRUCTURED_LOGGER_PREFIX.info(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    public void flowSteps() {
        assertNotNull(STRUCTURED_LOGGER);
        STRUCTURED_LOGGER.info(entry("key1", 123), entry("key2", 456));

        String flow = "send-data";
        UUID requestId = UUID.randomUUID();

        // Debugging steps
        STRUCTURED_LOGGER.info(client(),
                flow(flow),
                flowId(requestId),
                order(1),
                phase("INIT"),
                message("Initialize DB and Web connection")
        );

        // Debugging steps
        STRUCTURED_LOGGER.info(client(),
                flow(flow),
                flowId(requestId),
                order(2),
                phase("SENDING")
        );

        // Info success or Warn or Error result
        STRUCTURED_LOGGER.warn(client(),
                flow(flow),
                flowId(requestId),
                order("3A"),
                phase("SENT SUCCESFULLY"),
                key("db-id", UUID.randomUUID()),
                key("web-id", UUID.randomUUID())
        );
    }

    @Test
    public void errorStacktrace() {
        //--------------------------------------
        // 1. Prepare data
        //--------------------------------------
        assertNotNull(STRUCTURED_LOGGER);
        String errorMessage = "error message";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        //--------------------------------------
        // 2. Execute
        //--------------------------------------
        // SHOULD NEVER RAISE EXCEPTION
        Map.Entry<String, String> result = errorStackTrace(exception);
        LOGGER.debug("found result:{}", result);
        STRUCTURED_LOGGER.info(result);

        //--------------------------------------
        // 3. Validate
        //--------------------------------------
        assertTrue(result.getValue().contains(errorMessage));
    }
}