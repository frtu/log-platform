package com.github.frtu.logs.example.demo.biz;

import com.github.frtu.logs.tracing.annotation.ExecutionSpan;
import com.github.frtu.logs.tracing.annotation.ToLog;
import org.springframework.stereotype.Component;

@Component
public class ChaosGenerator {
    @ExecutionSpan(name = "ChaosGenerator.raiseException")
    public void raiseException(@ToLog("input.parameter") String errorMsg) {
        throw new IllegalStateException(errorMsg);
    }
}
