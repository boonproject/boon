package org.boon.slumberdb.stores.log;

import org.boon.Exceptions;
import org.boon.json.JsonParserFactory;

public class LogEntry {
    public long messageId;
    public String clientId;
    public String key;
    public String value;

    public LogEntry() {
    }

    public LogEntry(long messageId, String clientId, String key, String value) {
        this.messageId = messageId;
        this.clientId = clientId;
        this.key = key;
        this.value = value;
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
