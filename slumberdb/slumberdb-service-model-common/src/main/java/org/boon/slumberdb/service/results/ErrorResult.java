package org.boon.slumberdb.service.results;


import org.boon.Str;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.DataStoreSource;

import static org.boon.slumberdb.service.protocol.ProtocolConstants.DELIMITER;

public class ErrorResult extends Result {
    private String title;

    public ErrorResult(long messageId, String clientId, DataStoreSource source, String title) {
        super(messageId, clientId, source);
        this.title = title;
    }

    public ErrorResult() {
    }

    @Override
    public String toString() {
        return "ErrorResult{" +
                "clientId='" + clientId + '\'' +
                ", source=" + source +
                ", message=" + title +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String toTextMessage() {
        return Str.join(DELIMITER, ProtocolConstants.RESPONSE, "" + messageId, clientId, source.toString(), title);
    }
}
