package com.github.frtu.logs.example.demo.biz;

import com.github.frtu.logs.tracing.annotation.ExecutionSpan;
import com.github.frtu.logs.tracing.annotation.Tag;
import com.github.frtu.logs.tracing.annotation.ToLog;
import com.github.frtu.logs.tracing.annotation.ToTag;
import com.github.frtu.logs.tracing.core.TraceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrinterUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrinterUtil.class);

    @Autowired
    private TraceHelper traceHelper;

    @Autowired
    private ChaosGenerator chaosGenerator;

    @ExecutionSpan(name = "tagDemo", value = {
            @Tag(tagName = "static-tag1", tagValue = "value1"),
            @Tag(tagName = "static-tag2", tagValue = "value2")
    })
    public String tagDemo(@ToTag("parameter-tag") String helloTo) {
        String helloStr = String.format("Hello, %s!", helloTo);
        traceHelper.addTag("tag-block", helloTo);

        try {
            chaosGenerator.raiseException("Raise sample exception to demonstrate exception flag!");
        } catch (IllegalStateException e) {
            // Just to demonstrate exception calling issue
        }
        LOGGER.debug("Flow should continue");

        return helloStr;
    }

    @ExecutionSpan(name = "logDemo")
    public void logDemo(@ToLog("parameter-log") String helloStr) {
        traceHelper.addLog("log-block", "log = " + helloStr);
    }
}
