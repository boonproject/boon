package org.boon.slumberdb.service.results;

import org.boon.slumberdb.stores.DataStoreSource;

/**
 * Created by Richard on 7/8/14.
 */
public abstract class Result extends Response {
    protected String clientId;

    protected long messageId;

    public Result(long messageId, String clientId, DataStoreSource source) {
        this.clientId = clientId;
        this.source = source;
        this.messageId = messageId;
    }

    public Result() {

    }

    public String clientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    public long messageId() {
        return messageId;
    }


    public abstract String toTextMessage();

    @Override
    public String toString() {
        return "Result{" +
                "clientId='" + clientId + '\'' +
                ", source=" + source +
                '}';
    }
}
