package com.github.frtu.logs.tracing.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.github.frtu.logs.tracing"})
public class ExecutionSpanConfiguration {
    @ExecutionSpan
    public void simpleSpan(String param1, String param2) {
    }

    @ExecutionSpan({@Tag(tagName = "tag1", tagValue = "value1"),
            @Tag(tagName = "tag2", tagValue = "value2")})
    public void spanWithTags() {
    }

    @ExecutionSpan(@Tag(tagName = "tag1", tagValue = "value1"))
    public void spanWithTags(String param1, String param2) {
    }

    @ExecutionSpan
    public void spanForLog(String param1, @ToLog("param2") String param2) {
    }

    public void noAnnotation() {
    }
}
