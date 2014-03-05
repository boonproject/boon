package org.boon.logging;

import java.util.Arrays;

/**
 * Created by Richard on 3/5/14.
 */
public class LogRecord {

    public final Object[] messages;
    public final Throwable throwable;
    public final LogLevel level;
    public final boolean before;



    public static LogRecord before(Object[] messages, Throwable throwable, LogLevel level) {
        return new LogRecord(messages, throwable, level, true);
    }


    public static LogRecord after(Object[] messages, Throwable throwable, LogLevel level) {
        return new LogRecord(messages, throwable, level, false);

    }


    public static LogRecord before(Object[] messages,  LogLevel level) {
        return new LogRecord(messages, null, level, true);

    }


    public static LogRecord after(Object[] messages, LogLevel level) {
        return new LogRecord(messages, null, level, false);

    }


    public LogRecord(Object[] messages, Throwable throwable, LogLevel level, boolean before) {
        this.messages = messages;
        this.throwable = throwable;
        this.level = level;
        this.before = before;
    }

    public LogRecord() {
        messages = null;
        throwable = null;
        level = null;
        before = false;
    }

    public Object[] getMessages() {
        return messages;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public LogLevel getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "LogRecord{" +
                "messages=" + Arrays.toString(messages) +
                ", throwable=" + throwable +
                ", level=" + level +
                '}';
    }
}
