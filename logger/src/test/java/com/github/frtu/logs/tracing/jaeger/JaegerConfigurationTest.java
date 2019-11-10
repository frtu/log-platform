package com.github.frtu.logs.tracing.jaeger;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.github.frtu.logs.tracing.jaeger.JaegerConfiguration.SYSTEM_PROPERTY_SERVICE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JaegerConfiguration.class})
public class JaegerConfigurationTest {

    @Autowired
    Tracer tracer;

    @Autowired
    JaegerConfiguration jaegerConfiguration;

    @Test
    public void getTraceId() {
        final Span start = tracer.buildSpan("test-span").start();
        final String traceId = jaegerConfiguration.getTraceId(start);
        assertNotNull(traceId);
    }

    @Test
    public void getApplicationName() {
        final String serviceName = "service-a";
        System.setProperty(SYSTEM_PROPERTY_SERVICE_NAME, serviceName);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(JaegerConfiguration.class);
        ctx.registerShutdownHook();

        final JaegerConfiguration jaegerConfiguration = ctx.getBean(JaegerConfiguration.class);
        assertEquals(serviceName, jaegerConfiguration.getApplicationName());

        System.clearProperty(SYSTEM_PROPERTY_SERVICE_NAME);
    }

    @Test
    public void getJaegerConfigs() {
        final String endpoint = "http://localhost:14268/api/traces";
        final String agentHost = "localhost";
        final String agentPort = "6831";
        System.setProperty("JAEGER_ENDPOINT", endpoint);
        System.setProperty("JAEGER_AGENT_HOST", agentHost);
        System.setProperty("JAEGER_AGENT_PORT", agentPort);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(JaegerConfiguration.class);
        ctx.registerShutdownHook();

        final JaegerConfiguration jaegerConfiguration = ctx.getBean(JaegerConfiguration.class);
        assertEquals(endpoint, jaegerConfiguration.getJaegerEndpoint());
        assertEquals(agentHost, jaegerConfiguration.getJaegerAgentHost());
        assertEquals(agentPort, jaegerConfiguration.getJaegerAgentPort());
    }
}