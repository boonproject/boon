package org.boon.slumberdb.stores;

import org.boon.slumberdb.service.protocol.requests.*;

/**
 * Created by Richard on 6/27/14.
 */
public interface DataStore {

    void clearStats();

    void sendStats(long now);

    /**
     * Calls to set are non blocking calls.
     */
    //void set(long messageId, String clientId, String key, String value);
    void set(SetRequest setRequest);


    /**
     * Calls to get are non blocking calls.
     * The value will be put on an output outputDataQueue for the clientKey.
     */
    void get(GetRequest getRequest);

    /**
     * Calls to get are non blocking calls.
     * The value will be put on an output outputDataQueue for the clientKey.
     */
    void search(SearchRequest searchRequest);

    void batchRead(ReadBatchRequest request);


    void remove(RemoveRequest removeRequest);

    void addAll(BatchSetRequest batchSetRequest);

    void start();

    void stop();


}
