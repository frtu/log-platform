package com.github.frtu.logs.example.demo.biz;

import com.github.frtu.logs.core.metadata.ExecutionSpan;
import com.github.frtu.logs.core.metadata.ToLog;
import com.github.frtu.test.resilience.ChaosGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ChaosGeneratorController extends ChaosGenerator {
    public static final String OPERATION_NAME_RAISE_EXCEPTION = "ChaosGenerator.raiseException";

    private List<String> memoryLeak = new ArrayList<>();

    @ExecutionSpan(name = OPERATION_NAME_RAISE_EXCEPTION)
    public String raiseException(@ToLog("input.parameter") String errorMsg) {
        return super.raiseException(errorMsg);
    }
}
