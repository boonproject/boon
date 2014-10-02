package org.boon.slumberdb.stores;

import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.StatCount;
import org.boon.Logger;
import org.boon.Pair;
import org.boon.collections.IntList;
import org.boon.concurrent.Timer;
import org.boon.core.Sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.boon.Boon.configurableLogger;

/**
 * Created by Richard on 6/27/14.
 */
public abstract class BaseDataStore {


    protected final DataStoreSource source;
    protected LinkedTransferQueue<DataStoreRequest> writeOperationsQueue = new LinkedTransferQueue<>();
    protected LinkedTransferQueue<DataStoreRequest> readOperationsQueue = new LinkedTransferQueue<>();
    protected AtomicBoolean stop = new AtomicBoolean();
    protected DataOutputQueue outputDataQueue;
    protected DataStore nextReaderDataStore;
    protected ScheduledExecutorService scheduledExecutorService;
    protected DataStoreConfig dataStoreConfig;
    protected List<DataStoreRequest> readOperationsBatch = new ArrayList<>();
    protected List<DataStoreRequest> writeOperationsBatch = new ArrayList<>();
    protected Future<?> future;
    protected Timer timer = Timer.timer();
    private Logger logger = configurableLogger(this.getClass());


    protected BaseDataStore(DataStoreSource source) {
        this.source = source;
    }

    public void clearStats() {

    }

    public void sendStats(long now) {

    }

    public void init(DataStoreConfig dataStoreConfig, DataOutputQueue queue,
                     DataStore nextReaderDataStore) {
        this.dataStoreConfig = dataStoreConfig;
        this.outputDataQueue = queue;
        this.nextReaderDataStore = nextReaderDataStore;
    }


    public void set(SetRequest setRequest) {
        this.writeOperationsQueue.offer(setRequest);

    }

    public void addAll(BatchSetRequest batchSetRequest) {
        this.writeOperationsQueue.offer(batchSetRequest);

    }


    public void get(GetRequest request) {
        this.readOperationsQueue.offer(request);
    }

    /**
     * Called from worker thread.
     *
     * @throws InterruptedException
     */
    private void processReadQueue() throws InterruptedException {

        ReadStatus readStatus = new ReadStatus();


        while (true) {

            DataStoreRequest request = readOperationsQueue.poll(dataStoreConfig.pollTimeoutMS(), TimeUnit.MILLISECONDS);

            while (request != null) {

                readStatus.tracker.addCall(request, outputDataQueue);
                readOperationsBatch.add(request);
                if (readOperationsBatch.size() > dataStoreConfig.processQueueMaxBatchSize()) {
                    break;
                }
                request = readOperationsQueue.poll();

            }

            if (readOperationsBatch.size() > 0) {

                try {
                    recievedReadBatch(new ArrayList<>(readOperationsBatch));
                } finally {
                    readOperationsBatch.clear();
                }
            } else {
                flushReadsIfNeeded();
            }


            if (readStatus.readBatchSize.size() > 1_000) {


                StatCount count;
                final long now = Timer.timer().time();


                count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread TIME USER  BaseDataStore "
                        + Thread.currentThread().getName(), Sys.threadUserTime());
                this.outputDataQueue.put(count);

                count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread TIME CPU  BaseDataStore "
                        + Thread.currentThread().getName(), Sys.threadCPUTime());
                this.outputDataQueue.put(count);

                count = new StatCount(now, source, Action.GET, "BaseDataStore readStatus.readBatchSize.max", readStatus.readBatchSize.max());
                outputDataQueue.put(count);


                count = new StatCount(now, source, Action.GET, "BaseDataStore readStatus.readBatchSize.min", readStatus.readBatchSize.min());
                outputDataQueue.put(count);


                count = new StatCount(now, source, Action.GET, "BaseDataStore readStatus.readBatchSize.median", readStatus.readBatchSize.median());
                outputDataQueue.put(count);


                count = new StatCount(now, source, Action.GET, "BaseDataStore readStatus.readBatchSize.mean", readStatus.readBatchSize.mean());
                outputDataQueue.put(count);


                count = new StatCount(now, source, Action.GET, "BaseDataStore readStatus.readBatchSize.standardDeviation", readStatus.readBatchSize.standardDeviation());
                outputDataQueue.put(count);


                count = new StatCount(now, source, Action.GET, "BaseDataStore readStatus.readBatchSize.variance", readStatus.readBatchSize.variance());
                outputDataQueue.put(count);


                readStatus.readBatchSize.clear();

            }
        }

    }

    /**
     * Called from worker thread.
     * Processes the incoming queue for read and writes.
     */
    private void processWriteQueue() throws InterruptedException {

        WriteStatus status = new WriteStatus();

        while (true) {

            DataStoreRequest operation = writeOperationsQueue.poll(dataStoreConfig.pollTimeoutMS(), TimeUnit.MILLISECONDS);

            while (operation != null) {
                status.tracker.addCall(operation, outputDataQueue);
                writeOperationsBatch.add(operation);
                if (writeOperationsBatch.size() > dataStoreConfig.processQueueMaxBatchSize()) {
                    break;
                }
                operation = writeOperationsQueue.poll();

            }

            if (writeOperationsBatch.size() > 0) {

                try {
                    status.writeBatchSize.add(writeOperationsBatch.size());
                    recievedWriteBatch(new ArrayList<>(writeOperationsBatch));
                } finally {
                    writeOperationsBatch.clear();
                }
            } else {
                flushWritesIfNeeded();
            }

            if (status.writeBatchSize.size() > 1000) {

                status.sendBatchSize(source, outputDataQueue);
            }
        }
    }

    /**
     * Will flush to secondary storage if needed. This is used mostly by REMOTE_DB at the moment.
     */
    protected void flushReadsIfNeeded() throws InterruptedException {
    }

    /**
     * Will flush to secondary storage if needed. This is used mostly by REMOTE_DB at the moment.
     */
    protected void flushWritesIfNeeded() throws InterruptedException {
    }

    /**
     * Async send batches of data items to subclasses.
     */
    protected abstract void recievedReadBatch(List<DataStoreRequest> dataItems) throws InterruptedException;

    /**
     * Async send batches of data items to subclasses.
     */
    protected abstract void recievedWriteBatch(List<DataStoreRequest> dataItems) throws InterruptedException;

    public void stop() {

        stop.set(true);
        future.cancel(true);
        scheduledExecutorService.shutdownNow();

    }

    protected void nextDataStoreGet(GetRequest request) {
        try {
            nextReaderDataStore.get(request);
        } catch (Exception ex) {
            logger.error(ex, "Can't call next reader data store", nextReaderDataStore, request);
        }
    }

    protected void nextDataStoreReadBatch(ReadBatchRequest request) {

        try {

            nextReaderDataStore.batchRead(request);
        } catch (Exception ex) {
            logger.error(ex, "Can't call next reader data store for batch read", nextReaderDataStore);
        }

    }

    public void batchRead(ReadBatchRequest request) {

        readOperationsQueue.add(request);
    }

    public void remove(RemoveRequest request) {
        writeOperationsQueue.add(request);
    }

    /**
     * Start up the queue handlers.
     */
    public void start() {


        scheduledExecutorService = Executors.newScheduledThreadPool(2,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable);
                        thread.setName(" DataQueue Process " + source);
                        return thread;
                    }
                }
        );


        future = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {


            @Override
            public void run() {

                if (stop.get()) {
                    return;
                }
                try {
                    processWriteQueue();
                } catch (InterruptedException ex) {
                    //let it restart or stop

                } catch (Exception ex) {
                    logger.fatal(ex);
                }
            }
        }, 0, dataStoreConfig.threadErrorResumeTimeMS(), TimeUnit.MILLISECONDS);


        future = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {


            @Override
            public void run() {

                if (stop.get()) {
                    return;
                }
                try {
                    processReadQueue();
                } catch (InterruptedException ex) {
                    //let it restart or stop

                } catch (Exception ex) {
                    logger.fatal(ex, "Problem with base data store running scheduled job");
                }
            }
        }, 0, dataStoreConfig.threadErrorResumeTimeMS(), TimeUnit.MILLISECONDS);


    }

    private static class ReadStatus {

        IntList readBatchSize = new IntList();
        CallStatusTracker tracker = new CallStatusTracker();
    }

    private static class CallStatus {

        Action action;
        DataStoreSource source;
        int count;
    }

    private static class CallStatusTracker {

        long numberOfCallsSinceLastSendAttempt = 0;
        long totalNumCalls = 0;
        long lastStatSend = 0;
        Map<Pair<Action, DataStoreSource>, CallStatus> callStatusMap = new HashMap<>();

        void addCall(DataStoreRequest request, DataOutputQueue queue) {

            Pair<Action, DataStoreSource> key = Pair.pair(request.action(), request.source());
            CallStatus callStatus = callStatusMap.get(
                    key);


            if (callStatus == null) {
                callStatus = new CallStatus();
                callStatus.action = request.action();
                callStatus.source = request.source();
                callStatus.count = request.count();
                callStatusMap.put(key, callStatus);
            } else {

                callStatus.action = request.action();
                callStatus.source = request.source();
                callStatus.count += request.count();
            }

            numberOfCallsSinceLastSendAttempt++;
            if (numberOfCallsSinceLastSendAttempt > 100) {
                totalNumCalls += numberOfCallsSinceLastSendAttempt;
                numberOfCallsSinceLastSendAttempt = 0;

                long now = Timer.timer().time();
                long duration = now - lastStatSend;

                /* Only send every 20 seconds. */
                if (duration > 20_000) {
                    for (CallStatus stat : callStatusMap.values()) {
                        StatCount count = new StatCount(now, stat.source,
                                stat.action,
                                "DATA SOURCE ACCESS ",
                                stat.count);
                        queue.put(count);
                    }

                    StatCount count = new StatCount(now, DataStoreSource.SERVER,
                            Action.NONE,
                            "DATA SOURCE ACCESS " + Thread.currentThread().getName(),
                            totalNumCalls);
                    queue.put(count);


                    try {

                        count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread CPU Time " + Thread.currentThread().getName(), Sys.threadCPUTime());
                        queue.put(count);

                    } catch (Throwable ex) {
                    }


                    lastStatSend = now;
                }
            }
        }

        public void clearStats() {

            numberOfCallsSinceLastSendAttempt = 0;
            totalNumCalls = 0;
            lastStatSend = 0;
            callStatusMap.clear();
        }
    }

    private static class WriteStatus {

        IntList writeBatchSize = new IntList();
        CallStatusTracker tracker = new CallStatusTracker();

        public void sendBatchSize(DataStoreSource source, DataOutputQueue queue) {

            int max = writeBatchSize.max();
            int mean = writeBatchSize.max();
            int min = writeBatchSize.max();
            int median = writeBatchSize.median();
            int standardDeviation = writeBatchSize.standardDeviation();
            int variance = writeBatchSize.variance();


            StatCount count;
            final long now = Timer.timer().time();

            count = new StatCount(now, DataStoreSource.SERVER, Action.GET_STATS, "Thread request handler User Time", Sys.threadUserTime());
            queue.put(count);

            count = new StatCount(now, source,
                    Action.SET, "DataStore WRITE BATCH SIZE max", max
            );
            queue.put(count);

            count = new StatCount(now, source,
                    Action.SET, "DataStore WRITE BATCH SIZE mean", mean
            );
            queue.put(count);

            count = new StatCount(now, source,
                    Action.SET, "DataStore WRITE BATCH SIZE min", min
            );
            queue.put(count);


            count = new StatCount(now, source,
                    Action.SET, "DataStore WRITE BATCH SIZE median", median
            );
            queue.put(count);


            count = new StatCount(now, source,
                    Action.SET, "DataStore WRITE BATCH SIZE standardDeviation", standardDeviation
            );
            queue.put(count);


            count = new StatCount(now, source,
                    Action.SET, "DataStore WRITE BATCH SIZE variance", variance
            );
            queue.put(count);

            writeBatchSize.clear();
        }
    }

}
