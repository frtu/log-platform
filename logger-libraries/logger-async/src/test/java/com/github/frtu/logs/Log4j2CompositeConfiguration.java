package com.github.frtu.logs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4j2CompositeConfiguration {
    public static void main(String[] args) {
        System.setProperty("log4j.configurationFile", "log4j2-core.xml,log4j2-test.xml");

        Logger logger = LogManager.getLogger("com.github.frtu.logs");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
    }
}
