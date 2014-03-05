package org.boon.logging;

/**
 * Created by Richard on 3/5/14.
 */
public interface Logger {

    boolean infoOn();

    boolean debugOn();

    boolean traceOn();

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
}
