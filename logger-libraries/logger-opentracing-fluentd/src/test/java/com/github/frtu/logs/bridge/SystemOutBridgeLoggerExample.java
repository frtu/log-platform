package com.github.frtu.logs.bridge;

import org.slf4j.Logger;

/**
 * Allow to use slf4j for System.out.
 *
 * @author frtu
 */
public class SystemOutBridgeLoggerExample extends AbstractPlaceholderBridgeLogger implements Logger {
    private static final long serialVersionUID = -7058922562582723187L;

    public SystemOutBridgeLoggerExample() {
        super("System.out");
    }

    @Override
    public boolean isTraceEnabled() {
        return isDebugEnabled();
    }

    @Override
    public void trace(String msg) {
        debug(msg);
    }

    @Override
    public void trace(String msg, Throwable t) {
        System.out.println("trace:" + msg + " throwable" + t);
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {
        System.out.println("debug:" + msg);
    }

    @Override
    public void debug(String msg, Throwable t) {
        System.out.println("debug:" + msg + " throwable" + t);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String msg) {
        System.out.println("info:" + msg);
    }

    @Override
    public void info(String msg, Throwable t) {
        System.out.println("info:" + msg + " throwable" + t);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String msg) {
        System.out.println("warn:" + msg);
    }

    @Override
    public void warn(String msg, Throwable t) {
        System.out.println("trace:" + msg + " throwable" + t);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String msg) {
        System.out.println("error:" + msg);
    }

    @Override
    public void error(String msg, Throwable t) {
        System.out.println("error:" + msg + " throwable" + t);
    }
}
