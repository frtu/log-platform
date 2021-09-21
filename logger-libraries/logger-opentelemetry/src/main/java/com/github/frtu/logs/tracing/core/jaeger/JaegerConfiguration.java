package com.github.frtu.logs.tracing.core.jaeger;

import com.github.frtu.logs.core.metadata.ApplicationMetadata;
import com.github.frtu.logs.tracing.core.TraceUtil;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
public class JaegerConfiguration implements TraceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaegerConfiguration.class);

    @Value("#{environment.SAMPLING ?: false}")
    private boolean samplingTrace = false;

    @Value("#{environment.JAEGER_ENDPOINT ?: 'UNKNOWN'}")
    private String jaegerEndpoint;

    @Value("#{environment.JAEGER_AGENT_HOST ?: 'localhost'}")
    private String jaegerAgentHost;

    @Value("#{environment.JAEGER_AGENT_PORT ?: 14250}")
    private Integer jaegerAgentPort;

    @Value("#{environment.JAEGER_CHANNEL_TIMEOUT ?: 30000}")
    private Integer jaegerChannelTimeout;

    static final ManagedChannelFactory managedChannelFactory = new ManagedChannelFactory();
    @Autowired(required = false)
    protected ClientInterceptor[] clientInterceptors;

    @Autowired
    private ApplicationMetadata applicationMetadata;

    private OpenTelemetry openTelemetry;

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
        ManagedChannel jaegerChannel = managedChannelFactory.managedChannel(jaegerAgentHost, jaegerAgentPort, interceptors);

        JaegerGrpcSpanExporter jaegerExporter =
                JaegerGrpcSpanExporter.builder()
                        .setChannel(jaegerChannel)
                        .setTimeout(jaegerChannelTimeout, TimeUnit.MILLISECONDS)
                        .build();

        Resource serviceNameResource =
                Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, applicationName));

        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(SimpleSpanProcessor.create(jaegerExporter))
                        .setResource(Resource.getDefault().merge(serviceNameResource))
                        .build();
        OpenTelemetrySdk openTelemetry =
                OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

        Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));

        return openTelemetry;
    }
}
