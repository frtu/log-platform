package com.github.frtu.logs.core;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class StructuredLoggerTest {

    @Test
    public void entries() {
        //--------------------------------------
        // 1. Prepare data
        //--------------------------------------
        final String key = "key";
        final String value = "value";

        //--------------------------------------
        // 2. Execute
        //--------------------------------------
        final Map.Entry<String, String> entries = StructuredLogger.entries(key, value);

        //--------------------------------------
        // 3. Validate
        //--------------------------------------
        assertEquals(key, entries.getKey());
        assertEquals(value, entries.getValue());
    }

    @Test
    public void info() {
    }
}