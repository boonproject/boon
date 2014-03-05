package org.boon.logging;


import static org.boon.Boon.puts;
import static org.boon.logging.LogLevel.*;
import static org.boon.logging.LogLevel.DEBUG;
import static org.boon.logging.LogLevel.TRACE;

/**
 * Created by Richard on 3/5/14.
 */
public class TerminalLogger implements LoggerDelegate {



    private LogLevel level;


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
    public void level(LogLevel level) {
        this.level = level;
    }

    @Override
    public void turnOff() {

    }

    @Override
    public void fatal(Object... messages) {
        if (level.ordinal() <= FATAL.ordinal()) {
            puts(FATAL, messages);
        }
    }

    @Override
    public void fatal(Throwable t, Object... messages) {
        if (level.ordinal() <= FATAL.ordinal()) {
            puts(FATAL, t, messages);
        }

    }

    @Override
    public void error(Object... messages) {
        if (level.ordinal() <= ERROR.ordinal()) {
            puts(ERROR, messages);
        }

    }

    @Override
    public void error(Throwable t, Object... messages) {
        if (level.ordinal() <= ERROR.ordinal()) {
            puts(ERROR, t, messages);
        }

    }

    @Override
    public void warn(Object... messages) {
        if (level.ordinal() <= WARN.ordinal()) {
            puts(WARN,  messages);
        }

    }

    @Override
    public void warn(Throwable t, Object... messages) {
        if (level.ordinal() <= WARN.ordinal()) {
            puts(WARN, t, messages);
        }

    }

    @Override
    public void info(Object... messages) {
        if (level.ordinal() <= INFO.ordinal()) {
            puts(INFO,  messages);
        }

    }

    @Override
    public void info(Throwable t, Object... messages) {
        if (level.ordinal() <= INFO.ordinal()) {
            puts(INFO, t, messages);
        }

    }

    @Override
    public void config(Object... messages) {
        if (level.ordinal() <= CONFIG.ordinal()) {
            puts(CONFIG, messages);
        }

    }

    @Override
    public void config(Throwable t, Object... messages) {
        if (level.ordinal() <= CONFIG.ordinal()) {
            puts(CONFIG, t, messages);
        }

    }

    @Override
    public void debug(Object... messages) {
        if (level.ordinal() <= DEBUG.ordinal()) {
            puts(DEBUG, messages);
        }

    }

    @Override
    public void debug(Throwable t, Object... messages) {
        if (level.ordinal() <= DEBUG.ordinal()) {
            puts(DEBUG, t, messages);
        }

    }

    @Override
    public void trace(Object... messages) {
        if (level.ordinal() <= TRACE.ordinal()) {
            puts(TRACE, messages);
        }

    }

    @Override
    public void trace(Throwable t, Object... messages) {
        if (level.ordinal() <= TRACE.ordinal()) {
            puts(TRACE, t, messages);
        }

    }

    @Override
    public void fatal(Object message) {
        if (level.ordinal() <= FATAL.ordinal()) {
            puts(FATAL, message);
        }

    }

    @Override
    public void fatal(Object message, Throwable t) {
        if (level.ordinal() <= FATAL.ordinal()) {
            puts(FATAL, t, message);
        }

    }

    @Override
    public void error(Object message) {
        if (level.ordinal() <= ERROR.ordinal()) {
            puts(ERROR, message);
        }

    }

    @Override
    public void error(Object message, Throwable t) {
        if (level.ordinal() <= ERROR.ordinal()) {
            puts(ERROR, t, message);
        }

    }

    @Override
    public void warn(Object message) {
        if (level.ordinal() <= WARN.ordinal()) {
            puts(WARN, message);
        }

    }

    @Override
    public void warn(Object message, Throwable t) {
        if (level.ordinal() <= WARN.ordinal()) {
            puts(WARN, t, message);
        }

    }

    @Override
    public void info(Object message) {
        if (level.ordinal() <= INFO.ordinal()) {
            puts(INFO,  message);
        }

    }

    @Override
    public void info(Object message, Throwable t) {
        if (level.ordinal() <= INFO.ordinal()) {
            puts(INFO, t, message);
        }

    }

    @Override
    public void debug(Object message) {
        if (level.ordinal() <= DEBUG.ordinal()) {
            puts(DEBUG, message);
        }

    }

    @Override
    public void debug(Object message, Throwable t) {
        if (level.ordinal() <= DEBUG.ordinal()) {
            puts(DEBUG, t, message);
        }

    }

    @Override
    public void trace(Object message) {
        if (level.ordinal() <= TRACE.ordinal()) {
            puts(TRACE,  message);
        }

    }

    @Override
    public void trace(Object message, Throwable t) {
        if (level.ordinal() <= TRACE.ordinal()) {
            puts(TRACE, t, message);
        }
    }

}
