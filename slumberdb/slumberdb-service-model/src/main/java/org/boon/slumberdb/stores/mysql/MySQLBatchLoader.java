package org.boon.slumberdb.stores.mysql;

import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.requests.ReadBatchRequest;
import org.boon.slumberdb.service.results.BatchResult;
import org.boon.slumberdb.service.results.StatCount;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Logger;
import org.boon.core.Sys;
import org.boon.slumberdb.StringKeyValueStore;
import org.boon.slumberdb.mysql.BaseMySQLSupport;
import org.boon.slumberdb.mysql.SimpleStringKeyValueStoreMySQL;
import org.boon.slumberdb.noop.StringKeyValueStoreNoOp;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.boon.Boon.configurableLogger;

/**
 * Created by Richard on 9/8/14.
 */
public class MySQLBatchLoader {

    private final AtomicBoolean stop;
    private final boolean debug = GlobalConfig.DEBUG;
    private DataOutputQueue outputDataQueue;
    private Logger logger = configurableLogger(this.getClass().getSimpleName());


    private String url;
    private String user;
    private String password;
    private String table;
    private LinkedTransferQueue<ReadBatchRequest> batchReadOperations;


    private StringKeyValueStore store;


    public MySQLBatchLoader(DataStoreConfig dataStoreConfig, AtomicBoolean stop, DataOutputQueue outputDataQueue, LinkedTransferQueue<ReadBatchRequest> batchReadOperations, String url, String user, String password, String table) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.table = table;
        this.batchReadOperations = batchReadOperations;
        this.stop = stop;
        this.outputDataQueue = outputDataQueue;

        try {
            store = new SimpleStringKeyValueStoreMySQL(url, user, password, table, dataStoreConfig.sqlBatchWrite());
        } catch (Exception ex) {
            store = StringKeyValueStoreNoOp.SINGLETON;
            if (debug) logger.warn(ex, "Unable to connect to MySQL, DS will not be sending data", ex.getMessage());
            logger.error("Unable to connect to MySQL, DS will not be sending data", ex.getMessage());

        }


    }

    public void run() throws InterruptedException {

        long numberBatches = 0;
        long totalItemsLoadedViaBatch = 0;

        long totalItemsRequested = 0;

        while (true) {

            ReadBatchRequest batchReadOperation = null;

            try {

                batchReadOperation = batchReadOperations.poll(10, TimeUnit.SECONDS);

            } catch (InterruptedException ie) {
                Thread.interrupted();
                if (stop.get()) {
                    return;
                }
            }

            while (batchReadOperation != null) {


                final Collection<String> keys = batchReadOperation.keys();
                totalItemsRequested += keys.size();
                Map<String, String> results = store.loadAllByKeys(keys);

                numberBatches++;
                totalItemsLoadedViaBatch += results.size();

                if (numberBatches % 1000 == 0) {
                    long averageBatch = totalItemsLoadedViaBatch / numberBatches;


                    StatCount count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "BatchLoader MySQL averageBatchSize", averageBatch);
                    outputDataQueue.put(count);

                    count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "BatchLoader MySQL totalItemsLoaded", totalItemsLoadedViaBatch);
                    outputDataQueue.put(count);

                    count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "BatchLoader MySQL numberBatches", numberBatches);
                    outputDataQueue.put(count);


                    count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "BatchLoader MySQL totalItemsRequestedViaBatch", totalItemsRequested);
                    outputDataQueue.put(count);


                    BaseMySQLSupport support = (BaseMySQLSupport) store;
                    count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "DB Errors BatchLoader MySQL ", support.totalErrors());
                    outputDataQueue.put(count);


                    count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "DB Connections Open BatchLoader MySQL ", support.totalConnectionOpen());
                    outputDataQueue.put(count);


                    count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "DB Connections Closed BatchLoader MySQL ", support.totalClosedConnections());
                    outputDataQueue.put(count);


                    count = new StatCount(DataStoreSource.SERVER, Action.GET_STATS, "Thread TIME USER BatchLoader MySQL " +
                            Thread.currentThread().getName(), Sys.threadUserTime());
                    outputDataQueue.put(count);


                    count = new StatCount(DataStoreSource.SERVER, Action.GET_STATS, "Thread TIME CPU BatchLoader MySQL " +
                            Thread.currentThread().getName(), Sys.threadCPUTime());
                    outputDataQueue.put(count);


                }


                Map<String, String> map = new LinkedHashMap<>(keys.size());

                for (String key : keys) {
                    final String value = results.get(key);

                    map.put(key, value == null ? "NULL" : value);
                }


                BatchResult batchResult = new BatchResult(batchReadOperation.messageId(), batchReadOperation.clientId(),
                        DataStoreSource.REMOTE_DB, map);


                outputDataQueue.put(batchResult);


                batchReadOperation = batchReadOperations.poll();


            }

        }
    }


    @Override
    public String toString() {
        return "MySQLBatchLoader{" +
                "url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password.length() + '\'' +
                ", table='" + table + '\'' +
                ", store=" + store +
                '}';
    }
}
