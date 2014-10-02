package org.boon.slumberdb.service.protocol.requests;


import org.boon.slumberdb.service.protocol.Action;

import java.util.Map;

/**
 * Created by Richard on 9/4/14.
 */
public class QueryRequest extends BaseDataStoreRequest {

    //TODO Action.Query

    public static DataStoreRequest parse(String message) {
        return null;//TODO
    }

    public static DataStoreRequest parse(Action action, Map<String, String> message) {
        return null;//TODO
    }

    @Override
    public String formTextRequest() {
        return null;
    }
}
