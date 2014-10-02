package org.boon.slumberdb.stores.mysql;

import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.results.StatCount;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Logger;
import org.boon.collections.LongList;
import org.boon.core.Sys;
import org.boon.slumberdb.StringKeyValueStore;
import org.boon.slumberdb.mysql.BaseMySQLSupport;
import org.boon.slumberdb.mysql.SimpleStringKeyValueStoreMySQL;
import org.boon.slumberdb.noop.StringKeyValueStoreNoOp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.boon.Boon.configurableLogger;

/**
 * Created by Richard on 9/8/14.
 */
public class MySQLDataStoreLoader {

    private final boolean debug = GlobalConfig.DEBUG;

    private Logger logger = configurableLogger(this.getClass().getSimpleName());

    private String url;
    private String user;
    private String password;
    private String table;

    private StringKeyValueStore store;
    private LinkedTransferQueue<List<String>> loadQueue;
    private DataStoreConfig dataStoreConfig;
    private AtomicBoolean stop = new AtomicBoolean();
    private LinkedTransferQueue<Map<String, String>> loadedResultsFromDBQueue;

    private DataOutputQueue queueOut;


    public MySQLDataStoreLoader(DataStoreConfig dataStoreConfig, DataOutputQueue queueOut, AtomicBoolean stop,
                                LinkedTransferQueue<Map<String, String>> loadedResultsFromDBQueue,
                                LinkedTransferQueue<List<String>> loadQueue, String url, String user, String password, String table) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.table = table;
        this.dataStoreConfig = dataStoreConfig;
        this.stop = stop;
        this.loadedResultsFromDBQueue = loadedResultsFromDBQueue;
        this.loadQueue = loadQueue;
        this.queueOut = queueOut;

        try {
            store = new SimpleStringKeyValueStoreMySQL(url, user, password, table, dataStoreConfig.sqlBatchWrite());
        } catch (Exception ex) {
            store = StringKeyValueStoreNoOp.SINGLETON;
            if (debug) logger.warn(ex, "Unable to connect to MySQL, DS will not be sending data", ex.getMessage());
            logger.error("Unable to connect to MySQL, DS will not be sending data", ex.getMessage());

        }

    }

    public void run() throws InterruptedException {


        LongList inputBatches = new LongList();
        LongList outputBatches = new LongList();
        LongList loadedBatches = new LongList();
        LongList loadMiss = new LongList();
        long batchesCount = 0;

        List<String> batch = new ArrayList<>();
        Map<String, String> results;
        while (true) {

            List<String> poll = null;

            try {

                poll = loadQueue.poll(dataStoreConfig.pollTimeoutMS(), TimeUnit.MILLISECONDS);

            } catch (InterruptedException ie) {
                Thread.interrupted();
                if (stop.get()) {
                    return;
                }
            }

            while (poll != null) {

                inputBatches.add(poll.size());
                batch.addAll(poll);
                if (batch.size() > dataStoreConfig.dbMaxReadBatch()) {

                    break;
                }
                poll = loadQueue.poll();
                batchesCount++;
            }


            if (batch.size() > 0) {

                outputBatches.add(batch.size());
                try {
                    results = store.loadAllByKeys(batch);

                    loadedBatches.add(results.size());


                    int missCount = 0;
                    for (String key : batch) {
                        /* If it does not contain the key, then let the upstream know
                        they we were unable to load this value so we can respond back.
                         */
                        if (!results.containsKey(key)) {
                            missCount++;
                            results.put(key, "NULL");
                        }
                    }
                    loadMiss.add(missCount);
                    loadedResultsFromDBQueue.offer(results);


                    if (batchesCount % 100 == 0) {
                        processStats(batchesCount, inputBatches, outputBatches, loadedBatches, loadMiss);
                    }
                } finally {

                    batch.clear();

                }
            }

        }
    }

    private void processStats(
            long batchesCount,
            LongList inputBatches,
            LongList outputBatches,
            LongList loadedBatches,
            LongList loadMiss) {


        StatCount count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader batchesCount", batchesCount);
        queueOut.put(count);

        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader inputBatches max", inputBatches.max());
        queueOut.put(count);

        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader inputBatches.mean", inputBatches.mean());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader inputBatches.median", inputBatches.median());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader inputBatches.standardDeviation", inputBatches.standardDeviation());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader inputBatches.min", inputBatches.min());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader outputBatches max", outputBatches.max());
        queueOut.put(count);

        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader outputBatches.mean", outputBatches.mean());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader outputBatches.median", outputBatches.median());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader outputBatches.standardDeviation", outputBatches.standardDeviation());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader outputBatches.min", outputBatches.min());


        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader loadMiss max", loadMiss.max());
        queueOut.put(count);

        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader loadMiss.mean", loadMiss.mean());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader loadMiss.median", loadMiss.median());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader loadMiss.standardDeviation", loadMiss.standardDeviation());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader loadMiss.min", loadMiss.min());


        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader loadedBatches max", loadedBatches.max());
        queueOut.put(count);

        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader loadedBatches.mean", loadedBatches.mean());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader loadedBatches.median", loadedBatches.median());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader loadedBatches.standardDeviation", loadedBatches.standardDeviation());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "MySQLDataStoreLoader loadedBatches.min", loadedBatches.min());


        queueOut.put(count);


        BaseMySQLSupport support = (BaseMySQLSupport) store;
        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "DB Errors MySQLDataStoreLoader MySQL ", support.totalErrors());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "DB Connections Open MySQLDataStoreLoader MySQL ", support.totalConnectionOpen());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "DB Connections Closed MySQLDataStoreLoader MySQL ", support.totalClosedConnections());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.SERVER, Action.GET_STATS, "Thread TIME USER MySQLDataStoreLoader "
                +
                Thread.currentThread().getName(), Sys.threadUserTime());
        queueOut.put(count);


        count = new StatCount(DataStoreSource.SERVER, Action.GET_STATS, "Thread TIME CPU MySQLDataStoreLoader "
                + Thread.currentThread().getName(), Sys.threadCPUTime());
        queueOut.put(count);


        inputBatches.clear();
        outputBatches.clear();
        loadedBatches.clear();
        loadMiss.clear();

    }


    @Override
    public String toString() {
        return "MySQLDataStoreLoader{" +
                "url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password.length() + '\'' +
                ", table='" + table + '\'' +
                ", store=" + store +
                '}';
    }
}
