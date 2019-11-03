package com.github.frtu.logs.tracing.jaeger;

import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.jaegertracing.Configuration.ReporterConfiguration;
import static io.jaegertracing.Configuration.SamplerConfiguration;

@Configuration
public class JaegerConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaegerConfiguration.class);

    @Value("${application.name:UNKNOWN}")
    private String applicationName;

    @Bean
    public Tracer tracer() {
        return initTracer(applicationName);
    }

    public static JaegerTracer initTracer(String applicationName) {
        LOGGER.info("Creating Tracer using applicationName={}", applicationName);
        SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
        ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withLogSpans(true);
        io.jaegertracing.Configuration config = new io.jaegertracing.Configuration(applicationName).withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }
}
