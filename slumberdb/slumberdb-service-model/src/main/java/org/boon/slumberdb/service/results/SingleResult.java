package org.boon.slumberdb.service.results;


import org.boon.slumberdb.service.protocol.ActionResponse;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Str;

import static org.boon.slumberdb.service.protocol.ProtocolConstants.DELIMITER;
import static org.boon.Exceptions.die;

public class SingleResult extends Result {

    protected String key;
    protected String value;

    public SingleResult(long messageId, String clientId, DataStoreSource source, String key, String value) {
        super(messageId, clientId, source);
        this.key = key;
        this.value = value;
    }


    public SingleResult() {

    }

    public static SingleResult fromTextMessage(String text) {

        SingleResult dataItem = null;

        if (text.startsWith(ActionResponse.GET_RESPONSE.responseHeader())) {
            dataItem = new SingleResult();
        } else if (text.startsWith(ActionResponse.BROADCAST.responseHeader())) {
            dataItem = new BroadcastResult();
        } else {
            die("Unable to parse", text);
        }

        final String[] split = Str.split(text, DELIMITER);
        dataItem.messageId = Long.parseLong(split[1]);
        dataItem.clientId = split[2];
        String src = split[3].intern();
        dataItem.source = Enum.valueOf(DataStoreSource.class, src);
        dataItem.key = split[4];

        if (split.length == 6) {
            dataItem.value = split[5];
        } else {
            dataItem.value = null;
        }
        return dataItem;

    }

    public String key() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String payload() {
        return value;
    }

    @Override
    public String toString() {
        return "SingleResult{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                "} " + super.toString();
    }

    public String toTextMessage() {
        return Str.join(DELIMITER, ProtocolConstants.RESPONSE, "" + messageId, clientId, source.toString(), key, value);

    }

    public String toBroadcastTextMessage() {
        return Str.join(DELIMITER, ProtocolConstants.BATCH_RESPONSE, "" + messageId, clientId, source.toString(), key, value);

    }
}
