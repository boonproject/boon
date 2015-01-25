package org.boon.slumberdb.service.results;

import org.boon.slumberdb.stores.DataStoreSource;

/**
 * Created by Richard on 7/11/14.
 */
public class BroadcastResult extends SingleResult {
    public BroadcastResult(long messageId, String clientId, DataStoreSource source, String key, String value) {
        super(messageId, clientId, source, key, value);
    }

    public BroadcastResult() {
    }


    @Override
    public String toString() {
        return "BroadcastResult{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                "} " + super.toString();
    }

}
