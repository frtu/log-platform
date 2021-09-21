package com.github.frtu.logs.example.demo.biz;

import com.github.frtu.logs.core.metadata.ExecutionSpan;
import com.github.frtu.logs.core.metadata.ToLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class ChaosGenerator {
    public static final String OPERATION_NAME_RAISE_EXCEPTION = "ChaosGenerator.raiseException";

    private List<String> memoryLeak = new ArrayList<>();

    @ExecutionSpan(name = OPERATION_NAME_RAISE_EXCEPTION)
    public String raiseException(@ToLog("input.parameter") String errorMsg) {
        Random rand = new Random();
        int n = rand.nextInt(100);
        if ((n % 2) == 0) {
            throw new IllegalStateException(errorMsg);
        }
        return Integer.toString(n);
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
