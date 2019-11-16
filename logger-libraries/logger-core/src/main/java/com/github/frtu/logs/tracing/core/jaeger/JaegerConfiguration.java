package com.github.frtu.logs.tracing.core.jaeger;

import com.github.frtu.logs.tracing.core.TraceUtil;
import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static io.jaegertracing.Configuration.ReporterConfiguration;
import static io.jaegertracing.Configuration.SamplerConfiguration;

@Configuration
public class JaegerConfiguration implements TraceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaegerConfiguration.class);

    public static final String SYSTEM_PROPERTY_SERVICE_NAME = "SERVICE_NAME";
    @Value("${application.name:#{environment." + SYSTEM_PROPERTY_SERVICE_NAME + " ?: 'UNKNOWN'}}")
    private String applicationName;

    @Value("#{environment.JAEGER_ENDPOINT ?: 'UNKNOWN'}")
    private String jaegerEndpoint;

    @Value("#{environment.JAEGER_AGENT_HOST ?: 'UNKNOWN'}")
    private String jaegerAgentHost;

    @Value("#{environment.JAEGER_AGENT_PORT ?: 'UNKNOWN'}")
    private String jaegerAgentPort;

    @Override
    public String getTraceId(final Span span) {
        String traceId = null;
        if (span != null && span instanceof JaegerSpan) {
            traceId = ((JaegerSpan) span).context().getTraceId();
        }
        return traceId;
    }

    @PostConstruct
    public void logs() {
        LOGGER.info("applicationName:{}", applicationName);
        LOGGER.info("jaegerEndpoint:{}", jaegerEndpoint);
        LOGGER.info("jaegerAgentHost:{}", jaegerAgentHost);
        LOGGER.info("jaegerAgentPort:{}", jaegerAgentPort);
    }

    public String getApplicationName() {
        return applicationName;
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
    public Tracer tracer() {
        return initTracer(applicationName);
    }

    public static JaegerTracer initTracer(String applicationName) {
        LOGGER.info("Creating Tracer using applicationName={}", applicationName);
        SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
        ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withLogSpans(true);
        io.jaegertracing.Configuration config = new io.jaegertracing.Configuration(applicationName)
                .withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }
}
