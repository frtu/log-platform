package com.github.frtu.logs.tracing.annotation;

import com.github.frtu.logs.config.ConfigTracingAOP;
import com.github.frtu.spring.annotation.AnnotationMethodScan;
import com.github.frtu.spring.annotation.AnnotationMethodScanner;
import com.google.common.collect.ImmutableMap;
import io.opentracing.Span;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConfigTracingAOP.class})
public class ExecutionSpanAspectTest {
    @Autowired
    ExecutionSpanAspect executionSpanAspect;

    @Test
    public void getSimpleName() {
        final String name = executionSpanAspect.getName(String.class, "method", false);
        assertEquals("String.method", name);
    }

    @Test
    public void getFullName() {
        final String name = executionSpanAspect.getName(String.class, "method", true);
        assertEquals("java.lang.String.method", name);
    }

    @Test
    public void isAnnotationFoundPositive() throws NoSuchMethodException {
        final Method spanMethod = ExecutionSpanConfiguration.class.getMethod("simpleSpan", String.class, String.class);

        final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToLog>> scanner = AnnotationMethodScanner.of(ExecutionSpan.class, ToLog.class);
        final AnnotationMethodScan annotationMethodScan = scanner.scan(spanMethod);

        assertFalse("Annotation should exist in " + spanMethod.getName()
                , annotationMethodScan.isEmpty());
    }

    @Test
    public void isAnnotationFoundNegative() throws NoSuchMethodException {
        final Method spanMethod = ExecutionSpanConfiguration.class.getMethod("noAnnotation");

        final AnnotationMethodScanner<Class<ExecutionSpan>, Class<ToLog>> scanner = AnnotationMethodScanner.of(ExecutionSpan.class, ToLog.class);
        final AnnotationMethodScan annotationMethodScan = scanner.scan(spanMethod);

        assertTrue("Annotation doesn't exist in " + spanMethod.getName()
                , annotationMethodScan.isEmpty());
    }

    @Test
    public void enrichSpanWithTags() throws NoSuchMethodException {
        final Span span = createMock(Span.class);
        expect(span.setTag("tag1", "value1")).andReturn(span);
        expect(span.setTag("tag2", "value2")).andReturn(span);
        replay(span);

        final Method spanMethod = ExecutionSpanConfiguration.class.getMethod("spanWithTags");
        executionSpanAspect.enrichSpanWithTagsAndLogs(span, spanMethod, new Object[0]);

        verify(span);
    }

    @Test
    public void enrichSpanWithLogs() throws NoSuchMethodException {
        final Span span = createMock(Span.class);
        expect(span.log(ImmutableMap.of("param2", "b"))).andReturn(span);
        replay(span);

        final Method spanMethod = ExecutionSpanConfiguration.class.getMethod("spanForLog", String.class, String.class);
        executionSpanAspect.enrichSpanWithTagsAndLogs(span, spanMethod, new String[]{"a", "b"});

        verify(span);
    }
}