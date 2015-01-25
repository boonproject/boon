package org.boon.slumberdb.service.server;

import org.boon.Logger;
import org.boon.Maps;
import org.boon.Str;
import org.boon.collections.LazyMap;
import org.boon.concurrent.Timer;
import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.requests.BatchSetRequest;
import org.boon.slumberdb.service.protocol.requests.SetRequest;
import org.boon.slumberdb.service.results.*;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.slumberdb.stores.MasterDataStore;
import org.boon.slumberdb.stores.queue.DataOutputQueueTransferQueue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Boon.configurableLogger;
import static org.boon.Boon.puts;

/**
 * Created by Richard on 9/10/14.
 */
public class ResponseHandler {

    private static Map<String, StatCount> stats = new ConcurrentHashMap<>();
    private final boolean debug = GlobalConfig.DEBUG;
    Status status = new Status();
    long time;
    long lastStatusSend = 0;
    private int drainerId;
    private long lastStatusDisplay = Timer.timer().time();
    private MasterDataStore masterDataStore = null;
    private Logger logger = configurableLogger(this.getClass());




    /*

.-.   .-.  .--.  .-..-. .-.
|  `.'  | / {} \ | ||  `| |
| |\ /| |/  /\  \| || |\  |
`-' ` `-'`-'  `-'`-'`-' `-'
.----..-.  .-..-. .---.
| {_   \ \/ / | |{_   _}
| {__  / /\ \ | |  | |
`----'`-'  `-'`-'  `-'
.----..----.  .----. .-.   .-.   .----.   .--.  .---.  .--.      .----..---.  .----. .----. .----.
| {_  | {}  }/  {}  \|  `.'  |   | {}  \ / {} \{_   _}/ {} \    { {__ {_   _}/  {}  \| {}  }| {_
| |   | .-. \\      /| |\ /| |   |     //  /\  \ | | /  /\  \   .-._} } | |  \      /| .-. \| {__
`-'   `-' `-' `----' `-' ` `-'   `----' `-'  `-' `-' `-'  `-'   `----'  `-'   `----' `-' `-'`----'

     */
    private DataStoreServerConfig config;
    private DataOutputQueueTransferQueue queue = null;
    private DataStoreServer storeServer = null;

    /**
     * Main exit from system.
     * <p/>
     * <p/>
     * If you are debugging something not coming out of the system.
     * Start here.
     */
    private void handleResponseFromDataStore(Result result) {

        if (debug) {
            logger.info("ResponseHandler::handleResponseFromDataStore", result);
        }

        if (result instanceof SingleResult) {
            SingleResult singleResult = (SingleResult) result;
            int size = handleSingleResult(singleResult);
            counter(size, singleResult.source());
        }
        else if (result instanceof SearchBatchResult) {
            SearchBatchResult searchBatchResult = (SearchBatchResult) result;
            sendBatchResponse((BatchResult) result);
            int size = searchBatchResult.getResults().size();
            counter(size, searchBatchResult.source());
        }
        else if (result instanceof BatchResult) {
            BatchResult batchResult = (BatchResult) result;
            int size = handleBatchResult(batchResult);
            counter(size, batchResult.source());
        }
        else if (result instanceof ErrorResult) {
            ErrorResult errorResult = (ErrorResult) result;
            int size = handleErrorResult(errorResult);
            counter(size, errorResult.source());
        }
    }

    private int handleBatchResult(BatchResult batchResult) {
        int size = batchResult.getResults().size();
        switch (batchResult.source()) {
            case LOCAL_DB:
                masterDataStore.addAll(new BatchSetRequest(DataStoreSource.MEMORY,
                        Action.SET_BATCH_INTERNAL, batchResult.messageId(), batchResult.clientId(), batchResult.getResults()));
                break;
            case REPLICA:
            case REMOTE_DB:
                masterDataStore.addAll(new BatchSetRequest(DataStoreSource.LOCAL_STORES,
                        Action.SET_BATCH_INTERNAL, batchResult.messageId(), batchResult.clientId(), batchResult.getResults()));
                break;


        }

        if (size > 0) {
            sendMapInBatches(batchResult);
        }
        return size;
    }

    private int handleSingleResult(SingleResult result) {
        String clientId = result.clientId();

        switch (result.source()) {
            case LOCAL_DB:
                masterDataStore.set(result.key(), result.getValue());
                break;
            case REPLICA:
            case REMOTE_DB:
                masterDataStore.setSource(new SetRequest(DataStoreSource.LOCAL_STORES, Action.SET_INTERNAL, result));
                break;
        }

        storeServer.sendMessageToClientId(clientId, result.toTextMessage());

        return 1;
    }

    private int handleErrorResult(ErrorResult result) {
        String clientId = result.clientId();
        String message = "[\"error\",\"" + result.getTitle() + "\",\"" + result.source().toString() + "\"]";
        storeServer.sendMessageToClientId(clientId, message);
        return 1;
    }

    private void readDataSourceOutputQueue() {
        Response queueItem;

        while (true) {

            queueItem = queue.take();

            showStatus(time, queueItem);

            while (queueItem != null) {

                if (queueItem instanceof Result) {
                    handleResponseFromDataStore((Result) queueItem);

                } else if (queueItem instanceof Stat) {
                    handleStatFromDataStore((Stat) queueItem);
                }
                queueItem = queue.poll();


            }

            time = Timer.timer().time();

            sendWorkerStats(time);
        }

    }

    private void sendWorkerStats(long now) {

        long duration = now - lastStatusSend;
        if (duration > 30_000) {


            lastStatusSend = now;

            StatCount count;

            count = new StatCount(now, DataStoreSource.LOCAL_DB, Action.GET,
                    Str.addObjects("RESPONSE HANDLER ", drainerId),
                    this.status.fromLocalDB);

            this.queue.put(count);

            count = new StatCount(now, DataStoreSource.REMOTE_DB, Action.GET,
                    Str.addObjects("RESPONSE HANDLER ", drainerId),
                    this.status.fromRemoteDB);

            this.queue.put(count);


            count = new StatCount(now, DataStoreSource.MEMORY, Action.GET,
                    Str.addObjects("RESPONSE HANDLER ", drainerId),
                    this.status.fromMemory);

            this.queue.put(count);

            count = new StatCount(now, DataStoreSource.END, Action.GET,
                    Str.addObjects("RESPONSE HANDLER ", drainerId),
                    this.status.notFound);

            this.queue.put(count);
        }

    }

    private void showStatus(long time, Response queueItem) {

        if (queueItem == null) {


            long duration = time - lastStatusDisplay;

            if (duration > (config.statusInterval() + (drainerId * 100))) {
                lastStatusDisplay = time;
                puts("_____ Read Status ___", drainerId);
                puts("_____________________");

                puts("    FROM MEMORY    ", status.fromMemory);
                puts("    FROM LOCAL DB  ", status.fromLocalDB);
                puts("    FROM REMOTE DB ", status.fromRemoteDB);
                puts("    NOT FOUND      ", status.notFound);
                puts("");
            }
        }

    }

    public void startQueueDrainer() {
        storeServer.startWorker("ResponseHandler for DataStore " + this.drainerId,
                new Runnable() {
                    @Override
                    public void run() {
                        readDataSourceOutputQueue();
                    }
                }
        );

    }

    public void init(DataStoreServer dataStoreServer, DataStoreServerConfig config, int index,
                     MasterDataStore masterDataStore, DataOutputQueueTransferQueue queue) {

        this.storeServer = dataStoreServer;
        this.config = config;
        this.drainerId = index;
        this.masterDataStore = masterDataStore;
        this.queue = queue;
        startQueueDrainer();
    }

    private void sendMapInBatches(BatchResult batchResult) {

        int batchResultSize = config.batchResultSize();
        final String clientId = batchResult.clientId();
        final Map<String, String> results = batchResult.getResults();

        if (debug) logger.info("ResponseHandler::sendMapInBatches()", clientId, results.size());
        if (results.size() <= batchResultSize) {
            sendBatchResponse(batchResult);
        } else {

            LazyMap map = new LazyMap(results.size() > batchResultSize ? batchResultSize : results.size(), true);

            final Set<String> keys = results.keySet();

            for (String key : keys) {
                map.put(key, results.get(key));
                if (map.size() >= batchResultSize) {
                    sendBatchResponse(new BatchResult(batchResult, map.clearAndCopy()));

                }
            }


            if (map.size() > 0) {
                sendBatchResponse(new BatchResult(batchResult, map.clearAndCopy()));
            }

        }
    }

    private void sendBatchResponse(BatchResult batchResult) { //(final String clientId, Map<String, Object> map) {

        final String clientId = batchResult.clientId();
        if (!storeServer.clientExistsStill(clientId)) {
            return;
        }


        if (debug) logger.info("ResponseHandler::sendBatchResponse", batchResult);

        if (batchResult.getResults().size() == 0) {
            return;
        }


        storeServer.sendMessageToClientId(clientId, batchResult.toTextMessage());
    }

    private void counter(StatCount statCount) {

        stats.put(statCount.toKey(), statCount);
    }

    private void counter(int size, DataStoreSource source) {


        switch (source) {
            case LOCAL_DB:
                status.fromLocalDB += size;
                break;
            case REMOTE_DB:
                status.fromRemoteDB += size;
                break;
            case MEMORY:
                status.fromMemory += size;
                break;
            case END:
                status.notFound += size;
                break;

            default:
                logger.warn("ResponseHandler::UNKNOWN source", source, size);
        }

    }

    private void handleStatFromDataStore(Stat stat) {
        if (stat instanceof StatCount) {
            counter((StatCount) stat);
        } else if (stat instanceof StatsResults) {
            handleCollectStats((StatsResults) stat);
        }
    }

    private void handleCollectStats(StatsResults result) {
        Map<String, StatCount> map = Maps.copy(stats);

        result.setResults(map);

        storeServer.sendMessageToClientId(result.request().clientId(), result.toTextMessage());
    }

    private static class Status {
        long fromRemoteDB;
        long fromLocalDB;
        long fromMemory;
        long notFound;
    }


}
