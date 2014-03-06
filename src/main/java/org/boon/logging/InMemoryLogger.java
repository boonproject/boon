package org.boon.logging;

import org.boon.primitive.CharBuf;
import static org.boon.logging.LogLevel.*;

/**
 * Created by Richard on 3/5/14.
 */
public class InMemoryLogger implements LoggerDelegate {

    private LogLevel level = DEBUG;

    private CharBuf buf = CharBuf.create(100);


    /**
     *
     *
     * @return
     */
    @Override
    public boolean infoOn() {
        switch (level) {
            case INFO:
            case ERROR:
            case FATAL:
            case WARN:
                return true;
        }
        return false;
    }

    @Override
    public boolean debugOn() {
        switch (level) {
            case INFO:
            case ERROR:
            case FATAL:
            case WARN:
            case CONFIG:
            case DEBUG:
                return true;
        }
        return false;
    }

    @Override
    public boolean traceOn() {
        switch (level) {
            case INFO:
            case ERROR:
            case FATAL:
            case WARN:
            case CONFIG:
            case DEBUG:
            case TRACE:
                return true;
        }
        return false;
    }

    @Override
    public LoggerDelegate level(LogLevel level) {
        this.level = level;
        return this;
    }

    @Override
    public LoggerDelegate turnOff() {
        this.level = OFF;
        return this;
    }

    @Override
    public void fatal(Object... messages) {
        if (level.ordinal() <= FATAL.ordinal()) {
            buf.puts(FATAL, messages);
        }
    }

    @Override
    public void fatal(Throwable t, Object... messages) {
        if (level.ordinal() <= FATAL.ordinal()) {
            buf.puts(FATAL, t, messages);
        }

    }

    @Override
    public void error(Object... messages) {
        if (level.ordinal() <= ERROR.ordinal()) {
            buf.puts(ERROR, messages);
        }

    }

    @Override
    public void error(Throwable t, Object... messages) {
        if (level.ordinal() <= ERROR.ordinal()) {
            buf.puts(ERROR, t, messages);
        }

    }

    @Override
    public void warn(Object... messages) {
        if (level.ordinal() <= WARN.ordinal()) {
            buf.puts(WARN,  messages);
        }

    }

    @Override
    public void warn(Throwable t, Object... messages) {
        if (level.ordinal() <= WARN.ordinal()) {
            buf.puts(WARN, t, messages);
        }

    }

    @Override
    public void info(Object... messages) {
        if (level.ordinal() <= INFO.ordinal()) {
            buf.puts(INFO,  messages);
        }

    }

    @Override
    public void info(Throwable t, Object... messages) {
        if (level.ordinal() <= INFO.ordinal()) {
            buf.puts(INFO, t, messages);
        }

    }

    @Override
    public void config(Object... messages) {
        if (level.ordinal() <= CONFIG.ordinal()) {
            buf.puts(CONFIG, messages);
        }

    }

    @Override
    public void config(Throwable t, Object... messages) {
        if (level.ordinal() <= CONFIG.ordinal()) {
            buf.puts(CONFIG, t, messages);
        }

    }

    @Override
    public void debug(Object... messages) {
        if (level.ordinal() <= DEBUG.ordinal()) {
            buf.puts(DEBUG, messages);
        }

    }

    @Override
    public void debug(Throwable t, Object... messages) {
        if (level.ordinal() <= DEBUG.ordinal()) {
            buf.puts(DEBUG, t, messages);
        }

    }

    @Override
    public void trace(Object... messages) {
        if (level.ordinal() <= TRACE.ordinal()) {
            buf.puts(TRACE, messages);
        }

    }

    @Override
    public void trace(Throwable t, Object... messages) {
        if (level.ordinal() <= TRACE.ordinal()) {
            buf.puts(TRACE, t, messages);
        }

    }

    @Override
    public void fatal(Object message) {
        if (level.ordinal() <= FATAL.ordinal()) {
            buf.puts(FATAL, message);
        }

    }

    @Override
    public void fatal(Object message, Throwable t) {
        if (level.ordinal() <= FATAL.ordinal()) {
            buf.puts(FATAL, t, message);
        }

    }

    @Override
    public void error(Object message) {
        if (level.ordinal() <= ERROR.ordinal()) {
            buf.puts(ERROR, message);
        }

    }

    @Override
    public void error(Object message, Throwable t) {
        if (level.ordinal() <= ERROR.ordinal()) {
            buf.puts(ERROR, t, message);
        }

    }

    @Override
    public void warn(Object message) {
        if (level.ordinal() <= WARN.ordinal()) {
            buf.puts(WARN, message);
        }

    }

    @Override
    public void warn(Object message, Throwable t) {
        if (level.ordinal() <= WARN.ordinal()) {
            buf.puts(WARN, t, message);
        }

    }

    @Override
    public void info(Object message) {
        if (level.ordinal() <= INFO.ordinal()) {
            buf.puts(INFO,  message);
        }

    }

    @Override
    public void info(Object message, Throwable t) {
        if (level.ordinal() <= INFO.ordinal()) {
            buf.puts(INFO, t, message);
        }

    }

    @Override
    public void debug(Object message) {
        if (level.ordinal() <= DEBUG.ordinal()) {
            buf.puts(DEBUG, message);
        }

    }

    @Override
    public void debug(Object message, Throwable t) {
        if (level.ordinal() <= DEBUG.ordinal()) {
            buf.puts(DEBUG, t, message);
        }

    }

    @Override
    public void trace(Object message) {
        if (level.ordinal() <= TRACE.ordinal()) {
            buf.puts(TRACE,  message);
        }

    }

    @Override
    public void trace(Object message, Throwable t) {
        if (level.ordinal() <= TRACE.ordinal()) {
            buf.puts(TRACE, t, message);
        }
    }

    public CharBuf getBuf() {
        return buf;
    }
}
