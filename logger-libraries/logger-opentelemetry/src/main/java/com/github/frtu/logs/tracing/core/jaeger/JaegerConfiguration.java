package com.github.frtu.logs.tracing.core.jaeger;

import com.github.frtu.logs.core.metadata.ApplicationMetadata;
import com.github.frtu.logs.tracing.core.TraceUtil;
import io.grpc.ClientInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

import static com.github.frtu.logs.tracing.core.opentelemetry.OpenTelemetryBuilder.buildOpenTelemetrySdk;
import static com.github.frtu.logs.tracing.core.opentelemetry.OpenTelemetryBuilder.buildSdkTracerProvider;

@Configuration
public class JaegerConfiguration implements TraceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaegerConfiguration.class);

    private ApplicationMetadata applicationMetadata;
    protected ClientInterceptor[] clientInterceptors;

    private String jaegerEndpoint;
    private String jaegerAgentHost;
    private Integer jaegerAgentPort;
    private Integer jaegerChannelTimeout;
    private boolean samplingTrace = false;

    private OpenTelemetry openTelemetry;

    static final ManagedChannelFactory managedChannelFactory = new ManagedChannelFactory();

    Tracer tracer;

    String getJaegerEndpoint() {
        return jaegerEndpoint;
    }

    String getJaegerAgentHost() {
        return jaegerAgentHost;
    }

    Integer getJaegerAgentPort() {
        return jaegerAgentPort;
    }

    public JaegerConfiguration(
            @Autowired
            ApplicationMetadata applicationMetadata,
            @Value("#{environment.JAEGER_AGENT_HOST ?: 'localhost'}")
            String jaegerAgentHost,
            @Value("#{environment.JAEGER_AGENT_PORT ?: 14250}")
            Integer jaegerAgentPort,
            @Value("#{environment.JAEGER_ENDPOINT ?: 'UNKNOWN'}")
            String jaegerEndpoint,
            @Value("#{environment.JAEGER_CHANNEL_TIMEOUT ?: 30000}")
            Integer jaegerChannelTimeout,
            @Value("#{environment.SAMPLING ?: false}")
            boolean samplingTrace,
            @Autowired(required = false)
            ClientInterceptor[] clientInterceptors
    ) {
        this.samplingTrace = samplingTrace;
        this.jaegerEndpoint = jaegerEndpoint;
        this.jaegerAgentHost = jaegerAgentHost;
        this.jaegerAgentPort = jaegerAgentPort;
        this.jaegerChannelTimeout = jaegerChannelTimeout;
        this.clientInterceptors = clientInterceptors;
        this.applicationMetadata = applicationMetadata;
    }

    @PostConstruct
    public void initialize() {
        LOGGER.info("TRACING - jaegerAgentHost:'{}', jaegerAgentPort:'{}'", jaegerAgentHost, jaegerAgentPort);
        this.openTelemetry = init(applicationMetadata.getApplicationName(), jaegerAgentHost, jaegerAgentPort, jaegerChannelTimeout, clientInterceptors);
        this.tracer = this.openTelemetry.getTracer("logger-opentelemetry");
    }

    @Bean
    @Override
    public Tracer getTracer() {
        return this.tracer;
    }

    public static OpenTelemetry init(String applicationName, String jaegerAgentHost, Integer jaegerAgentPort) {
        return init(applicationName, jaegerAgentHost, jaegerAgentPort, 30000, null);
    }

    public static OpenTelemetry init(String applicationName, String jaegerAgentHost, Integer jaegerAgentPort, Integer jaegerChannelTimeout, ClientInterceptor[] interceptors) {
        LOGGER.info("TRACING - Creating Tracer using applicationName={} samplingTrace={}", applicationName, jaegerAgentHost, jaegerAgentPort);
        JaegerGrpcSpanExporter jaegerExporter = JaegerGrpcSpanExporter.builder()
                .setEndpoint("http://" + jaegerAgentHost + ":" + jaegerAgentPort)
                .setTimeout(jaegerChannelTimeout, TimeUnit.SECONDS)
                .build();

        SdkTracerProvider tracerProvider = buildSdkTracerProvider(applicationName, SimpleSpanProcessor.create(jaegerExporter));

        OpenTelemetrySdk openTelemetry = buildOpenTelemetrySdk(TextMapPropagator.composite(
                W3CTraceContextPropagator.getInstance(), JaegerPropagator.getInstance()
        ), tracerProvider);

        Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));

        return openTelemetry;
    }
}
