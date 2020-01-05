package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.tracing.core.TraceHelper;
import com.google.common.collect.ImmutableMap;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @see <a href="https://github.com/yurishkuro/opentracing-tutorial/tree/master/java/src/main/java/lesson01">lesson01</a>
 * @see <a href="https://github.com/yurishkuro/opentracing-tutorial/tree/master/java/src/main/java/lesson02">lesson02</a>
 */
@Controller
public class ResourceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private Tracer tracer;

    @Autowired
    private TraceHelper traceHelper;

    @RequestMapping("/")
    @ResponseBody
    String home(@RequestParam(value = "service", defaultValue = "ServiceA", required = false) String name) {
        Span span = tracer.buildSpan("say-hello1").start();
        LOGGER.info("service={}", name);
        span.finish();

        try (Scope scope = tracer.buildSpan("say-hello2").startActive(true)) {
            scope.span().log(ImmutableMap.of("event", "string-format", "value", name));
            scope.span().setTag("hello-to", name);

            String formatString = formatString(name);
            traceHelper.addLog("log1", "value1");
            printHello(formatString);

            return formatString;
        }
    }

    private String formatString(String helloTo) {
        traceHelper.addLog("log2", "value2");
        try (Scope scope = tracer.buildSpan("formatString").startActive(true)) {
            traceHelper.addLog("log3", "value3");
            String helloStr = String.format("Hello, %s!", helloTo);
            printHello(helloStr);

            scope.span().log(ImmutableMap.of("event", "string-format", "value", helloStr));
            return helloStr;
        }
    }

    private void printHello(String helloStr) {
        try (Scope scope = tracer.buildSpan("printHello").startActive(true)) {
            traceHelper.addLog("log4", "value4");
            LOGGER.info(helloStr);
            scope.span().log(ImmutableMap.of("event", "println"));
            traceHelper.flagError("error msg!!");
        }
    }
}
