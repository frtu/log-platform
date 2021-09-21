package com.github.frtu.logs.tracing.core.jaeger;

import com.github.frtu.logs.core.metadata.ApplicationMetadata;
import com.github.frtu.logs.tracing.core.TraceUtil;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


@Configuration
public class JaegerConfiguration implements TraceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaegerConfiguration.class);

    @Value("#{environment.SAMPLING ?: false}")
    private boolean samplingTrace = false;

    @Value("#{environment.JAEGER_ENDPOINT ?: 'UNKNOWN'}")
    private String jaegerEndpoint;

    @Value("#{environment.JAEGER_AGENT_HOST ?: 'UNKNOWN'}")
    private String jaegerAgentHost;

    @Value("#{environment.JAEGER_AGENT_PORT ?: 'UNKNOWN'}")
    private String jaegerAgentPort;

    @Autowired
    private ApplicationMetadata applicationMetadata;

    Tracer tracer;

    @Override
    public String getTraceId(final Span span) {
        String traceId = null;
        return traceId;
    }

    @PostConstruct
    public void logs() {
        LOGGER.info("TRACING - jaegerEndpoint:'{}', jaegerAgentHost:'{}', jaegerAgentPort:'{}'", jaegerEndpoint, jaegerAgentHost, jaegerAgentPort);
        checkTracerInitialized();
    }

    private void checkTracerInitialized() {
        if (this.tracer == null) {
            throw new IllegalStateException("You need to initialize this component with Spring.");
        }
    }

    String getJaegerEndpoint() {
        return jaegerEndpoint;
    }

    String getJaegerAgentHost() {
        return jaegerAgentHost;
    }

    String getJaegerAgentPort() {
        return jaegerAgentPort;
    }

    @Bean
    @Override
    public Tracer getTracer() {
        return this.tracer;
    }
}
