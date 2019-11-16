package com.github.frtu.logs;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.more.appenders.marker.MapMarker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class LogbackAppenderTest {
    private static final Logger LOG = LoggerFactory.getLogger(LogbackAppenderTest.class);

    @Before
    public void before() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (!lc.isStarted()) {
            lc.start();
        }
    }

    @After
    public void after() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.stop();
    }

    @Test
    public void logSimple() throws InterruptedException {
        LOG.debug("Test the logger 1.");
        LOG.debug("Test the logger 2.");

        Thread.sleep(1000); // Wait a moment because these log is being appended asynchronous...
    }

    @Test
    public void logMdc() throws InterruptedException {
        MDC.put("req.requestURI", "/hello/world.js");
        LOG.debug("Test the logger 1.");
        LOG.debug("Test the logger 2.");

        Thread.sleep(1000); // Wait a moment because these log is being appended asynchronous...
    }

    @Test
    public void logMarker() throws InterruptedException {
        Marker sendEmailMarker = MarkerFactory.getMarker("SEND_EMAIL");
        LOG.debug(sendEmailMarker, "Test the marker 1.");
        LOG.debug(sendEmailMarker, "Test the marker 2.");

        Thread.sleep(1000); // Wait a moment because these log is being appended asynchronous...
    }

    @Test
    public void logNestedMarker() throws InterruptedException {
        Marker notifyMarker = MarkerFactory.getMarker("NOTIFY");
        Marker sendEmailMarker = MarkerFactory.getMarker("SEND_EMAIL");
        sendEmailMarker.add(notifyMarker);
        LOG.debug(sendEmailMarker, "Test the nested marker 1.");
        LOG.debug(sendEmailMarker, "Test the nested marker 2.");

        Thread.sleep(1000); // Wait a moment because these log is being appended asynchronous...
    }

    @Test
    public void logThrowable() throws InterruptedException {
        LOG.info("Without Exception.");
        LOG.error("Test the checked Exception.", new IOException("Connection something"));
        LOG.warn("Test the unchecked Exception.", new IllegalStateException("Oh your state"));

        Thread.sleep(1000); // Wait a moment because these log is being appended asynchronous...
    }

    @Test
    public void logMapMarker() throws InterruptedException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        MapMarker mapMarker = new MapMarker("MAP_MARKER", map);
        LOG.debug(mapMarker, "Test the marker map.");

        Thread.sleep(1000); // Wait a moment because these log is being appended asynchronous...
    }

    @Test
    public void logNestedMapMarker() throws InterruptedException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        Marker notifyMarker = MarkerFactory.getMarker("NOTIFY");
        notifyMarker.add(new MapMarker("MAP_MARKER", map));
        LOG.debug(notifyMarker, "Test the nested marker map.");

        Thread.sleep(1000); // Wait a moment because these log is being appended asynchronous...
    }

    @Test
    public void logDecidedByAppendersMarkerFilter() throws InterruptedException {
        Marker alertMarker = MarkerFactory.getMarker("SECURITY_ALERT");
        LOG.debug(alertMarker, "Test alert filter.");

        Thread.sleep(1000); // Wait a moment because these log is being appended asynchronous...
    }
}
