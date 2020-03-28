package com.github.frtu.logs.example.demo.biz;

import com.github.frtu.logs.tracing.annotation.ExecutionSpan;
import com.github.frtu.logs.tracing.annotation.ToLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ChaosGenerator {
    private List<String> memoryLeak = new ArrayList<>();

    @ExecutionSpan(name = "ChaosGenerator.raiseException")
    public void raiseException(@ToLog("input.parameter") String errorMsg) {
        throw new IllegalStateException(errorMsg);
    }

    public String memoryleak() {
        StringBuilder longPayload = new StringBuilder();
        for (int i = 0; i < 100000000; i++) {
            longPayload.append('0');
        }
        String big = longPayload.toString();
        for (int j = 0; j < 1000000; j++) {
            for (int i = 0; i < 1000000; i++) {
                increaseMemory(big);
            }
            LOGGER.debug("Counter {}", j);
        }
        return "No issue";
    }

    protected void increaseMemory(String paylaod) {
        memoryLeak.add(paylaod);
    }
}
