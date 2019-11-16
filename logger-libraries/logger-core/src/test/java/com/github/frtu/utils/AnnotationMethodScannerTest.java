package com.github.frtu.utils;

import com.github.frtu.logs.tracing.annotation.ExecutionSpan;
import com.github.frtu.logs.tracing.annotation.ExecutionSpanConfiguration;
import com.github.frtu.logs.tracing.annotation.Tag;
import com.github.frtu.logs.tracing.annotation.ToLog;
import com.google.common.collect.Multimap;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class AnnotationMethodScannerTest {
    @Test
    public void scanClass() {
        final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToLog>> scanner = AnnotationMethodScanner.of(ExecutionSpan.class, ToLog.class);

        // Scan Class
        final Multimap<String, AnnotationMethodScan<Class<? extends Annotation>, Class<? extends Annotation>>> multimap = scanner
                .scan(ExecutionSpanConfiguration.class);

        assertEquals(4, multimap.size());
        assertEquals("Only one method in ExecutionSpanConfiguration.simpleSpan", 1, multimap.get("simpleSpan").size());
        assertEquals("Should have 2 method in ExecutionSpanConfiguration.spanWithTags", 2, multimap.get("spanWithTags").size());
        assertEquals("Only one method in ExecutionSpanConfiguration.spanForLog", 1, multimap.get("spanForLog").size());
    }

    @Test
    public void scanMethodWithTags() throws NoSuchMethodException {
        final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToLog>> scanner = AnnotationMethodScanner.of(ExecutionSpan.class, ToLog.class);

        // Scan Method
        final Method spanMethod = ExecutionSpanConfiguration.class.getMethod("spanWithTags");
        final AnnotationMethodScan spanMethodScan = scanner.scan(spanMethod);

        // @Tag
        final Annotation[] annotationValueArray = spanMethodScan.getAnnotationValueArray();
        assertEquals(2, annotationValueArray.length);
        assertEquals(Tag.class, annotationValueArray[1].annotationType());
        final Tag tag = (Tag) annotationValueArray[1];
        assertEquals("tag2", tag.tagName());
        assertEquals("value2", tag.tagValue());

        // @ToLog
        assertEquals("spanWithTags should have no parameter annotated with @ToLog", 0, spanMethodScan.getParamAnnotations().length);
    }

    @Test
    public void scanMethodForLog() throws NoSuchMethodException {
        final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToLog>> scanner = AnnotationMethodScanner.of(ExecutionSpan.class, ToLog.class);

        // Scan Method
        final Method spanMethod = ExecutionSpanConfiguration.class.getMethod("spanForLog", String.class, String.class);
        final AnnotationMethodScan spanMethodScan = scanner.scan(spanMethod);

        final Annotation[] annotationValueArray = spanMethodScan.getAnnotationValueArray();
        assertEquals(0, annotationValueArray.length);
        final Annotation[] paramAnnotations = spanMethodScan.getParamAnnotations();
        assertNull("No Annotation on the first parameter!", paramAnnotations[0]);
        assertTrue("Should have @ToLog annotation on the second parameter!", (paramAnnotations[1] instanceof ToLog));
    }
}