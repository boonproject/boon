package org.boon.slumberdb.stores;

import org.boon.Lists;
import org.boon.Logger;
import org.boon.Pair;
import org.boon.core.Sys;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.boon.slumberdb.service.config.ReplicationDataStoreConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.ErrorResult;
import org.boon.slumberdb.service.results.StatCount;
import org.boon.slumberdb.service.server.ServiceMethod;
import org.boon.slumberdb.stores.log.AsyncFileWriterDataStore;
import org.boon.slumberdb.stores.mysql.MySQLDataStore;
import org.boon.slumberdb.stores.replicate.ReplicationDataStore;

import java.util.ArrayList;
import java.util.List;

import static org.boon.Boon.configurableLogger;
import static org.boon.Boon.puts;


/**
 * Remaining tasks:
 * Created by Richard on 6/27/14.
 */
public class MasterDataStore implements DataStore {

    private Logger logger = configurableLogger(MasterDataStore.class);

    private DataOutputQueue transferQueue;
    private LevelDBDataStore levelDBDataStore;
    private MySQLDataStore mySQLDataStore;
    private ConcurrentMapDataStore mapDataStore = new ConcurrentMapDataStore();
    private EndOfTheLineMapStore endOfTheLineMapStore;
    private AsyncFileWriterDataStore logStore = new AsyncFileWriterDataStore();
    private ReplicationDataStore replicationDataStore = new ReplicationDataStore();
    private List<DataStore> dataStoreList;
    private DataStoreConfig dataStoreConfig;
    private boolean mySQLReadOnly;

    public void init(DataOutputQueue dataOutputQueue, StartupMode mode) {
        init(dataOutputQueue, mode, null, null);
    }

    public void init(DataOutputQueue dataOutputQueue) {
        init(dataOutputQueue, StartupMode.LEVELDB_AND_MYSQL, null, null);
    }

    public void init(DataOutputQueue dataOutputQueue, DataStoreServerConfig inDataStoreServerConfig) {
        init(dataOutputQueue, inDataStoreServerConfig.startupMode(), null, inDataStoreServerConfig.replicationDataStoreConfig());
    }

    public void init(DataOutputQueue dataOutputQueue, StartupMode mode, DataStoreConfig inDataStoreConfig, ReplicationDataStoreConfig inReplicationDataStoreConfig) {
        this.dataStoreConfig = inDataStoreConfig == null ? DataStoreConfig.load() : inDataStoreConfig;
        puts("Master - Data Store Config", this.dataStoreConfig);

        logStore.init(this.dataStoreConfig);

        if (mode == StartupMode.LEVELDB_AND_MYSQL) {
            transferQueue = dataOutputQueue;
            endOfTheLineMapStore = new EndOfTheLineMapStore(transferQueue);
            mySQLDataStore = new MySQLDataStore();
            levelDBDataStore = new LevelDBDataStore();

            dataStoreList = Lists.list(logStore, mapDataStore, levelDBDataStore, mySQLDataStore);
            mapDataStore.init(this.dataStoreConfig, transferQueue, levelDBDataStore);
            levelDBDataStore.init(this.dataStoreConfig, transferQueue, mySQLDataStore);

            try {
                mySQLDataStore.init(this.dataStoreConfig, transferQueue, endOfTheLineMapStore);
            } catch (Exception ex) {
                logger.error(ex, "Unable to connect to MySQL, proceeding without MySQL, no data will be forwarded to MySQL");
            }
        }

        else if (mode == StartupMode.LEVELDB) {

            transferQueue = dataOutputQueue;
            endOfTheLineMapStore = new EndOfTheLineMapStore(transferQueue);
            // NO mySQLDataStore
            levelDBDataStore = new LevelDBDataStore();

            dataStoreList = Lists.list(mapDataStore, logStore, levelDBDataStore);
            mapDataStore.init(this.dataStoreConfig, transferQueue, levelDBDataStore);
            levelDBDataStore.init(this.dataStoreConfig, transferQueue, endOfTheLineMapStore);
        }

        else if (mode == StartupMode.MYSQL) {

            transferQueue = dataOutputQueue;
            endOfTheLineMapStore = new EndOfTheLineMapStore(transferQueue);
            mySQLDataStore = new MySQLDataStore();
            // NO levelDBDataStore

            dataStoreList = Lists.list(logStore, mySQLDataStore, mapDataStore);
            mapDataStore.init(this.dataStoreConfig, transferQueue, mySQLDataStore);
            mySQLDataStore.init(this.dataStoreConfig, transferQueue, endOfTheLineMapStore);
        }

        else if (mode == StartupMode.MYSQL_READONLY) {

            transferQueue = dataOutputQueue;
            endOfTheLineMapStore = new EndOfTheLineMapStore(transferQueue);
            mySQLDataStore = new MySQLDataStore();

            dataStoreList = Lists.list(logStore, mySQLDataStore, mapDataStore);

            mapDataStore.init(this.dataStoreConfig, transferQueue, mySQLDataStore);
            mySQLDataStore.init(this.dataStoreConfig, transferQueue, endOfTheLineMapStore);
            mySQLReadOnly = true;
        }

        else if (mode == StartupMode.NO_BACKING_DB) {

            transferQueue = dataOutputQueue;
            endOfTheLineMapStore = new EndOfTheLineMapStore(transferQueue);
            // NO mySQLDataStore
            // NO levelDBDataStore

            dataStoreList = Lists.list(mapDataStore, logStore);
            mapDataStore.init(this.dataStoreConfig, transferQueue, endOfTheLineMapStore);
        }

        if (inReplicationDataStoreConfig != null) {
            replicationDataStore = new ReplicationDataStore();
            replicationDataStore.init(inReplicationDataStoreConfig);
            dataStoreList.add(replicationDataStore);
        }
    }


    @ServiceMethod
    public void setSource(SetRequest request) {
        _set(request.source(), request);
    }

    /*
        THIS IS THE DELEGATE FOR SETTING DATA. For instance
        set(SetRequest request) and setSource(SetRequest request)
        will both call this method instead of processing the set themself,
        therefore providing a common and consistent way to handle the set - SFF
    */
    protected void _set(DataStoreSource source, SetRequest request) {
        switch (source) {
            case MEMORY:
                _setMemory(request);
                break;
            case LOCAL_DB:
                _setLocal(request, true);
                break;
            case REMOTE_DB:
                _setRemote(request, true);
                break;
            case TRANSACTION_LOG:
                _setTransactionLog(request);
                break;
            case LOCAL_STORES:
                _setMemory(request);
                _setLocal(request, true);
                break;
            case ALL:
                _setTransactionLog(request);
                _setReplication(request);
                _setMemory(request);
                _setLocal(request, false);
                _setRemote(request, false);
                break;
            case REPLICATION:
                _setMemory(request);
                _setLocal(request, false);
                _setRemote(request, false);
                break;
            default:
                queueInvalidSource(request);
                logger.error("Master Data Store:: Unable to handle Set Source", request);
        }
    }

    private void _setMemory(SetRequest request) {
        mapDataStore.set(request);
    }

    private void _setLocal(SetRequest request, boolean required) {
        if (levelDBDataStore != null) {
            levelDBDataStore.set(request);
        }
        else if (required) {
            queueInvalidSource(request);
        }
    }

    private void _setRemote(SetRequest request, boolean required) {
        if (mySQLDataStore != null && !mySQLReadOnly) {
            mySQLDataStore.set(request);
        }
        else if (required) {
            queueInvalidSource(request);
        }
    }

    private void _setTransactionLog(SetRequest request) {
        logStore.set(request);
    }

    private void _setReplication(SetRequest request) {
        if (replicationDataStore != null) {
            replicationDataStore.set(request);
        }
    }

    @ServiceMethod
    public void getSource(GetRequest request) {
        switch (request.source()) {
            case MEMORY:
                mapDataStore.get(request);
                break;
            case LOCAL_DB:
                if (levelDBDataStore != null) {
                    levelDBDataStore.get(request);
                }
                else {
                    queueInvalidSource(request);
                }
                break;
            case REMOTE_DB:
                if (mySQLDataStore != null) {
                    mySQLDataStore.get(request);
                }
                else {
                    queueInvalidSource(request);
                }
                break;
            case TRANSACTION_LOG:
                queueInvalidSource(request);
                logger.error("Master Data Store:: Improper Get Source", request);
                break;
            default:
                queueInvalidSource(request);
                logger.error("Master Data Store:: Unable to handle Get Source", request);
        }
    }

    @ServiceMethod
    public void removeFromSource(RemoveRequest request) {
        switch (request.source()) {
            case MEMORY:
                mapDataStore.remove(request);
                break;
            case LOCAL_DB:
                if (levelDBDataStore != null) {
                    levelDBDataStore.remove(request);
                }
                else {
                    queueInvalidSource(request);
                }
                break;
            case REMOTE_DB:
                if (mySQLDataStore != null) {
                    mySQLDataStore.remove(request);
                }
                else {
                    queueInvalidSource(request);
                }
                break;
            case TRANSACTION_LOG:
                logStore.remove(request);
                break;
            default:
                logger.error("Master Data Store:: Unable to handle REMOVE Source", request);

        }
    }

    @ServiceMethod
    @Override
    public void clearStats() {
        logStore.clearStats();
        if (replicationDataStore != null) {
            replicationDataStore.clearStats();
        }
        mapDataStore.clearStats();
        if (mySQLDataStore != null) {
            mySQLDataStore.clearStats();
        }
        if (levelDBDataStore != null) {
            levelDBDataStore.clearStats();
        }
    }


    @ServiceMethod
    @Override
    public void sendStats(long now) {
        logStore.sendStats(now);
        if (replicationDataStore != null) {
            replicationDataStore.sendStats(now);
        }
        mapDataStore.sendStats(now);
        if (mySQLDataStore != null) {
            mySQLDataStore.sendStats(now);
        }
        if (levelDBDataStore != null) {
            levelDBDataStore.sendStats(now);
        }

        StatCount statCount;

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbReaderCount",
                dataStoreConfig.dbReaderCount());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbWriterCount",
                dataStoreConfig.dbWriterCount());

        transferQueue.put(statCount);

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG levelDBBatchReaderCount",
                dataStoreConfig.levelDBBatchReaderCount());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG checkStatusEveryIntervalMS",
                dataStoreConfig.checkStatusEveryIntervalMS());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbMaxReadBatch",
                dataStoreConfig.dbMaxReadBatch());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbMaxWriteBatch",
                dataStoreConfig.dbMaxWriteBatch());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbMinReadBatch",
                dataStoreConfig.dbMinReadBatch());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbReadFlushQueueIntervalMS",
                dataStoreConfig.dbReadFlushQueueIntervalMS());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG levelDBCacheSize",
                dataStoreConfig.levelDBCacheSize());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG levelDBMaxOpenFiles",
                dataStoreConfig.levelDBMaxOpenFiles());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG levelDBWriteBufferSize",
                dataStoreConfig.levelDBWriteBufferSize());

        transferQueue.put(statCount);

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG sqlBatchWrite",
                dataStoreConfig.sqlBatchWrite());

        transferQueue.put(statCount);

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG threadErrorResumeTimeMS",
                dataStoreConfig.threadErrorResumeTimeMS());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG pollTimeoutMS",
                dataStoreConfig.pollTimeoutMS());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG blacklist" + dataStoreConfig.mySQLKeyBlackList(),
                dataStoreConfig.mySQLKeyBlackList().size());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG DB URL " + dataStoreConfig.dbUrl(),
                dataStoreConfig.dbUrl().length());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG levelDBFileName " + dataStoreConfig.levelDBFileName(),
                dataStoreConfig.levelDBFileName().length());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG outputDirectory " + dataStoreConfig.outputDirectory(),
                dataStoreConfig.outputDirectory().length());

        transferQueue.put(statCount);
    }


    @ServiceMethod
    @Override
    public void set(SetRequest request) {
        _set(request.source(), request);
    }

    @ServiceMethod
    @Override
    public void get(GetRequest getRequest) {
        mapDataStore.get(getRequest);
    }

    @ServiceMethod
    @Override
    public void search(SearchRequest searchRequest) {
        if (levelDBDataStore != null) {
            levelDBDataStore.search(searchRequest);
        }
    }

    @ServiceMethod
    public String get(String key) {
        return mapDataStore.get(key);
    }

    @ServiceMethod
    @Override
    public void batchRead(ReadBatchRequest request) {
        mapDataStore.batchRead(request);
    }

    @ServiceMethod
    @Override
    public void remove(RemoveRequest removeRequest) {
        logStore.remove(removeRequest);
        if (replicationDataStore != null) {
            replicationDataStore.remove(removeRequest);
        }
        mapDataStore.remove(removeRequest);
        if (mySQLDataStore != null && !mySQLReadOnly) {
            mySQLDataStore.remove(removeRequest);
        }
        if (levelDBDataStore != null) {
            levelDBDataStore.remove(removeRequest);
        }
    }


    @ServiceMethod
    @Override
    public void addAll(BatchSetRequest batchSetRequest) {
        if (DataStoreSource.REPLICATION != batchSetRequest.source()) {
            logStore.addAll(batchSetRequest);
            if (replicationDataStore != null) {
                replicationDataStore.addAll(batchSetRequest);
            }
        }
        mapDataStore.addAll(batchSetRequest);
        if (mySQLDataStore != null && !mySQLReadOnly) {
            mySQLDataStore.addAll(batchSetRequest);
        }
        if (levelDBDataStore != null) {
            levelDBDataStore.addAll(batchSetRequest);
    }
    }


    @Override
    public void start() {
        for (DataStore dataStore : dataStoreList) {
            dataStore.start();
        }
        Sys.sleep(10);
    }

    @Override
    public void stop() {
        if (dataStoreList != null) {
            for (DataStore dataStore : dataStoreList) {
                dataStore.stop();
            }
        }
    }

    @ServiceMethod
    public void flush() {
        logStore.flush();
        if (replicationDataStore != null) {
            replicationDataStore.flush();
        }
    }


    @ServiceMethod
    public long countLocalDB() {
        return levelDBDataStore == null ? -1 : levelDBDataStore.count();
    }

    @ServiceMethod
    public long countKeyPrefix(String prefix) {
        return levelDBDataStore == null ? -1 : levelDBDataStore.countKeyPrefix(prefix);
    }

    @ServiceMethod
    public long deleteLocalDBByKeyPrefix(String prefix) {
        return levelDBDataStore == null ? -1 : levelDBDataStore.deleteKeysByPrefix(prefix);
    }

    @ServiceMethod
    public List<Pair<String, String>> search(String prefix) {
        return levelDBDataStore.search(prefix);
    }

    @ServiceMethod
    public List<String> searchForKeys(String prefix) {
        return levelDBDataStore == null ? new ArrayList<String>() : levelDBDataStore.searchForKeys(prefix);
    }


    @ServiceMethod
    public long deleteByKeyPrefix(String prefix) {
        List<String> keys = searchForKeys(prefix);
        for (String key : keys) {
            mapDataStore.remove(key);
        }
        if (mySQLDataStore != null) {
            for (String key : keys) {
                mySQLDataStore.remove(new RemoveRequest(666, "admin", key));
            }
        }

        return keys.size();
    }

    @ServiceMethod
    public long countMemory() {
        return mapDataStore.count();
    }

    @ServiceMethod
    public boolean exists(String key) {
        return mapDataStore.exists(key);
    }

    @ServiceMethod
    public DataStoreConfig config() {
        return dataStoreConfig;
    }

    @ServiceMethod
    public void set(String key, String value) {
        mapDataStore.set(key, value);
    }

    private void queueInvalidSource(BaseDataStoreRequest request) {
        transferQueue.put(invalidSourceResult(request));
    }

    private ErrorResult invalidSourceResult(BaseDataStoreRequest request) {
        return new ErrorResult(request.messageId(), request.clientId(), request.source(), "Invalid Source");
    }
}
