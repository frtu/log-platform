package com.github.frtu.logs.tracing.core.jaeger;

import com.github.frtu.logs.config.LogConfigTracingOnly;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {LogConfigTracingOnly.class})
public class JaegerConfigurationTest {

    @Autowired
    JaegerConfiguration jaegerConfiguration;

    @Test
    public void getTracer() {
        final Tracer tracer = jaegerConfiguration.getTracer();
        assertNotNull("jaegerConfiguration may not be initialize correctly! A tracer MUST exist!", tracer);
    }

    @Test
    public void getTraceId() {
        final Span start = jaegerConfiguration.getTracer().buildSpan("test-span").start();
        final String traceId = jaegerConfiguration.getTraceId(start);
        assertNotNull(traceId);
    }

    @Test
    public void getJaegerConfigs() {
        final String endpoint = "http://localhost:14268/api/traces";
        final String agentHost = "localhost";
        final String agentPort = "6831";
        System.setProperty("JAEGER_ENDPOINT", endpoint);
        System.setProperty("JAEGER_AGENT_HOST", agentHost);
        System.setProperty("JAEGER_AGENT_PORT", agentPort);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(LogConfigTracingOnly.class);
        ctx.registerShutdownHook();

        final JaegerConfiguration jaegerConfiguration = ctx.getBean(JaegerConfiguration.class);
        assertEquals(endpoint, jaegerConfiguration.getJaegerEndpoint());
        assertEquals(agentHost, jaegerConfiguration.getJaegerAgentHost());
        assertEquals(agentPort, jaegerConfiguration.getJaegerAgentPort());
    }
}