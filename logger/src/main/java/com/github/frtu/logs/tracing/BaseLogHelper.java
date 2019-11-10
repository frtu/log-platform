package com.github.frtu.logs.tracing;

public abstract class BaseLogHelper implements LogHelper {
    @Override
    public LogHelper addKey(String keyName, Boolean value) {
        return addKey(keyName, value, Boolean.class);
    }

    @Override
    public LogHelper addKey(String keyName, Short value) {
        return addKey(keyName, value, Short.class);
    }

    @Override
    public LogHelper addKey(String keyName, Integer value) {
        return addKey(keyName, value, Integer.class);
    }

    @Override
    public LogHelper addKey(String keyName, Long value) {
        return addKey(keyName, value, Long.class);
    }

    @Override
    public LogHelper addKey(String keyName, Float value) {
        return addKey(keyName, value, Float.class);
    }

    @Override
    public LogHelper addKey(String keyName, Double value) {
        return addKey(keyName, value, Double.class);
    }

    @Override
    public LogHelper addKey(String keyName, Byte value) {
        return addKey(keyName, value, Byte.class);
    }

    @Override
    public LogHelper addKey(String keyName, Character value) {
        return addKey(keyName, value, Character.class);
    }

    @Override
    public LogHelper addKey(String keyName, String value) {
        return addKey(keyName, value, String.class);
    }

    @Override
    public LogHelper addData(String keyName, Boolean value) {
        return addData(keyName, value, Boolean.class);
    }

    @Override
    public LogHelper addData(String keyName, Short value) {
        return addData(keyName, value, Short.class);
    }

    @Override
    public LogHelper addData(String keyName, Integer value) {
        return addData(keyName, value, Integer.class);
    }

    @Override
    public LogHelper addData(String keyName, Long value) {
        return addData(keyName, value, Long.class);
    }

    @Override
    public LogHelper addData(String keyName, Float value) {
        return addData(keyName, value, Float.class);
    }

    @Override
    public LogHelper addData(String keyName, Double value) {
        return addData(keyName, value, Double.class);
    }

    @Override
    public LogHelper addData(String keyName, Byte value) {
        return addData(keyName, value, Byte.class);
    }

    @Override
    public LogHelper addData(String keyName, Character value) {
        return addData(keyName, value, Character.class);
    }

    @Override
    public LogHelper addData(String keyName, String value) {
        return addData(keyName, value, String.class);
    }
}
