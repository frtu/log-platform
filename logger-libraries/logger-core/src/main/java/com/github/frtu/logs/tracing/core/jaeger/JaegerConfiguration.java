package com.github.frtu.logs.tracing.core.jaeger;

import com.github.frtu.logs.core.ApplicationMetadata;
import com.github.frtu.logs.tracing.core.TraceUtil;
import io.jaegertracing.internal.JaegerSpan;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.micrometer.MicrometerMetricsFactory;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static io.jaegertracing.Configuration.ReporterConfiguration;
import static io.jaegertracing.Configuration.SamplerConfiguration;

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
        if (span != null && span instanceof JaegerSpan) {
            traceId = ((JaegerSpan) span).context().getTraceId();
        }
        return traceId;
    }

    @PostConstruct
    public void logs() {
        LOGGER.info("jaegerEndpoint:'{}', jaegerAgentHost:'{}', jaegerAgentPort:'{}'", jaegerEndpoint, jaegerAgentHost, jaegerAgentPort);
        this.tracer = initTracer(applicationMetadata.getApplicationName(), samplingTrace);
        checkTracerInitialized();
        if (!GlobalTracer.isRegistered()) {
            GlobalTracer.register(this.tracer);
        }
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

    public static JaegerTracer initTracer(String applicationName, boolean samplingTrace) {
        LOGGER.info("Creating Tracer using applicationName={} samplingTrace={}", applicationName, samplingTrace);

        // https://www.jaegertracing.io/docs/1.15/sampling/
        SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv();
        if (!samplingTrace) {
            samplerConfig.withType("const").withParam(1);
        }
        ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withLogSpans(true);

        io.jaegertracing.Configuration config = new io.jaegertracing.Configuration(applicationName)
                .withSampler(samplerConfig).withReporter(reporterConfig);

        // https://github.com/jaegertracing/jaeger-client-java/tree/master/jaeger-micrometer
        final MicrometerMetricsFactory metricsFactory = new MicrometerMetricsFactory();
        final JaegerTracer tracer = config.getTracerBuilder()
                .withMetricsFactory(metricsFactory)
                .build();
        return tracer;
    }
}
