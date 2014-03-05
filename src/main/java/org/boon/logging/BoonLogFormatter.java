package org.boon.logging;

import org.boon.Exceptions;
import org.boon.primitive.CharBuf;

import java.io.PrintWriter;
import java.util.logging.LogRecord;

import static org.boon.Exceptions.getFilteredStackTrace;

/**
 * Created by Richard on 3/5/14.
 */
public class BoonLogFormatter  extends java.util.logging.Formatter {
    private static String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String format(final LogRecord record) {

        CharBuf sb = CharBuf.create(255);
        sb.jsonDate(record.getMillis());

        sb.add("[").add(Thread.currentThread().getName()).append("]");


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

        }
        return sb.toString();
    }

}
