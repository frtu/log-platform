package com.github.frtu.logs.example.demo;

import com.github.frtu.logs.tracing.annotation.ExecutionSpan;
import com.github.frtu.logs.tracing.annotation.ToLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @see <a href="https://github.com/yurishkuro/opentracing-tutorial/tree/master/java/src/main/java/lesson01">lesson01</a>
 * @see <a href="https://github.com/yurishkuro/opentracing-tutorial/tree/master/java/src/main/java/lesson02">lesson02</a>
 */
@Controller
public class ResourceController {
    @Autowired
    PrinterUtil printerUtil;


    @ExecutionSpan
    @RequestMapping("/")
    @ResponseBody
    String home(@RequestParam(value = "service", defaultValue = "ServiceB", required = false)
                @ToLog("name") String name) {
        String formatString = printerUtil.formatString(name);
        printerUtil.printHello(formatString);
        return formatString;
    }
}
