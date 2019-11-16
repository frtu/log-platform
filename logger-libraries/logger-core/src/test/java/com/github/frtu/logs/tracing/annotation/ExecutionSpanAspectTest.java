package com.github.frtu.logs.tracing.annotation;

import com.github.frtu.utils.AnnotationMethodScan;
import com.github.frtu.utils.AnnotationMethodScanner;
import com.google.common.collect.Multimap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ExecutionSpanConfiguration.class})
public class ExecutionSpanAspectTest {
    @Autowired
    ExecutionSpanAspect executionSpanAspect;

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