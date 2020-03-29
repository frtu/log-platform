package com.github.frtu.logs.example.level.sub1;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Sub1 {
    public void levels() {
        LOGGER.trace("trace logs");
        LOGGER.debug("debug logs");
        LOGGER.info("info logs");
    }
}
