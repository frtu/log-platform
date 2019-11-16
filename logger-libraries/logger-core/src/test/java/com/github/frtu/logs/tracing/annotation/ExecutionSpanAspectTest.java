package com.github.frtu.logs.tracing.annotation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExecutionSpanAspectTest {
    final ExecutionSpanAspect executionSpanAspect = new ExecutionSpanAspect();

    @Test
    public void getSimpleName() {
        executionSpanAspect.isFullClassName = false;
        final String name = executionSpanAspect.getName(String.class, "method");
        assertEquals("String.method", name);
    }

    @Test
    public void getFullName() {
        executionSpanAspect.isFullClassName = true;
        final String name = executionSpanAspect.getName(String.class, "method");
        assertEquals("java.lang.String.method", name);
    }
}