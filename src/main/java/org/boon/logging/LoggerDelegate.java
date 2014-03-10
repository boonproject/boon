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
