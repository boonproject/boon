package org.boon;


import org.boon.core.Handler;
import org.boon.logging.*;

/**
 * <p>This class allows isolation of all logging dependencies in one place.
 *
 * There is 0 dependencies on third party logs</p>
 *
 * <p>By default logging uses uses JDK logging.
 * The logging configuration file (logging.properties).
 * You can use standard JDK logging config.</p>
 *
 * I wrote similar facilities in Crank and EasyJava,
 * but this style was heavily inspired by Vertx which was inspired by JBoss.
 *
 * @author Rick Hightower
 */
public class Logger {


    private transient volatile LoggerDelegate logger;
    final LoggerDelegate original;


    public Logger(final LoggerDelegate delegate) {
        logger = delegate;
        original = logger;
    }

    public synchronized void tee(LoggerDelegate newLogger) {
        logger = new TeeLoggerWrapper(logger, newLogger);
    }


    public synchronized void handler(Handler<LogRecord> handler) {
        logger = new ConfigurableLogger(logger, handler);
    }


    public synchronized void teeAndHandler(LoggerDelegate newLogger, Handler<LogRecord> handler) {
        logger = new TeeLoggerWrapper(logger, newLogger);
    }


    public synchronized void unwrap() {
        logger = original;
    }

    public boolean infoOn() {
        return logger.infoOn();
    }

    public boolean debugOn() {
        return logger.debugOn();
    }

    public boolean traceOn() {
        return logger.traceOn();
    }

    public void fatal(final Object message) {
        logger.fatal(message);
    }

    public void fatal(final Object message, final Throwable t) {
        logger.fatal(message, t);
    }

    public void error(final Object message) {
        logger.error(message);
    }

    public void error(final Object message, final Throwable t) {
        logger.error(message, t);
    }

    public void warn(final Object message) {
        logger.warn(message);
    }

    public void warn(final Object message, final Throwable t) {
        logger.warn(message, t);
    }

    public void info(final Object message) {
        logger.info(message);
    }

    public void info(final Object message, final Throwable t) {
        logger.info(message, t);
    }

    public void debug(final Object message) {
        logger.debug(message);
    }

    public void debug(final Object message, final Throwable t) {
        logger.debug(message, t);
    }

    public void trace(final Object message) {
        logger.trace(message);
    }

    public void trace(final Object message, final Throwable t) {
        logger.trace(message, t);
    }


    public void level(LogLevel level) {
        logger.level(level);
    }



    public void turnOff() {
        logger.turnOff();
    }

    public void fatal(Object... messages) {
        logger.fatal(messages);
    }

    public void fatal(Throwable t, Object... messages) {
        logger.fatal(t, messages);
    }

    public void error(Object... messages) {
        logger.error(messages);
    }

    public void error(Throwable t, Object... messages) {
        logger.error(t, messages);
    }

    public void warn(Object... messages) {
        logger.warn(messages);
    }

    public void warn(Throwable t, Object... messages) {

        logger.warn(t, messages);
    }

    public void info(Object... messages) {
        logger.info(messages);
    }

    public void info(Throwable t, Object... messages) {
        logger.info(t, messages);
    }

    public void config(Object... messages) {
        logger.config(messages);
    }

    public void config(Throwable t, Object... messages) {
        logger.config(t, messages);
    }

    public void debug(Object... messages) {
        logger.debug(messages);
    }

    public void debug(Throwable t, Object... messages) {
        logger.debug(t, messages);
    }

    public void trace(Object... messages) {
        logger.trace(messages);

    }

    public void trace(Throwable t, Object... messages) {
        logger.trace(t, messages);

    }

}