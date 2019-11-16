package com.github.frtu.logs.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    @RequestMapping("/")
    @ResponseBody
    String home(@RequestParam(value = "service", defaultValue = "ServiceA", required = false) String name) {
            String formatString = formatString(name);
            printHello(formatString);

            return formatString;
    }

    private String formatString(String helloTo) {
            String helloStr = String.format("Hello, %s!", helloTo);
            printHello(helloStr);

            return helloStr;
    }

    private void printHello(String helloStr) {
            LOGGER.info(helloStr);
    }
}
