package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.config.LogConfigTracingOnly;
import com.github.frtu.metrics.config.MetricsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({LogConfigTracingOnly.class, MetricsConfig.class})
public class DemoApplicationA {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplicationA.class, args);
    }
}
