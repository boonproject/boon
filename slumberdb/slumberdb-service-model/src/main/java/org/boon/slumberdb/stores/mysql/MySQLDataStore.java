package org.boon.slumberdb.stores.mysql;


import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.SingleResult;
import org.boon.slumberdb.service.results.StatCount;
import org.boon.slumberdb.stores.BaseDataStore;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStore;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Lists;
import org.boon.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import static org.boon.Boon.configurableLogger;


public class MySQLDataStore extends BaseDataStore implements DataStore {


    private final boolean debug = GlobalConfig.DEBUG;
    private final LinkedTransferQueue<ReadBatchRequest> batchReadOperations = new LinkedTransferQueue<>();
    protected List<ScheduledExecutorService> scheduledExecutorServices = new ArrayList<>();
    protected List<Future<?>> futures = new ArrayList<>();
    int sqlBatchWrite;
    private Logger logger = configurableLogger(this.getClass().getSimpleName());
    private long lastReadFlushTime;
    private long lastWriteFlushTime;
    /**
     * Holds the readMapKeyToGetOperation result so we can marry the database reads to the SingleResult.
     */
    private ConcurrentMap<String, List<GetRequest>> readMapKeyToGetOperation = new ConcurrentHashMap<>();
    /**
     * Holds the outstanding read requests, i.e., the ones we have not sent yet to MySQLDataStoreLoader.
     */
    private List<String> readList = new ArrayList<>();
    private List<String> removeList = new ArrayList<>();
    private LinkedTransferQueue<Map<String, String>> loadedResultsFromDBQueue = new LinkedTransferQueue<>();
    private LinkedTransferQueue<List<String>> loadQueue = new LinkedTransferQueue<>();
    private LinkedTransferQueue<Object> writeQueue = new LinkedTransferQueue<>();
    private Set<String> keyblackList = new HashSet<>();
    private boolean hasBlackList;
    private Map<String, String> outputMap = new ConcurrentHashMap<>();
    private int numReaders = 5;
    private int numWriters = 5;
    private int maxReadBatch = 1000;
    private int minReadBatch = 100;
    private int maxWriteBatch = 10_000;
    private int flushQueueInterval = 250;
    private String url;
    private String user;
    private String password;
    private String table;


    public MySQLDataStore() {
        super(DataStoreSource.REMOTE_DB);
    }

    public void init(final DataStoreConfig config, DataOutputQueue queue, DataStore nextReaderDataStore) {


        super.init(config, queue, nextReaderDataStore);


        url = config.dbUrl();
        user = config.dbUser();
        password = config.dbPassword();
        table = config.dbTable();
        numReaders = config.dbReaderCount();

        numWriters = config.dbWriterCount();

        maxReadBatch = config.dbMaxReadBatch();

        minReadBatch = config.dbMinReadBatch();
        maxWriteBatch = config.dbMaxWriteBatch();

        sqlBatchWrite = config.sqlBatchWrite();

        flushQueueInterval = config.dbReadFlushQueueIntervalMS();

        if (debug) {
            logger.info(config);
        }

        initBlackListSet(config);

        if (this.keyblackList.size() > 0) {
            this.hasBlackList = true;
        }


    }

    private void initBlackListSet(DataStoreConfig config) {
        final List<String> blackList = config.mySQLKeyBlackList();

        if (blackList != null) {
            for (String key : blackList) {
                this.keyblackList.add(key);
            }
        }
    }


    /**
     * Will flush to secondary storage if needed. This is used mostly by REMOTE_DB at the moment.
     */
    protected void flushReadsIfNeeded() throws InterruptedException {
        long now = timer.time();

        /* Every 250 ms flush the read queue. */
        if (now - lastReadFlushTime > flushQueueInterval && readList.size() > 0) {


            if (!loadQueue.offer(new ArrayList<>(readList))) {
                logger.warn("MySQL LOAD QUEUE IS FULL", loadQueue.size());
            } else {
                readList.clear();
            }
            lastReadFlushTime = now;


        }
    }


    /**
     * Will flush to secondary storage if needed. This is used mostly by REMOTE_DB at the moment.
     */
    protected void flushWritesIfNeeded() throws InterruptedException {

        long now = timer.time();

        /* Every 30 seconds  flush the write queue. */
        if (now - lastWriteFlushTime > dataStoreConfig.dbWriteFlushQueueIntervalMS()) {

            if (outputMap.size() > 0) {

                if (!writeQueue.offer(new LinkedHashMap(outputMap))) {
                    logger.warn("MySQL STORE QUEUE IS FULL", writeQueue.size());

                } else {
                    outputMap.clear();
                }

                lastWriteFlushTime = now;
            }


            if (removeList.size() > 0) {


                if (!writeQueue.offer(new ArrayList<>(removeList))) {
                    logger.warn("MySQL LOAD QUEUE IS FULL", writeQueue.size());
                } else {
                    removeList.clear();
                }


            }


        }
    }


    public void processLoadResults() throws InterruptedException {

        long recordsLoaded = 0;

        long notFound = 0;

        while (true) {

            Map<String, String> results = loadedResultsFromDBQueue.poll(dataStoreConfig.pollTimeoutMS(), TimeUnit.MILLISECONDS);

            if (results != null) {

                for (Map.Entry<String, String> entry : results.entrySet()) {
                    final List<GetRequest> getOperations = readMapKeyToGetOperation.put(entry.getKey(), Lists.safeList(GetRequest.class));
                    if (getOperations == null) {
                        continue;
                    }
                    recordsLoaded++;

                    for (GetRequest getOperation : getOperations) {

                        if (entry.getValue() != null) {


                            SingleResult dataItem = new SingleResult(getOperation.messageId(), getOperation.clientId(),
                                    DataStoreSource.REMOTE_DB, entry.getKey(), entry.getValue());
                            outputDataQueue.put(dataItem);
                        } else {
                            //If the value was null, i.e., not found, we can send it to the next data store.
                            nextDataStoreGet(getOperation);
                            notFound++;
                        }
                    }
                }


                if (recordsLoaded % 1_000 == 0) {

                    StatCount count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "recordsLoaded from MySQL", recordsLoaded);
                    outputDataQueue.put(count);

                    count = new StatCount(DataStoreSource.REMOTE_DB, Action.GET, "notFound from MySQL", notFound);
                    outputDataQueue.put(count);

                }

            }


        }
    }

    @Override
    protected void recievedReadBatch(List<DataStoreRequest> operations) throws InterruptedException {

        handleGets(operations);


        handleBatchRead(operations);

    }

    private void handleBatchRead(List<DataStoreRequest> operations) {


        for (DataStoreRequest dataStoreRequest : operations) {
            if (dataStoreRequest instanceof ReadBatchRequest) {
                this.batchReadOperations.offer((ReadBatchRequest) dataStoreRequest);
            }
        }


    }


    @Override
    protected void recievedWriteBatch(List<DataStoreRequest> operations) throws InterruptedException {


        handleAddAll(operations);
        handlePuts(operations);
        handleRemove(operations);

    }


    private void handleGets(List<DataStoreRequest> operations) throws InterruptedException {


        for (DataStoreRequest operation : operations) {

            if (operation instanceof GetRequest) {
                GetRequest getOperation = (GetRequest) operation;
                readList.add(getOperation.key());

                final List<GetRequest> items = readMapKeyToGetOperation.get(getOperation.key());
                if (items == null) {
                    readMapKeyToGetOperation.put(getOperation.key(), Lists.safeList(getOperation));
                } else {
                    items.add(getOperation);
                }
            }

        }

        /* If the readList is greater than MIN_WRITE_BATCH. */
        if (readList.size() > minReadBatch) {
            /* If the loadPushQueue is empty then push this read on to the queue NOW. */
            if (loadQueue.size() == 0) {

                try {
                    loadQueue.put(new ArrayList<>(readList));
                    readList.clear();

                } catch (Exception ex) {
                    logger.error(ex, "Unable to add an item to the load queue, this means we can't read from MySQL");
                }


            }
            /* If the load queue was not empty than there is not much we can do so we might as well go ahead
            and batch read requests. */
            else {
                /* If we are here it means that at least one outstanding request is being processed the MySQLDataStoreLoaders */

                /* If it is bigger than the max batch size then put the readList on the queue. */
                if (readList.size() >= maxReadBatch) {
                    try {
                        loadQueue.put(new ArrayList<>(readList));
                    } catch (Exception ex) {
                        logger.error(ex, "Unable to add an item to the load queue, this means we can't read from MySQL");
                    } finally {
                        readList.clear();
                    }
                }
            }
        }

    }


    @Override
    public void search(SearchRequest searchRequest) {

    }

    public void addAll(BatchSetRequest request) {

        Map<String, String> newMap = new LinkedHashMap<>(request.payloadAsMap());

        for (String blackListKey : keyblackList) {
            newMap.remove(blackListKey);
        }

        this.writeOperationsQueue.offer(new BatchSetRequest(request.messageId(), request.clientId(), newMap));
    }


    private void handleAddAll(List<DataStoreRequest> operations) {

        for (DataStoreRequest operation : operations) {
            if (operation.action() == Action.SET_BATCH) {
                BatchSetRequest addAllOperation = (BatchSetRequest) operation;
                writeQueue.put(addAllOperation.payloadAsMap());
            }
        }
    }


    private void handleRemove(List<DataStoreRequest> operations) {

        for (DataStoreRequest dataItem : operations) {

            if (dataItem instanceof RemoveRequest) {

                RemoveRequest request = (RemoveRequest) dataItem;

                if (hasBlackList) {
                    if (keyblackList.contains(request.key())) {
                        continue;
                    }
                }

                removeList.add(request.key());


                if (removeList.size() >= maxWriteBatch) {
                    try {
                        writeQueue.put(outputMap);
                        removeList.clear();

                    } catch (Exception ex) {
                        logger.fatal(ex, "MySQL Store, Unable to add an item to the store queue for REMOVE, this means we can't write to MySQL",
                                removeList.size(), "queue size", writeQueue.size());
                    }
                }

            }
        }

    }


    private void handlePuts(List<DataStoreRequest> dataItems) throws InterruptedException {
        for (DataStoreRequest dataItem : dataItems) {

            if (dataItem instanceof SetRequest) {

                SetRequest putOperation = (SetRequest) dataItem;

                if (hasBlackList) {
                    if (keyblackList.contains(putOperation.key())) {
                        continue;
                    }
                }

                outputMap.put(putOperation.key(), putOperation.payload());
            }
        }


        if (outputMap.size() > 0 && writeQueue.size() == 0) {
            try {
                writeQueue.put(new HashMap<>(outputMap));
                outputMap.clear();
            } catch (Exception ex) {
                logger.error(ex, "MySQL Store, Unable to add an item to the store queue, this means we can't write to MySQL buf size",
                        outputMap.size(), "queue size", writeQueue.size());
            }
        }

        if (outputMap.size() >= maxWriteBatch) {
            try {
                writeQueue.put(outputMap);
                outputMap.clear();

            } catch (Exception ex) {
                logger.fatal(ex, "MySQL Store, Unable to add an item to the store queue, this means we can't write to MySQL",
                        outputMap.size(), "queue size", writeQueue.size());
            }
        }
    }


    public void stop() {
        super.stop();


        for (Future future : futures) {

            try {
                future.cancel(true);
            } catch (Exception ex) {

            }
        }

        for (ScheduledExecutorService service : scheduledExecutorServices) {

            try {
                service.shutdown();
            } catch (Exception ex) {

            }
        }

    }


    public void start() {


        super.start();

        startWorker("Process Load Results", new Runnable() {
            @Override
            public void run() {
                if (stop.get()) {
                    return;
                }
                try {
                    processLoadResults();
                } catch (InterruptedException iex) {

                    if (stop.get()) {
                        if (debug) logger.info("Thread stopped with stop flag.");
                        return;
                    } else {
                        Thread.interrupted();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error(ex, "MySQLDataStore ", ex.getMessage());
                    handleSQLException(ex);

                }
            }
        });


        for (int index = 0; index < numWriters; index++) {


            final MySQLDataStorePutManager putManager = new MySQLDataStorePutManager(outputDataQueue, dataStoreConfig, stop, writeQueue,
                    url, user, password, table);


            startWorker("Writer " + index, new Runnable() {
                @Override
                public void run() {
                    try {
                        putManager.run();
                    } catch (Exception ex) {
                        logger.error(ex, "MySQLDataStore ", ex.getMessage());
                        handleSQLException(ex);

                    }
                }
            });
        }


        for (int index = 0; index < numReaders; index++) {


            final MySQLDataStoreLoader loader = new MySQLDataStoreLoader(dataStoreConfig,
                    outputDataQueue,
                    stop,
                    loadedResultsFromDBQueue,
                    loadQueue,
                    url,
                    user,
                    password,
                    table);


            startWorker("Reader " + index, new Runnable() {
                @Override
                public void run() {
                    try {
                        loader.run();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        logger.error(ex, "MySQLDataStore Batch Loader", ex.getMessage());
                        handleSQLException(ex);
                    }
                }
            });
        }

        for (int index = 0; index < numReaders; index++) {


            final MySQLBatchLoader loader = new MySQLBatchLoader(dataStoreConfig, stop, outputDataQueue,
                    this.batchReadOperations, url, user, password, table);


            startWorker("Batch Reader " + index, new Runnable() {
                @Override
                public void run() {
                    try {
                        loader.run();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        logger.error(ex, "MySQLDataStore Batch Loader", ex.getMessage());
                        handleSQLException(ex);

                    }
                }
            });
        }

    }

    public void handleSQLException(Throwable ex) {

        if (ex instanceof SQLException) {
            SQLException sqlException = (SQLException) ex;

            SQLException next = sqlException.getNextException();

            while (next != null) {
                logger.error(next, "MySQLDataStore Nested SQL Exception", next.getMessage());
                next = sqlException.getNextException();

            }

        } else {
            handleSQLException(ex.getCause());
        }

    }


    private void startWorker(final String name, final Runnable runnable) {


        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable);
                        thread.setName(this.getClass().getSimpleName() + " " + name);
                        return thread;
                    }
                }
        );

        scheduledExecutorServices.add(scheduledExecutorService);


        Future<?> future;

        future = scheduledExecutorService.scheduleAtFixedRate(
                runnable, 0, dataStoreConfig.threadErrorResumeTimeMS(), TimeUnit.MILLISECONDS);
        futures.add(future);


    }


}
