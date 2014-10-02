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

import org.boon.Exceptions;
import org.boon.primitive.CharBuf;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

import java.io.PrintWriter;
import java.util.logging.LogRecord;

import static org.boon.Exceptions.getFilteredStackTrace;

/**
 * Created by Richard on 3/5/14.
 * All of the logging here was heavily inspired by the logging that is in Vertx.
 *
 *
 * I believe at all times the usage was small enough and derived enough to be covered under "fair use".
 * And now the work has been so derived and changed that it is a different beast.
 */
public class BoonLogFormatter  extends java.util.logging.Formatter {
    private static String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String format(final LogRecord record) {

        CharBuf sb = CharBuf.create(255);
        sb.jsonDate(record.getMillis());

        sb.add("[").add(Thread.currentThread().getName()).append("]");


//        JavaLangAccess access = SharedSecrets.getJavaLangAccess();
//        Throwable throwable = new Throwable();
//        int depth = access.getStackTraceDepth(throwable);
//
//        boolean lookingForLogger = true;
//        for (int index = 0; index < depth; index++) {
//            // prevents
//            // from paying building the entire stack frame.
//            StackTraceElement frame =
//                    access.getStackTraceElement(throwable, index);
//            String cname = frame.getClassName();
//

        sb.add(record.getLevel()).add(" [");
        sb.add(record.getLoggerName()).add("]").add("  ");
        sb.add(record.getMessage());

        sb.append(LINE_SEPARATOR);



        if (record.getThrown()!=null) {



            StackTraceElement[] filteredStackTrace = getFilteredStackTrace(record.getThrown().getStackTrace());

            if (filteredStackTrace.length> 0) {
                Exceptions.stackTraceToJson(sb, filteredStackTrace );
            }


                PrintWriter pw = new PrintWriter(sb);
                record.getThrown().printStackTrace(pw);
                pw.close();


            record.getThrown().printStackTrace(sb);
        }
        return sb.toString();
    }

}
