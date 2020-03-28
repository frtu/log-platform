package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.example.demo.biz.ChaosGenerator;
import com.github.frtu.logs.example.demo.biz.PrinterUtil;
import com.github.frtu.logs.tracing.annotation.ExecutionSpan;
import com.github.frtu.logs.tracing.annotation.ToLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @see <a href="https://github.com/yurishkuro/opentracing-tutorial/tree/master/java/src/main/java/lesson01">lesson01</a>
 * @see <a href="https://github.com/yurishkuro/opentracing-tutorial/tree/master/java/src/main/java/lesson02">lesson02</a>
 */
@Slf4j
@Controller
public class ResourceController {
    @Autowired
    PrinterUtil printerUtil;

    @Autowired
    private ChaosGenerator chaosGenerator;

    @ExecutionSpan
    @RequestMapping("/")
    @ResponseBody
    String home(@RequestParam(value = "service", defaultValue = "ServiceB", required = false)
                @ToLog("name") String name) {
        String formatString = printerUtil.tagDemo(name);
        printerUtil.logDemo(formatString);
        return formatString;
    }

    @RequestMapping("/memoryleak")
    @ResponseBody
    String memoryleak() {
        chaosGenerator.memoryleak();
        return "No issue";
    }
}
