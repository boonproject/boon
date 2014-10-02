package org.boon.slumberdb.stores.mysql;

import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.results.StatCount;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Logger;
import org.boon.core.Sys;
import org.boon.slumberdb.StringKeyValueStore;
import org.boon.slumberdb.mysql.BaseMySQLSupport;
import org.boon.slumberdb.mysql.SimpleStringKeyValueStoreMySQL;
import org.boon.slumberdb.noop.StringKeyValueStoreNoOp;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.boon.Boon.configurableLogger;

/**
 * Created by Richard on 9/8/14.
 */
public class MySQLDataStorePutManager {

    private final boolean debug = GlobalConfig.DEBUG;
    private Logger logger = configurableLogger(this.getClass().getSimpleName());

    private String url;
    private String user;
    private String password;
    private String table;

    private StringKeyValueStore store;
    private LinkedTransferQueue<Object> storeQueue;
    private AtomicBoolean stop;
    private DataStoreConfig dataStoreConfig;
    private DataOutputQueue outputQueue;


    public MySQLDataStorePutManager(DataOutputQueue outputQueue,
                                    DataStoreConfig dataStoreConfig, AtomicBoolean stop, LinkedTransferQueue<Object> storeQueue,
                                    String url, String user, String password, String table) {
        this.outputQueue = outputQueue;
        this.url = url;
        this.user = user;
        this.password = password;
        this.table = table;
        this.storeQueue = storeQueue;
        this.stop = stop;
        this.dataStoreConfig = dataStoreConfig;

        try {
            store = new SimpleStringKeyValueStoreMySQL(url, user, password, table, dataStoreConfig.sqlBatchWrite());
        } catch (Exception ex) {
            store = StringKeyValueStoreNoOp.SINGLETON;
            if (debug) logger.warn(ex, "Unable to connect to MySQL, DS will not be sending data", ex.getMessage());
            logger.error("Unable to connect to MySQL, DS will not be sending data", ex.getMessage());

        }
    }

    public void run() {


        long numberOfWrites = 0;
        long numberOfRemoves = 0;

        long numOperations = 1;

        Map<String, String> mapToWrite;
        List<String> listToDelete;


        Object object = null;


        while (true) {

            try {
                object = storeQueue.poll(dataStoreConfig.pollTimeoutMS(), TimeUnit.MILLISECONDS);


            } catch (InterruptedException iex) {
                if (stop.get()) {
                    return;
                } else {
                    Thread.interrupted();
                }
            }

            while (object != null) {
                numOperations++;

                if (object instanceof Map) {
                    mapToWrite = (Map<String, String>) object;
                    numberOfWrites += mapToWrite.size();
                    store.putAll(mapToWrite);
                } else if (object instanceof List) {
                    listToDelete = (List<String>) object;
                    numberOfRemoves += listToDelete.size();
                    store.removeAll(listToDelete);
                }

                object = storeQueue.poll();

            }


            if (numOperations % 100 == 0) {

                StatCount count = new StatCount(DataStoreSource.REMOTE_DB, Action.SET, "MySQLDataStorePutManager MySQL numberOfWrites", numberOfWrites);
                outputQueue.put(count);

                count = new StatCount(DataStoreSource.REMOTE_DB, Action.SET, "MySQLDataStorePutManager MySQL numberOfRemoves", numberOfRemoves);
                outputQueue.put(count);


                BaseMySQLSupport support = (BaseMySQLSupport) store;
                count = new StatCount(DataStoreSource.REMOTE_DB, Action.SET, "DB Errors MySQLDataStorePutManager MySQL ", support.totalErrors());
                outputQueue.put(count);


                count = new StatCount(DataStoreSource.REMOTE_DB, Action.SET, "DB Connections Open MySQLDataStorePutManager MySQL ", support.totalConnectionOpen());
                outputQueue.put(count);


                count = new StatCount(DataStoreSource.REMOTE_DB, Action.SET, "DB Connections Closed MySQLDataStorePutManager MySQL ", support.totalClosedConnections());
                outputQueue.put(count);


                count = new StatCount(DataStoreSource.SERVER, Action.GET_STATS, "Thread TIME USER  MySQLDataStorePutManager "
                        + Thread.currentThread().getName(), Sys.threadUserTime());
                this.outputQueue.put(count);

                count = new StatCount(DataStoreSource.SERVER, Action.GET_STATS, "Thread TIME CPU  MySQLDataStorePutManager "
                        + Thread.currentThread().getName(), Sys.threadCPUTime());
                this.outputQueue.put(count);


            }


        }

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
