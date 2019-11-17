package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.tracing.annotation.ExecutionSpan;
import com.github.frtu.logs.tracing.annotation.Tag;
import com.github.frtu.logs.tracing.annotation.ToLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PrinterUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrinterUtil.class);

    @ExecutionSpan
    public String formatString(@ToLog("helloTo") String helloTo) {
        String helloStr = String.format("Hello, %s!", helloTo);
        printHello(helloStr);

        return helloStr;
    }

    @ExecutionSpan({
            @Tag(tagName = "key1", tagValue = "value1"),
            @Tag(tagName = "key2", tagValue = "value2")
    })
    public void printHello(String helloStr) {
        LOGGER.info(helloStr);
    }
}
