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

import java.util.Arrays;

/**
 * Created by Richard on 3/5/14.
 */
public class LogRecord {

    public final Object[] messages;
    public final Throwable throwable;
    public final LogLevel level;
    public final boolean before;



    public static LogRecord before(Object[] messages, Throwable throwable, LogLevel level) {
        return new LogRecord(messages, throwable, level, true);
    }


    public static LogRecord after(Object[] messages, Throwable throwable, LogLevel level) {
        return new LogRecord(messages, throwable, level, false);

    }


    public static LogRecord before(Object[] messages,  LogLevel level) {
        return new LogRecord(messages, null, level, true);

    }


    public static LogRecord after(Object[] messages, LogLevel level) {
        return new LogRecord(messages, null, level, false);

    }


    public LogRecord(Object[] messages, Throwable throwable, LogLevel level, boolean before) {
        this.messages = messages;
        this.throwable = throwable;
        this.level = level;
        this.before = before;
    }

    public LogRecord() {
        messages = null;
        throwable = null;
        level = null;
        before = false;
    }

    public Object[] getMessages() {
        return messages;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public LogLevel getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "LogRecord{" +
                "messages=" + Arrays.toString(messages) +
                ", throwable=" + throwable +
                ", level=" + level +
                '}';
    }
}
