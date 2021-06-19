package com.github.frtu.logs.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static com.github.frtu.logs.core.RpcLogger.*;
import static com.github.frtu.logs.core.RpcLogger.errorMessage;
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
}