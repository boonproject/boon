package org.boon.slumberdb.service.config;

import org.boon.core.Sys;

/**
 * Created by Scott in January 2015
 */
public class ReplicationDataStoreConfig {
    private DataStoreClientConfig dataStoreClientConfig;
    private int maxBatchSize = 0;
    private long maxWaitMillis = 1000;

    public static ReplicationDataStoreConfig load() {
        String fileLocation = Sys.sysProp("ReplicationDataStoreConfig", "/opt/org/slumberdb/replicationdatastore.json");
        return Sys.loadFromFileLocation(ReplicationDataStoreConfig.class, fileLocation);
    }

    public DataStoreClientConfig dataStoreClient() {
        return dataStoreClientConfig;
    }

    public int maxBatchSize() {
        return maxBatchSize;
    }

    public long maxWaitMillis() {
        return maxWaitMillis;
    }
}
