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


import java.util.logging.Level;

import static org.boon.Boon.sputs;
import static org.boon.Str.str;

public class JDKLogger implements LoggerDelegate {
    private final java.util.logging.Logger logger;




    JDKLogger(final String name) {
        logger = java.util.logging.Logger.getLogger(name);

    }

    @Override
    public boolean infoOn() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public boolean debugOn() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean traceOn() {
        return logger.isLoggable(Level.FINEST);
    }


    @Override
    public void fatal(Object... messages) {
        logger.log(Level.SEVERE, sputs(messages));
    }

    @Override
    public void fatal(Throwable t, Object... messages) {

        logger.log(Level.SEVERE, sputs(messages), t);
    }


    public void fatal(final Exception message) {
        logger.log(Level.SEVERE, "", message);
    }


    public void error(final Exception message) {
        logger.log(Level.SEVERE, "", message);
    }


    public void warn(final Exception message) {
        logger.log(Level.WARNING, "", message);
    }

    @Override
    public void error(Object... messages) {

        logger.log(Level.SEVERE, sputs(messages));

    }

    @Override
    public void error(Throwable t, Object... messages) {

        logger.log(Level.SEVERE, sputs(messages), t);
    }

    @Override
    public void warn(Object... messages) {


        logger.log(Level.WARNING, sputs(messages));

    }

    @Override
    public void warn(Throwable t, Object... messages) {

        logger.log(Level.WARNING, sputs(messages), t);

    }

    @Override
    public void info(Object... messages) {


        logger.log(Level.INFO, sputs(messages));
    }

    @Override
    public void info(Throwable t, Object... messages) {

        logger.log(Level.INFO, sputs(messages), t);

    }


    @Override
    public void config(Object... messages) {


        logger.log(Level.CONFIG, sputs(messages));
    }

    @Override
    public void config(Throwable t, Object... messages) {

        logger.log(Level.CONFIG, sputs(messages), t);

    }

    @Override
    public void debug(Object... messages) {

        logger.log(Level.FINE, sputs(messages));

    }

    @Override
    public void debug(Throwable t, Object... messages) {

        logger.log(Level.FINE, sputs(messages), t);

    }

    @Override
    public void trace(Object... messages) {

        logger.log(Level.FINEST, sputs(messages));

    }

    @Override
    public void trace(Throwable t, Object... messages) {

        logger.log(Level.FINEST, sputs(messages), t);

    }


    public LogLevel level() {
        Level level = logger.getLevel();
        if (level == Level.FINE) {
            return LogLevel.DEBUG;
        } else if (level == Level.FINEST) {
            return LogLevel.TRACE;
        } else if (level == Level.CONFIG) {
            return LogLevel.CONFIG;
        }  else if (level == Level.INFO) {
            return LogLevel.INFO;
        } else if (level == Level.WARNING) {
            return LogLevel.WARN;
        } else if (level == Level.SEVERE) {
            return LogLevel.FATAL;
        }
        return LogLevel.ERROR;
    }

    public LoggerDelegate level(LogLevel level) {
        switch (level) {
            case DEBUG:
                logger.setLevel( Level.FINE );
                break;

            case TRACE:
                logger.setLevel( Level.FINEST );
                break;

            case CONFIG:
                logger.setLevel( Level.CONFIG );
                break;

            case INFO:
                logger.setLevel( Level.INFO );
                break;

            case WARN:
                logger.setLevel( Level.WARNING );
                break;


            case ERROR:
                logger.setLevel( Level.SEVERE );
                break;

            case FATAL:
                logger.setLevel( Level.SEVERE );
                break;

        }
        return this;
    }


    public LoggerDelegate turnOff() {
        logger.setLevel(Level.OFF);
        return this;
    }


    public void fatal(final Object message) {
        logger.log(Level.SEVERE, str(message));
    }

    public void fatal(final Object message, final Throwable t) {
        logger.log(Level.SEVERE, str(message) , t);
    }

    public void error(final Object message) {
        logger.log(Level.SEVERE, str(message));
    }

    public void error(final Object message, final Throwable t) {
        logger.log(Level.SEVERE, str(message), t);

    }

    public void warn(final Object message) {
        logger.log(Level.WARNING, str(message));
    }

    public void warn(final Object message, final Throwable t) {
        logger.log(Level.WARNING, str(message), t);
    }

    public void info(final Object message) {
        logger.log(Level.INFO, str(message));
    }

    public void info(final Object message, final Throwable t) {
        logger.log(Level.INFO, str(message), t);
    }

    public void debug(final Object message) {
        logger.log(Level.FINE, str(message));
    }

    public void debug(final Object message, final Throwable t) {
        logger.log(Level.FINE, str(message), t);
    }

    public void trace(final Object message) {
        logger.log(Level.FINEST, str(message));
    }

    public void trace(final Object message, final Throwable t) {
        logger.log(Level.FINEST, str(message), t);
    }
}
