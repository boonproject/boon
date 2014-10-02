/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.logging;

import org.boon.core.Handler;

import static org.boon.primitive.Arry.array;
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


    public LoggerDelegate level(LogLevel level) {
        logger.level(level);
        return this;
    }



    public LoggerDelegate turnOff() {
        logger.turnOff();
        return this;
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
