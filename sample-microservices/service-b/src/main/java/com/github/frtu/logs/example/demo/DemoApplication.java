package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.config.LogConfigAll;
import com.github.frtu.metrics.config.MetricsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({LogConfigAll.class, MetricsConfig.class})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
