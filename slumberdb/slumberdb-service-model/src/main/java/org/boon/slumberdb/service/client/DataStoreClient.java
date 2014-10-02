package org.boon.slumberdb.service.client;


import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStoreSource;

import java.util.Collection;
import java.util.Map;

public interface DataStoreClient {


    public void flush();

    public boolean connected(String key);

    public void get(String key);

    public void batchLoad(Collection<String> keys);

    public void set(String key, Object value);


    public void broadcastSet(String key, Object value);


    public void setBatch(Map<String, Object> batch);


    public void setBatchIfNotExists(Map<String, Object> batch);

    public DataOutputQueue queue();

    public DataStoreAdminClient admin(String shardId, String password);

    void setIfNotExists(String add, Object value);

    void getFromMemory(String key);

    void getFromLocalDB(String key);


    /* This is mostly for testing. */
    public void set(DataStoreSource source, String key, Object value);

    public void get(DataStoreSource source, String key);

    public void remove(DataStoreSource source, String key);

    public void remove(String key);

    public void getStats();

    public void clearStats();


    public void getStats(DataStoreSource source);

    public void clearStats(DataStoreSource source);

}
