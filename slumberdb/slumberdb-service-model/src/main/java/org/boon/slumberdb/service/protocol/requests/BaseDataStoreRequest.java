package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.stores.DataStoreSource;

/**
 * Created by Richard on 9/8/14.
 */
public abstract class BaseDataStoreRequest extends DataStoreRequest {

    public BaseDataStoreRequest(long id, Action action) {
        super(id, action);
    }

    public BaseDataStoreRequest() {
    }

    @Override
    public String clientId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String key() {

        throw new UnsupportedOperationException();
    }

    @Override
    void key(String key) {

        throw new UnsupportedOperationException();
    }

    @Override
    public String payload() {

        throw new UnsupportedOperationException();
    }

    @Override
    void clientId(String clientId) {

        throw new UnsupportedOperationException();
    }

    @Override
    void objectVersion(long objectVersion) {

        throw new UnsupportedOperationException();
    }

    @Override
    void updateTimeStamp(long ts) {

        throw new UnsupportedOperationException();
    }

    @Override
    void createTimeStamp(long ts) {

        throw new UnsupportedOperationException();
    }

    @Override
    protected void setSource(DataStoreSource dataStoreSource) {

        throw new UnsupportedOperationException();
    }
}
