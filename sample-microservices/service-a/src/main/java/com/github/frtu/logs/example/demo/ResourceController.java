package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.example.level.sub1.Sub1;
import com.github.frtu.logs.example.level.sub1.sub2.Sub2;
import com.github.frtu.logs.example.level.sub1.sub2.sub3.Sub3;
import com.github.frtu.logs.tracing.core.OpenTelemetryHelper;
import io.micrometer.core.annotation.Timed;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
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
    private OpenTelemetryHelper traceHelper;

    @RequestMapping("/")
    @ResponseBody
    @Timed("home")
    String home(@RequestParam(value = "service", defaultValue = "ServiceA", required = false) String name) {
        Span firstSpan = traceHelper.startSpan("single-level");
        LOGGER.info("service={}", name);
        firstSpan.end();

        // https://opentelemetry.io/docs/java/manual_instrumentation/
        final Span secondSpan = traceHelper.startSpan("nested-span");
        try (Scope scope = secondSpan.makeCurrent()) {
            traceHelper.setAttribute("home", name);
            traceHelper.addEvent( "home-event1", "service", name);

            String formatString = formatString(name);
            traceHelper.addEvent("home-event2", "log1", "value1");
            printHello(formatString);

            return formatString;
        } catch (Throwable t) {
            traceHelper.flagError(t.getMessage());
            throw t;
        } finally {
            secondSpan.end();
        }
    }

    private String formatString(String helloTo) {
        traceHelper.addEvent("formatString-pre-scope", "log2", "value2");
        final Span span = traceHelper.startSpan("formatString");
        try (Scope scope = span.makeCurrent()) {
            traceHelper.addEvent("formatString-event1", "log3", "value3");
            String helloStr = String.format("Hello, %s!", helloTo);
            printHello(helloStr);

            traceHelper.addEvent("formatString-event2", "value", helloStr);
            return helloStr;
        } catch (Throwable t) {
            traceHelper.flagError(t.getMessage());
            throw t;
        } finally {
            span.end();
        }
    }

    private void printHello(String helloStr) {
        final Span span = traceHelper.startSpan("printHello");
        try (Scope scope = span.makeCurrent()) {
            traceHelper.addEvent("printHello-event1","log4", "value4");
            LOGGER.info(helloStr);
            traceHelper.addEvent("printHello-event2","event", "println");
            traceHelper.flagError("error msg!!");
        } catch (Throwable t) {
            traceHelper.flagError(t.getMessage());
            throw t;
        } finally {
            span.end();
        }
    }

    @RequestMapping("/levels")
    @ResponseBody
    String levels() {
        new Sub1().levels();
        new Sub2().levels();
        new Sub3().levels();
        return "See log levels in Logs";
    }
}
