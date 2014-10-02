package org.boon.slumberdb.service.protocol.requests;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.stores.DataStoreSource;

/**
 * Created by Richard on 9/3/14.
 */
public class PreambleOfRequest extends DataStoreRequest {


    public static Action action(String message) {
        PreambleOfRequest message1 = new PreambleOfRequest();
        parsePreamble(message1, message);
        return message1.action();

    }

    @Override
    public String clientId() {
        return null;
    }

    @Override
    public String key() {
        return null;
    }

    @Override
    void key(String key) {

    }

    @Override
    public String payload() {
        return null;
    }

    @Override
    public String formTextRequest() {
        return null;
    }

    @Override
    void clientId(String clientId) {

    }

    @Override
    void objectVersion(long objectVersion) {

    }

    @Override
    void updateTimeStamp(long ts) {

    }

    @Override
    void createTimeStamp(long ts) {

    }

    @Override
    protected void setSource(DataStoreSource dataStoreSource) {
    }


}
