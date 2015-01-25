package org.boon.slumberdb.stores.log;

import org.boon.Exceptions;
import org.boon.json.JsonParserFactory;

public class LogEntry implements Comparable<LogEntry> {
    public long time;
    public long index;
    public long messageId;
    public String clientId;
    public String key;
    public String value;

    public LogEntry() {
    }

    public LogEntry(long time, long index, long messageId, String clientId, String key, String value) {
        this.time = time;
        this.index = index;
        this.messageId = messageId;
        this.clientId = clientId;
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(LogEntry o) {
            return Long.compare(time, o.time);
        }

    public static LogEntry load(String json) {
        try {
            return new JsonParserFactory().create().parse(LogEntry.class, json);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Exceptions.handle(ex, "Unable to parse LogEntry json: ", json);
            return null;
        }
    }
}
