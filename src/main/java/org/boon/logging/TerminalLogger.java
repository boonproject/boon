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


import static org.boon.Boon.puts;
import static org.boon.logging.LogLevel.*;
import static org.boon.logging.LogLevel.DEBUG;
import static org.boon.logging.LogLevel.TRACE;

/**
 * Created by Richard on 3/5/14.
 */
public class TerminalLogger implements LoggerDelegate {



    private LogLevel level = DEBUG;


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
    public TerminalLogger level(LogLevel level) {
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
