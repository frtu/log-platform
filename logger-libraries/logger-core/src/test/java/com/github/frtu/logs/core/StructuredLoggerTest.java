package com.github.frtu.logs.core;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class StructuredLoggerTest {

    @Test
    public void entry() {
        //--------------------------------------
        // 1. Prepare data
        //--------------------------------------
        final String key = "key";
        final String value = "value";

        //--------------------------------------
        // 2. Execute
        //--------------------------------------
        final Map.Entry<String, String> entry = StructuredLogger.entry(key, value);

        //--------------------------------------
        // 3. Validate
        //--------------------------------------
        assertEquals(key, entry.getKey());
        assertEquals(value, entry.getValue());
    }
}