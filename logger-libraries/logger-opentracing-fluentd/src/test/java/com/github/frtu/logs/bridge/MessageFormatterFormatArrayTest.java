package com.github.frtu.logs.bridge;

import org.junit.Test;
import org.slf4j.helpers.MessageFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Frédéric TU
 * @see <a href="https://github.com/frtu/SimpleToolbox/blob/master/SimpleLogs/src/test/java/com/github/frtu/simple/logs/bridge/MessageFormatterFormatArrayTest.java">Moved from old project SimpleToolbox</a>
 * @since 0.9.5
 */
public class MessageFormatterFormatArrayTest {
    @Test
    public void testFormat() {
        String messageFormat = "arg1={} arg2={} final text";
        Object[] arguments = new Object[]{1, "2"};
        String format = MessageFormatter.arrayFormat(messageFormat, arguments).getMessage();
        assertEquals("arg1=1 arg2=2 final text", format);
    }

//    @Test
//    public void testFormatWithDate() {
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
//        calendar.set(2014, 11, 31, 23, 59, 59);
//        calendar.set(Calendar.MILLISECOND, 999);
//        String date = "2014-12-31T23:59:59.999Z";
//
//        String messageFormat = "arg1={} arg2={} final text";
//        Object[] arguments = new Object[] { 1, calendar };
//
//        String format = MessageFormatter.arrayFormat(messageFormat, arguments).getMessage();
//        assertEquals("arg1=1 arg2=" + date + " final text", format);
//    }

    @Test
    public void testFormatWithPlaceholderAtEdges() {
        String messageFormat = "{} arg2={}";
        Object[] arguments = new Object[]{1, "2"};
        String format = MessageFormatter.arrayFormat(messageFormat, arguments).getMessage();
        assertEquals("1 arg2=2", format);
    }

    @Test
    public void testFormatNullOrEmpty() {
        String messageFormat = "{} arg2={}";
        Object[] arguments = new Object[]{1, "2"};

        assertNull(MessageFormatter.arrayFormat(null, arguments).getMessage());
        assertEquals(messageFormat, MessageFormatter.arrayFormat(messageFormat, null).getMessage());
        assertEquals(messageFormat, MessageFormatter.arrayFormat(messageFormat, new Object[0]).getMessage());
    }

    @Test
    public void testFormatAsymetric() {
        String messageFormat = "arg1={} arg2={} final text";
        Object[] arguments = new Object[]{1};
        String format = MessageFormatter.arrayFormat(messageFormat, arguments).getMessage();
        assertEquals("arg1=1 arg2={} final text", format);
    }
}