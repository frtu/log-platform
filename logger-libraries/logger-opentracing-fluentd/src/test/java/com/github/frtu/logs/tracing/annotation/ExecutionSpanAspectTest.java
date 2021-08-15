package com.github.frtu.logs.tracing.annotation;

import com.github.frtu.logs.config.LogConfigTracingAOP;
import com.google.common.collect.ImmutableMap;
import io.opentracing.Span;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;

import static org.easymock.EasyMock.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {LogConfigTracingAOP.class})
public class ExecutionSpanAspectTest {
    @Autowired
    ExecutionSpanAspect executionSpanAspect;

    @Test
    public void enrichSpanWithTags() throws NoSuchMethodException {
        final Span span = createMock(Span.class);
        expect(span.setTag("tag1", "value1")).andReturn(span);
        expect(span.setTag("tag2", "value2")).andReturn(span);
        replay(span);

        final Method spanMethod = ExecutionSpanSample.class.getMethod("spanWithTags");
        executionSpanAspect.enrichSpanWithTags(span, spanMethod, new Object[0]);

        verify(span);
    }

    @Test
    public void enrichSpanWithParameterTag() throws NoSuchMethodException {
        final Span span = createMock(Span.class);
        expect(span.setTag("tag1", "value1")).andReturn(span);
        expect(span.setTag("param-tag", "a")).andReturn(span);
        replay(span);

        final Method spanMethod = ExecutionSpanSample.class.getMethod("spanWithTags", String.class, String.class);
        executionSpanAspect.enrichSpanWithTags(span, spanMethod, new String[]{"a", "b"});

        verify(span);
    }

    @Test
    public void enrichSpanWithLogs() throws NoSuchMethodException {
        final Span span = createMock(Span.class);
        expect(span.log(ImmutableMap.of("param2", "b"))).andReturn(span);
        replay(span);

        final Method spanMethod = ExecutionSpanSample.class.getMethod("spanForLog", String.class, String.class);
        executionSpanAspect.enrichSpanWithLogs(span, spanMethod, new String[]{"a", "b"});

        verify(span);
    }
}