package org.boon.slumberdb.stores;

import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.StatCount;
import org.boon.slumberdb.service.server.ServiceMethod;
import org.boon.slumberdb.stores.log.AsyncFileWriterDataStore;
import org.boon.slumberdb.stores.mysql.MySQLDataStore;
import org.boon.Lists;
import org.boon.Logger;
import org.boon.Pair;
import org.boon.core.Sys;

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
    private List<DataStore> dataStoreList;
    private DataStoreConfig config;
    private boolean mySQLReadOnly;


    public void init(DataOutputQueue dataOutputQueue, StartupMode mode) {


        final DataStoreConfig config = DataStoreConfig.load();

        puts("Master Data Store Config", config);

        init(config, dataOutputQueue, mode);

    }

    public void init(DataOutputQueue dataOutputQueue) {


        final DataStoreConfig config = DataStoreConfig.load();

        init(config, dataOutputQueue, StartupMode.LEVELDB_AND_MYSQL);
    }

    public void init(DataStoreConfig config, DataOutputQueue dataOutputQueue, StartupMode mode) {


        this.config = config;
        logStore.init(config);

        if (mode == StartupMode.LEVELDB_AND_MYSQL) {


            transferQueue = dataOutputQueue;
            endOfTheLineMapStore = new EndOfTheLineMapStore(transferQueue);
            mySQLDataStore = new MySQLDataStore();
            levelDBDataStore = new LevelDBDataStore();

            dataStoreList = Lists.list(logStore, mapDataStore, levelDBDataStore, mySQLDataStore);

            mapDataStore.init(config, transferQueue, levelDBDataStore);
            levelDBDataStore.init(config, transferQueue, mySQLDataStore);

            try {
                mySQLDataStore.init(config, transferQueue, endOfTheLineMapStore);
            } catch (Exception ex) {
                logger.error(ex, "Unable to connect to MySQL, proceeding without MySQL, no data will be forwarded to MySQL");
            }

        } else if (mode == StartupMode.LEVELDB) {

            transferQueue = dataOutputQueue;

            endOfTheLineMapStore = new EndOfTheLineMapStore(transferQueue);

            levelDBDataStore = new LevelDBDataStore();

            dataStoreList = Lists.list(mapDataStore, logStore, levelDBDataStore);

            mapDataStore.init(config, transferQueue, levelDBDataStore);
            levelDBDataStore.init(config, transferQueue, endOfTheLineMapStore);


        } else if (mode == StartupMode.MYSQL) {

            transferQueue = dataOutputQueue;

            endOfTheLineMapStore = new EndOfTheLineMapStore(transferQueue);

            mySQLDataStore = new MySQLDataStore();

            dataStoreList = Lists.list(logStore, mySQLDataStore, mapDataStore);

            mapDataStore.init(config, transferQueue, mySQLDataStore);
            mySQLDataStore.init(config, transferQueue, endOfTheLineMapStore);


        } else if (mode == StartupMode.MYSQL_READONLY) {

            transferQueue = dataOutputQueue;

            endOfTheLineMapStore = new EndOfTheLineMapStore(transferQueue);

            mySQLDataStore = new MySQLDataStore();

            dataStoreList = Lists.list(logStore, mySQLDataStore, mapDataStore);

            mapDataStore.init(config, transferQueue, mySQLDataStore);
            mySQLDataStore.init(config, transferQueue, endOfTheLineMapStore);
            mySQLReadOnly = true;


        } else if (mode == StartupMode.NO_BACKING_DB) {


            transferQueue = dataOutputQueue;

            endOfTheLineMapStore = new EndOfTheLineMapStore(transferQueue);

            dataStoreList = Lists.list(mapDataStore, logStore);

            mapDataStore.init(config, transferQueue, endOfTheLineMapStore);

        }


    }


    @ServiceMethod
    public void setSource(SetRequest request) {


        switch (request.source()) {
            case MEMORY:
                mapDataStore.set(request);
                break;
            case LOCAL_DB:
                levelDBDataStore.set(request);
                break;
            case REMOTE_DB:
                mySQLDataStore.set(request);
                break;
            case TRANSACTION_LOG:
                logStore.set(request);
                break;
            case LOCAL_STORES:
                mapDataStore.set(request);
                levelDBDataStore.set(request);
                break;
            default:
                logger.error("Master Data Store:: Unable to handle Set Source", request);
        }
    }


    @ServiceMethod
    public void getSource(GetRequest request) {


        switch (request.source()) {
            case MEMORY:
                mapDataStore.get(request);
                break;
            case LOCAL_DB:
                levelDBDataStore.get(request);
                break;
            case REMOTE_DB:
                mySQLDataStore.get(request);
                break;
            case TRANSACTION_LOG:
                logStore.get(request);
                break;
            default:
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
                levelDBDataStore.remove(request);
                break;
            case REMOTE_DB:
                mySQLDataStore.remove(request);
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
        mapDataStore.clearStats();
        mySQLDataStore.clearStats();
        levelDBDataStore.clearStats();
    }


    @ServiceMethod
    @Override
    public void sendStats(long now) {


        logStore.sendStats(now);
        mapDataStore.sendStats(now);
        mySQLDataStore.sendStats(now);
        levelDBDataStore.sendStats(now);

        StatCount statCount;

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbReaderCount",
                config.dbReaderCount());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbWriterCount",
                config.dbWriterCount());

        transferQueue.put(statCount);

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG levelDBBatchReaderCount",
                config.levelDBBatchReaderCount());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG checkStatusEveryIntervalMS",
                config.checkStatusEveryIntervalMS());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbMaxReadBatch",
                config.dbMaxReadBatch());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbMaxWriteBatch",
                config.dbMaxWriteBatch());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbMinReadBatch",
                config.dbMinReadBatch());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG dbReadFlushQueueIntervalMS",
                config.dbReadFlushQueueIntervalMS());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG levelDBCacheSize",
                config.levelDBCacheSize());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG levelDBMaxOpenFiles",
                config.levelDBMaxOpenFiles());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG levelDBWriteBufferSize",
                config.levelDBWriteBufferSize());

        transferQueue.put(statCount);

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG sqlBatchWrite",
                config.sqlBatchWrite());

        transferQueue.put(statCount);

        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG threadErrorResumeTimeMS",
                config.threadErrorResumeTimeMS());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG pollTimeoutMS",
                config.pollTimeoutMS());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG blacklist" + config.mySQLKeyBlackList(),
                config.mySQLKeyBlackList().size());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG DB URL " + config.dbUrl(),
                config.dbUrl().length());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG levelDBFileName " + config.levelDBFileName(),
                config.levelDBFileName().length());

        transferQueue.put(statCount);


        statCount = new StatCount(now, DataStoreSource.SERVER,
                Action.NONE,
                "CONFIG outputDirectory " + config.outputDirectory(),
                config.outputDirectory().length());

        transferQueue.put(statCount);
    }


    @ServiceMethod
    @Override
    public void set(SetRequest request) {


        logStore.set(request);
        mapDataStore.set(request);
        if (!mySQLReadOnly) {
            mySQLDataStore.set(request);
        }
        if (levelDBDataStore != null) {
            levelDBDataStore.set(request);
        }

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
        mapDataStore.remove(removeRequest);
        if (levelDBDataStore != null) {
            levelDBDataStore.remove(removeRequest);
        }
        if (!mySQLReadOnly) {
            mySQLDataStore.remove(removeRequest);
        }

    }


    @ServiceMethod
    @Override
    public void addAll(BatchSetRequest batchSetRequest) {
        logStore.addAll(batchSetRequest);
        mapDataStore.addAll(batchSetRequest);
        if (levelDBDataStore != null) {
            levelDBDataStore.addAll(batchSetRequest);
        }
        if (!mySQLReadOnly) {
            mySQLDataStore.addAll(batchSetRequest);
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
    }


    @ServiceMethod
    public long countLocalDB() {
        if (levelDBDataStore != null) {
            return this.levelDBDataStore.count();
        } else {
            return -1L;
        }
    }

    @ServiceMethod
    public long countKeyPrefix(String prefix) {
        if (levelDBDataStore != null) {
            return this.levelDBDataStore.countKeyPrefix(prefix);
        } else {
            return -1L;
        }
    }

    @ServiceMethod
    public long deleteLocalDBByKeyPrefix(String prefix) {
        if (levelDBDataStore != null) {
            return this.levelDBDataStore.deleteKeysByPrefix(prefix);
        } else {
            return -1L;
        }
    }

    @ServiceMethod
    public List<Pair<String, String>> search(String prefix) {

        return this.levelDBDataStore.search(prefix);

    }

    @ServiceMethod
    public List<String> searchForKeys(String prefix) {

        return this.levelDBDataStore.searchForKeys(prefix);

    }


    @ServiceMethod
    public long deleteByKeyPrefix(String prefix) {

        List<String> keys = this.levelDBDataStore.searchForKeys(prefix);

        for (String key : keys) {
            mapDataStore.remove(key);
        }


        for (String key : keys) {
            mySQLDataStore.remove(new RemoveRequest(666, "admin", key));
        }

        return keys.size();

    }

    @ServiceMethod
    public long countMemory() {
        if (mapDataStore != null) {
            return this.mapDataStore.count();
        } else {
            return -1L;
        }
    }


    @ServiceMethod
    public boolean exists(String key) {
        return this.mapDataStore.exists(key);
    }

    @ServiceMethod
    public DataStoreConfig config() {
        return config;
    }

    @ServiceMethod
    public void set(String key, String value) {
        mapDataStore.set(key, value);
    }

}
