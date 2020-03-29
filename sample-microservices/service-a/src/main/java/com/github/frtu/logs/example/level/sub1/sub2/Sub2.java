package com.github.frtu.logs.example.level.sub1.sub2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Sub2 {
    public void levels() {
        LOGGER.trace("trace logs");
        LOGGER.debug("debug logs");
        LOGGER.info("info logs");
    }
}
