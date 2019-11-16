package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.tracing.annotation.ExecutionSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PrinterUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrinterUtil.class);

    @ExecutionSpan
    public String formatString(String helloTo) {
        String helloStr = String.format("Hello, %s!", helloTo);
        printHello(helloStr);

        return helloStr;
    }

    @ExecutionSpan
    public void printHello(String helloStr) {
        LOGGER.info(helloStr);
    }
}
