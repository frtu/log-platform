package com.github.frtu.logs.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ResourceController {
    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Service B";
    }
}
