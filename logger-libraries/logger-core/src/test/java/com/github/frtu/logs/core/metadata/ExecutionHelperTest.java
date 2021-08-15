package com.github.frtu.logs.core.metadata;

import com.github.frtu.spring.annotation.AnnotationMethodScan;
import com.github.frtu.spring.annotation.AnnotationMethodScanner;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionHelperTest {
    private ExecutionHelper executionHelper = new ExecutionHelper();

    @Test
    public void getSimpleName() {
        final String name = executionHelper.getName(String.class, "method", false);
        assertEquals("String.method", name);
    }

    @Test
    public void getFullName() {
        final String name = executionHelper.getName(String.class, "method", true);
        assertEquals("java.lang.String.method", name);
    }

    @Test
    public void isAnnotationFoundPositive() throws NoSuchMethodException {
        final Method spanMethod = ExecutionSpanSample.class.getMethod("simpleSpan", String.class, String.class);

        final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToLog>> scanner = AnnotationMethodScanner.of(ExecutionSpan.class, ToLog.class);
        final AnnotationMethodScan annotationMethodScan = scanner.scan(spanMethod);

        assertFalse(annotationMethodScan.isEmpty(), "Annotation should exist in " + spanMethod.getName());
    }

    @Test
    public void isAnnotationFoundNegative() throws NoSuchMethodException {
        final Method spanMethod = ExecutionSpanSample.class.getMethod("noAnnotation");

        final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToLog>> scanner = AnnotationMethodScanner.of(ExecutionSpan.class, ToLog.class);
        final AnnotationMethodScan annotationMethodScan = scanner.scan(spanMethod);

        assertTrue(annotationMethodScan.isEmpty(), "Annotation doesn't exist in " + spanMethod.getName());
    }
}