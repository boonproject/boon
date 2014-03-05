package org.boon.logging;

/**
 * Created by Richard on 3/5/14.
 */
public interface LoggerDelegate {

    boolean infoOn();

    boolean debugOn();

    boolean traceOn();

    public void level(LogLevel level);

    public void turnOff();

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

    public void fatal(final Object message);

    public void fatal(final Object message, final Throwable t);

    public void error(final Object message);

    public void error(final Object message, final Throwable t);

    public void warn(final Object message);

    public void warn(final Object message, final Throwable t);

    public void info(final Object message);

    public void info(final Object message, final Throwable t);

    public void debug(final Object message);

    public void debug(final Object message, final Throwable t);

    public void trace(final Object message);

    public void trace(final Object message, final Throwable t);



}
