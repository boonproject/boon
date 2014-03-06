package org.boon.logging;

/**
 * Created by Richard on 3/5/14.
 */
public interface LoggerDelegate {

    boolean infoOn();

    boolean debugOn();

    boolean traceOn();

    LoggerDelegate level(LogLevel level);

    LoggerDelegate turnOff();

    void fatal(Object... messages);

    void fatal(Throwable t, Object... messages);

    void error(Object... messages);

    void error(Throwable t, Object... messages);

    void warn(Object... messages);

    void warn(Throwable t, Object... messages);

    void info(Object... messages);

    void info(Throwable t, Object... messages);

    void config(Object... messages);

    void config(Throwable t, Object... messages);

    void debug(Object... messages);

    void debug(Throwable t, Object... messages);

    void trace(Object... messages);

    void trace(Throwable t, Object... messages);

    void fatal(final Object message);

    void fatal(final Object message, final Throwable t);

    void error(final Object message);

    void error(final Object message, final Throwable t);

    void warn(final Object message);

    void warn(final Object message, final Throwable t);

    void info(final Object message);

    void info(final Object message, final Throwable t);

    void debug(final Object message);

    void debug(final Object message, final Throwable t);

    void trace(final Object message);

    void trace(final Object message, final Throwable t);



}
