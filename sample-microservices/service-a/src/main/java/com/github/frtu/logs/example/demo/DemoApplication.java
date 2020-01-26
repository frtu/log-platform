package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.config.ConfigFluentD;
import com.github.frtu.logs.config.ConfigTracingOnly;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ConfigTracingOnly.class, ConfigFluentD.class})
@ComponentScan(basePackages = {"com.github.frtu.metrics", "com.github.frtu.logs.example"})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
