package org.boon.logging;

import org.boon.core.Handler;

import static org.boon.Arrays.array;
import static org.boon.logging.LogRecord.after;
import static org.boon.logging.LogRecord.before;

/**
 * Created by Richard on 3/5/14.
 */
public class ConfigurableLogger implements LoggerDelegate{


    private transient volatile LoggerDelegate logger;
    private final Handler<LogRecord> handler;
    final LoggerDelegate original;

    public static final HandlerNoOP noOP = new HandlerNoOP();



    public synchronized void unwrap() {
        logger = original;
    }

    public ConfigurableLogger(final LoggerDelegate delegate, Handler<LogRecord> logRecordHandler) {
        this.logger = delegate;
        this.handler = logRecordHandler;
        original = logger;
    }


    public ConfigurableLogger(final LoggerDelegate delegate) {
        this.logger = delegate;
        this.handler = noOP;
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
        handler.handle(before(array(message), LogLevel.FATAL));
        logger.fatal(message);

        handler.handle(after(array(message), LogLevel.FATAL));
    }

    public void fatal(final Object message, final Throwable throwable) {

        handler.handle(before(array(message), throwable, LogLevel.FATAL));
        logger.fatal(message, throwable);

        handler.handle(after(array(message), throwable, LogLevel.FATAL));
    }

    public void error(final Object message) {

        handler.handle(before(array(message), LogLevel.ERROR));
        logger.error(message);

        handler.handle(after(array(message), LogLevel.ERROR));
    }

    public void error(final Object message, final Throwable throwable) {

        handler.handle(before(array(message), throwable, LogLevel.ERROR));
        logger.error(message, throwable);

        handler.handle(after(array(message), throwable, LogLevel.ERROR));
    }

    public void warn(final Object message) {

        handler.handle(before(array(message), LogLevel.WARN));
        logger.warn(message);

        handler.handle(after(array(message), LogLevel.WARN));
    }

    public void warn(final Object message, final Throwable throwable) {

        handler.handle(before(array(message), throwable, LogLevel.WARN));
        logger.warn(message, throwable);

        handler.handle(after(array(message), throwable, LogLevel.WARN));
    }

    public void info(final Object message) {

        handler.handle(before(array(message), LogLevel.INFO));
        logger.info(message);

        handler.handle(after(array(message), LogLevel.INFO));
    }

    public void info(final Object message, final Throwable throwable) {

        handler.handle(before(array(message), throwable, LogLevel.INFO));
        logger.info(message, throwable);

        handler.handle(after(array(message), throwable, LogLevel.INFO));
    }

    public void debug(final Object message) {

        handler.handle(before(array(message), LogLevel.DEBUG));
        logger.debug(message);
        handler.handle(after(array(message), LogLevel.DEBUG));

    }

    public void debug(final Object message, final Throwable throwable) {

        handler.handle(before(array(message), throwable, LogLevel.DEBUG));
        logger.debug(message, throwable);
        handler.handle(after(array(message), throwable, LogLevel.DEBUG));
    }

    public void trace(final Object message) {

        handler.handle(before(array(message), LogLevel.TRACE));
        logger.trace(message);

        handler.handle(after(array(message), LogLevel.TRACE));
    }

    public void trace(final Object message, final Throwable throwable) {

        handler.handle(before(array(message), throwable, LogLevel.TRACE));
        logger.trace(message, throwable);
        handler.handle(after(array(message), throwable, LogLevel.TRACE));
    }


    public void level(LogLevel level) {
        logger.level(level);
    }



    public void turnOff() {
        logger.turnOff();
    }

    public void fatal(Object... messages) {

        handler.handle(before(messages, LogLevel.FATAL));
        logger.fatal(messages);

        handler.handle(after(messages, LogLevel.FATAL));
    }

    public void fatal(Throwable throwable, Object... messages) {

        handler.handle(before(messages, throwable, LogLevel.FATAL));
        logger.fatal(throwable, messages);

        handler.handle(after(messages, throwable, LogLevel.FATAL));
    }

    public void error(Object... messages) {

        handler.handle(before(messages, LogLevel.ERROR));
        logger.error(messages);

        handler.handle(after(messages, LogLevel.ERROR));
    }

    public void error(Throwable throwable, Object... messages) {

        handler.handle(before(messages, throwable, LogLevel.ERROR));
        logger.error(throwable, messages);

        handler.handle(after(messages, throwable, LogLevel.ERROR));
    }

    public void warn(Object... messages) {

        handler.handle(before(messages, LogLevel.WARN));
        logger.warn(messages);

        handler.handle(after(messages, LogLevel.WARN));
    }

    public void warn(Throwable throwable, Object... messages) {

        handler.handle(before(messages, throwable, LogLevel.WARN));

        logger.warn(throwable, messages);

        handler.handle(after(messages, throwable, LogLevel.WARN));
    }

    public void info(Object... messages) {

        handler.handle(before(messages, LogLevel.INFO));
        logger.info(messages);

        handler.handle(after(messages, LogLevel.INFO));
    }

    public void info(Throwable throwable, Object... messages) {

        handler.handle(before(messages, throwable, LogLevel.INFO));
        logger.info(throwable, messages);

        handler.handle(after(messages, throwable, LogLevel.INFO));
    }

    public void config(Object... messages) {

        handler.handle(before(messages, LogLevel.CONFIG));
        logger.config(messages);

        handler.handle(after(messages, LogLevel.CONFIG));
    }

    public void config(Throwable throwable, Object... messages) {

        handler.handle(before(messages, throwable, LogLevel.CONFIG));
        logger.config(throwable, messages);

        handler.handle(after(messages, throwable, LogLevel.CONFIG));
    }

    public void debug(Object... messages) {

        handler.handle(before(messages, LogLevel.DEBUG));
        logger.debug(messages);

        handler.handle(after(messages, LogLevel.DEBUG));
    }

    public void debug(Throwable throwable, Object... messages) {

        handler.handle(before(messages, throwable, LogLevel.DEBUG));
        logger.debug(throwable, messages);

        handler.handle(after(messages, throwable, LogLevel.DEBUG));
    }

    public void trace(Object... messages) {

        handler.handle(before(messages, LogLevel.TRACE));
        logger.trace(messages);

        handler.handle(after(messages, LogLevel.TRACE));

    }

    public void trace(Throwable throwable, Object... messages) {

        handler.handle(before(messages, throwable, LogLevel.TRACE));
        logger.trace(throwable, messages);

        handler.handle(after(messages, throwable, LogLevel.TRACE));

    }


}
