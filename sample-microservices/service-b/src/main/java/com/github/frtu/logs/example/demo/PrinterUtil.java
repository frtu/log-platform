package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.tracing.annotation.ExecutionSpan;
import com.github.frtu.logs.tracing.annotation.Tag;
import com.github.frtu.logs.tracing.annotation.ToLog;
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

    @ExecutionSpan(name = "formatStringOneParam")
    public String formatString(@ToLog("helloTo") String helloTo) {
        String helloStr = String.format("Hello, %s!", helloTo);

        traceHelper.addLog("log-hello", helloTo);
        printHello(helloStr);

        return helloStr;
    }

    @ExecutionSpan(name = "printHelloOneParam", value = {
            @Tag(tagName = "key1", tagValue = "value1"),
            @Tag(tagName = "key2", tagValue = "value2")
    })
    public void printHello(String helloStr) {
        traceHelper.addLog("print-hello", helloStr);
        LOGGER.info(helloStr);
    }
}
