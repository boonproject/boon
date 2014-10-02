package org.boon.slumberdb.stores;


import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.BatchResult;
import org.boon.slumberdb.service.results.SearchBatchResult;
import org.boon.slumberdb.service.results.SingleResult;
import org.boon.slumberdb.service.search.SearchHandler;
import org.boon.Logger;
import org.boon.Pair;
import org.boon.concurrent.SimpleExecutors;
import org.boon.core.Sys;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;
import org.boon.slumberdb.StringKeyValueStore;
import org.boon.slumberdb.leveldb.SimpleStringKeyValueStoreLevelDB;
import org.boon.slumberdb.noop.StringKeyValueStoreNoOp;
import org.iq80.leveldb.Options;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.boon.Boon.configurableLogger;

/**
 * Created by Richard on 6/27/14.
 */
public class LevelDBDataStore extends BaseDataStore implements DataStore {

    private final boolean debug = GlobalConfig.DEBUG;
    StringKeyValueStore store;
    Thread loaderThread;
    private Logger logger = configurableLogger(this.getClass().getSimpleName());
    private ExecutorService executorService;

    public LevelDBDataStore() {
        super(DataStoreSource.LOCAL_DB);
    }

    public void init(final DataStoreConfig config,
                     DataOutputQueue queue,
                     final DataStore nextReaderDataStore
    ) {
        super.init(config, queue, nextReaderDataStore);


        this.executorService = SimpleExecutors.threadPool(config.levelDBBatchReaderCount(), "LevelDB BatchReader");

        File file = new File(config.outputDirectory());
        file.mkdirs();

        file = new File(file, config.levelDBFileName());

        Options options = new Options();
        options.cacheSize(config.levelDBCacheSize());
        options.writeBufferSize(config.levelDBWriteBufferSize());
        options.maxOpenFiles(config.levelDBMaxOpenFiles());

        try {
            store = new SimpleStringKeyValueStoreLevelDB(file.toString(), options, false);
        } catch (Exception ex) {
            store = StringKeyValueStoreNoOp.SINGLETON;
            if (debug) logger.error(ex, "UNABLE TO CONNECT TO LEVEL DB", ex.getMessage());
            logger.debug(ex, "UNABLE TO CONNECT TO LEVEL DB", ex.getMessage());
            logger.error("UNABLE TO CONNECT TO LEVEL DB", ex.getMessage());
        }
    }

    public KeyValueIterable<String, String> loadAll() {
        return store.loadAll();
    }


    @Override
    protected void recievedReadBatch(List<DataStoreRequest> operations) {

        for (DataStoreRequest currentOperation : operations) {

            switch (currentOperation.action()) {
                case BATCH_READ:
                    ReadBatchRequest batchReadOperation = (ReadBatchRequest) currentOperation;

                    final Collection<String> keys = batchReadOperation.keys();
                    final String clientId = batchReadOperation.clientId();

                    handleBatchRead((currentOperation).messageId(), clientId, keys);

                    break;
                case GET:
                case GET_LOCAL_DB:
                case GET_SOURCE:
                    GetRequest get = (GetRequest) currentOperation;
                    handleGet(get);
                    break;

            }

        }
    }

    @Override
    protected void recievedWriteBatch(List<DataStoreRequest> operations) {

        for (DataStoreRequest currentOperation : operations) {

            switch (currentOperation.action()) {
                case SET_BATCH_INTERNAL:
                case SET_BATCH_IF_NOT_EXISTS:
                case SET_BATCH:

                    BatchSetRequest addAllOperation = (BatchSetRequest) currentOperation;

                    store.putAll(addAllOperation.payloadAsMap());
                    break;


                case SET_INTERNAL:
                case SET:
                case SET_BROADCAST:
                case SET_SOURCE:
                    SetRequest putOperation = (SetRequest) currentOperation;

                    store.put(putOperation.key(), putOperation.payload());
                    break;
                case REMOVE:
                case REMOVE_SOURCE:
                    RemoveRequest remove = (RemoveRequest) currentOperation;
                    store.remove(remove.key());
                    break;


            }

        }
    }

    private void handleBatchRead(final long messsageId, final String clientId, Collection<String> keys) {


        int maxReadBatch = dataStoreConfig.dbMaxReadBatch();


        List<String> keysToFetch = new ArrayList<>(maxReadBatch);
        for (String key : keys) {
            keysToFetch.add(key);
            if (keysToFetch.size() > maxReadBatch) {

                doHandleBatchRead(messsageId, clientId, new ArrayList<>(keysToFetch));

                keysToFetch.clear();
            }

        }


        if (keysToFetch.size() > 0) {
            doHandleBatchRead(messsageId, clientId, keysToFetch);

        }


    }


    private void doHandleBatchRead(final long messageId, final String clientId, final Collection<String> keys) {

        executorService.submit(new Runnable() {
            @Override
            public void run() {

                List<String> keysLeft = new ArrayList<>();
                Map<String, String> results = new LinkedHashMap<>();
                for (String key : keys) {

                    String value = store.load(key);
                    if (value != null) {
                        results.put(key, value);
                    } else {
                        keysLeft.add(key);
                    }
                }

                if (results.size() > 0) {

                    BatchResult batchResult = new BatchResult(messageId, clientId,
                            DataStoreSource.LOCAL_DB, results);
                    outputDataQueue.put(batchResult);

                }

                if (keysLeft.size() > 0) {
                    nextDataStoreReadBatch(new ReadBatchRequest(messageId, clientId, keysLeft));
                }


            }
        });

    }

    private void handleGet(GetRequest request) {

        final String value = store.load(request.key());
        if (value != null) {


            SingleResult dataItem = new SingleResult(request.messageId(), request.clientId(),
                    DataStoreSource.LOCAL_DB,
                    request.key(), value);

            outputDataQueue.put(dataItem);
        } else {
            nextDataStoreGet(request);
        }
    }


    public String get(String key) {

        return this.store.load(key);
    }


    @Override
    public void search(SearchRequest searchRequest) {
        SearchHandler handler = searchRequest.handler();
        Map<String, String> results = handler.handle(this.store, searchRequest);
        outputDataQueue.put(new SearchBatchResult(searchRequest.messageId(),
                searchRequest.clientId(), DataStoreSource.MEMORY, results));
    }


    public List<Pair<String, String>> search(String prefix) {

        List<Pair<String, String>> results = new ArrayList<>(10_000);

        KeyValueIterable<String, String> search = null;

        try {
            search = this.store.search(prefix);

            for (Entry<String, String> entry : search) {
                results.add(new Pair<>(entry.key(), entry.value()));
            }

        } finally {
            if (search != null) search.close();
        }

        return results;

    }

    public void start() {
        super.start();

        final AtomicBoolean done = new AtomicBoolean();


        loaderThread = new Thread(new Runnable() {
            @Override
            public void run() {

                long start = System.currentTimeMillis();
                final KeyValueIterable<String, String> entries = loadAll();

                int load = 0;

                for (Entry<String, String> entry : entries) {

                    load++;
                    if (load % 1_000_000 == 0) {

                        logger.info("Loading in-memory concurrent map from Level DB. Loaded", load, "entries");
                    }
                }

                entries.close();


                long end = System.currentTimeMillis();

                long duration = end - start;


                logger.info("Done loading in-memory concurrent map from Level DB. Loaded", load, "entries", " in ", duration / 1_000, "seconds");

                done.set(true);
            }
        }, "Loader Thread");

        loaderThread.start();

        int countDown = 30;
        while (!done.get()) {
            Sys.sleep(1_000);
            if (countDown < 0) {
                logger.warn("Still loading data after 30 seconds, starting up async");
                break;
            }
            countDown--;
        }


    }


    public long deleteKeysByPrefix(String prefix) {
        KeyValueIterable<String, String> entries = null;
        long count = 0;

        try {
            entries = store.search(prefix);


            for (Entry<String, String> entry : entries) {
                store.remove(entry.key());
                count++;
            }
        } finally {

            if (entries != null) entries.close();
        }
        return count;
    }

    public long countKeyPrefix(String prefix) {
        KeyValueIterable<String, String> entries = null;
        long count = 0;

        try {
            entries = store.search(prefix);


            for (Entry<String, String> entry : entries) {
                store.remove(entry.key());
                count++;
            }
        } finally {
            if (entries != null) entries.close();
        }
        return count;
    }

    public long count() {
        KeyValueIterable<String, String> entries = null;
        long count = 0;

        try {
            entries = store.loadAll();


            for (Entry<String, String> entry : entries) {
                store.remove(entry.key());
                count++;
            }
        } finally {
            if (entries != null) entries.close();
        }
        return count;
    }

    public void stop() {

        super.stop();
        store.close();
        Sys.sleep(10_000);
    }

    public List<String> searchForKeys(String prefix) {


        List<String> results = new ArrayList<>(10_000);

        KeyValueIterable<String, String> search = null;

        try {
            search = this.store.search(prefix);

            for (Entry<String, String> entry : search) {
                results.add(entry.key());
            }

        } finally {
            if (search != null) search.close();
        }

        return results;


    }
}
