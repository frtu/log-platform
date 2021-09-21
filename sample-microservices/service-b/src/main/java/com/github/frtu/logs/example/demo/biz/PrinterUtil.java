package com.github.frtu.logs.example.demo.biz;

import com.github.frtu.logs.core.RpcLogger;
import com.github.frtu.logs.core.metadata.ExecutionSpan;
import com.github.frtu.logs.core.metadata.Tag;
import com.github.frtu.logs.core.metadata.ToLog;
import com.github.frtu.logs.core.metadata.ToTag;
import com.github.frtu.logs.tracing.core.TraceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.github.frtu.logs.core.RpcLogger.*;

@Component
public class PrinterUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrinterUtil.class);
    private static final RpcLogger RPC_LOGGER = RpcLogger.create(LOGGER, "frtu");

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

        final Map.Entry[] entries = entries(client(),
                method("GET"),
                uri(ChaosGenerator.OPERATION_NAME_RAISE_EXCEPTION),
                requestBody(helloTo, false));
        try {
            final String response = chaosGenerator.raiseException("Randomly generate exception to demonstrate exception flag!");
            RPC_LOGGER.info(entries, responseBody(response, false), statusCode(200));
        } catch (IllegalStateException e) {
            // Just to demonstrate exception calling issue
            RPC_LOGGER.warn(entries, statusCode(500));
        }
        LOGGER.debug("Flow should continue");

        return helloStr;
    }

    @ExecutionSpan(name = "logDemo")
    public void logDemo(@ToLog("parameter-log") String helloStr) {
        traceHelper.addLog("log-block", "log = " + helloStr);
    }
}
