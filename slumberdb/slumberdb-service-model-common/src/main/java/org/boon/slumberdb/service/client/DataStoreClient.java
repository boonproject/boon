package org.boon.slumberdb.service.client;

import org.boon.slumberdb.service.protocol.requests.BatchSetRequest;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStoreSource;

import java.util.Collection;
import java.util.Map;

public interface DataStoreClient {


    void flush();

    boolean connected(String key);

    void get(String key);

    void batchLoad(Collection<String> keys);

    void set(String key, Object value);


    void broadcastSet(String key, Object value);


    void setBatch(Map<String, Object> batch);

    void setBatchIfNotExists(Map<String, Object> batch);

    DataOutputQueue queue();

    DataStoreAdminClient admin(String shardId, String password);

    void setIfNotExists(String add, Object value);

    void getFromMemory(String key);

    void getFromLocalDB(String key);


    /* This is mostly for testing. */
    void set(DataStoreSource source, String key, Object value);

    void get(DataStoreSource source, String key);

    void setBatch(DataStoreSource source, Map<String, Object> batch);

    void setBatchIfNotExists(DataStoreSource source, Map<String, Object> batch);

    void setBatch(BatchSetRequest request);

    void remove(DataStoreSource source, String key);

    void remove(String key);

    void getStats();

    void clearStats();


    void getStats(DataStoreSource source);

    void clearStats(DataStoreSource source);

}
