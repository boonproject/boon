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

/**
 * Created by Richard on 3/5/14.
 */
public final class TeeLoggerWrapper extends ConfigurableLogger {
    private final LoggerDelegate logger;

    public static final HandlerNoOP noOP = new HandlerNoOP();

    public TeeLoggerWrapper(LoggerDelegate delegate1, LoggerDelegate delegate2, Handler<LogRecord> logRecordHandler) {
        super(delegate1, logRecordHandler);
        this.logger = delegate2;
    }


    public TeeLoggerWrapper(LoggerDelegate delegate1, LoggerDelegate delegate2) {
        super(delegate1, noOP);
        this.logger = delegate2;
    }

    @Override
    public boolean infoOn() {
        return super.infoOn();
    }

    @Override
    public boolean debugOn() {
        return super.debugOn();
    }

    @Override
    public boolean traceOn() {
        return super.traceOn();
    }

    @Override
    public void fatal(Object message) {
        super.fatal(message);
        logger.fatal(message);

    }

    @Override
    public void fatal(Object message, Throwable throwable) {

        super.fatal(message, throwable);
        logger.fatal(message, throwable);

    }

    @Override
    public void error(Object message) {
        super.error(message);
        logger.error(message);

    }

    @Override
    public void error(Object message, Throwable throwable) {
        super.error(message, throwable);
        logger.error(message, throwable);

    }

    @Override
    public void warn(Object message) {
        super.warn(message);
        logger.warn(message);

    }

    @Override
    public void warn(Object message, Throwable throwable) {
        super.warn(message, throwable);
        logger.warn(message, throwable);

    }

    @Override
    public void info(Object message) {
        super.info(message);
        logger.info(message);

    }

    @Override
    public void info(Object message, Throwable throwable) {
        super.info(message, throwable);
        logger.info(message, throwable);

    }

    @Override
    public void debug(Object message) {
        super.debug(message);
        logger.debug(message);

    }

    @Override
    public void debug(Object message, Throwable throwable) {
        super.debug(message, throwable);
        logger.debug(message, throwable);

    }

    @Override
    public void trace(Object message) {
        super.trace(message);
        logger.trace(message);

    }

    @Override
    public void trace(Object message, Throwable throwable) {
        super.trace(message, throwable);
        logger.trace(message, throwable);

    }

    @Override
    public LoggerDelegate level(LogLevel level) {
        super.level(level);
        logger.level(level);
        return this;

    }

    @Override
    public LoggerDelegate turnOff() {
        super.turnOff();
        logger.turnOff();
        return this;
    }

    @Override
    public void fatal(Object... messages) {
        super.fatal(messages);
        logger.fatal(messages);

    }

    @Override
    public void fatal(Throwable throwable, Object... messages) {
        super.fatal(throwable, messages);
        logger.fatal(throwable, messages);

    }

    @Override
    public void error(Object... messages) {
        super.error(messages);
        logger.error(messages);

    }

    @Override
    public void error(Throwable throwable, Object... messages) {
        super.error(throwable, messages);
        logger.error(throwable, messages);

    }

    @Override
    public void warn(Object... messages) {
        super.warn(messages);
        logger.warn(messages);

    }

    @Override
    public void warn(Throwable throwable, Object... messages) {
        super.warn(throwable, messages);
        logger.warn(throwable, messages);

    }

    @Override
    public void info(Object... messages) {
        super.info(messages);
        logger.info(messages);

    }

    @Override
    public void info(Throwable throwable, Object... messages) {
        super.info(throwable, messages);
        logger.info(throwable, messages);

    }

    @Override
    public void config(Object... messages) {
        super.config(messages);
        logger.config(messages);

    }

    @Override
    public void config(Throwable throwable, Object... messages) {
        super.config(throwable, messages);
        logger.config(throwable, messages);

    }

    @Override
    public void debug(Object... messages) {
        super.debug(messages);
        logger.debug(messages);

    }

    @Override
    public void debug(Throwable throwable, Object... messages) {
        super.debug(throwable, messages);
        logger.debug(throwable, messages);

    }

    @Override
    public void trace(Object... messages) {
        super.trace(messages);
        logger.trace(messages);

    }

    @Override
    public void trace(Throwable throwable, Object... messages) {
        super.trace(throwable, messages);
        logger.trace(throwable, messages);

    }
}
