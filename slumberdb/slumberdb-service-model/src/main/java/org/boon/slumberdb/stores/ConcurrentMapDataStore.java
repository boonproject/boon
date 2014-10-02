package org.boon.slumberdb.stores;

import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.BatchResult;
import org.boon.slumberdb.service.results.SingleResult;
import org.boon.slumberdb.service.results.StatCount;
import org.boon.collections.LazyMap;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Richard on 6/27/14.
 */
public class ConcurrentMapDataStore extends BaseDataStore implements DataStore {

    private final ConcurrentHashMap<String, byte[]> map = new ConcurrentHashMap<>(100_000, 0.75f, 16);
    ThreadLocal<int[]> missCount = new ThreadLocal<>();
    ThreadLocal<int[]> batchReadMissCount = new ThreadLocal<>();
    public ConcurrentMapDataStore() {
        super(DataStoreSource.MEMORY);
    }

    public void init(final DataStoreConfig dataStoreConfig,
                     final DataOutputQueue queue, final DataStore nextReaderDataStore
    ) {
        super.init(dataStoreConfig, queue, nextReaderDataStore);
    }


    public void set(String key, String value) {
        map.put(key, value.getBytes(StandardCharsets.UTF_8));

    }

    public void set(SetRequest request) {
        set(request.key(), request.payload());
        this.writeOperationsQueue.offer(request);
    }


    public void addAll(BatchSetRequest request) {

        final List<String> keys = request.keys();

        final List<String> values = request.values();

        int index = 0;
        for (String key : keys) {

            String value = values.get(index);
            set(key, value);

            index++;
        }

        this.writeOperationsQueue.offer(request);

    }


    int[] missCount() {
        int[] count = missCount.get();
        if (count == null) {
            count = new int[1];
            missCount.set(count);
        }
        return count;
    }

    @Override
    public void get(GetRequest request) {
        final String value = this.get(request.key());
        if (value != null) {


            final int[] missCount = missCount();

            missCount[0]++;
            if (missCount[0] % 20 == 0) {
                StatCount count = new StatCount(super.source, Action.GET, "MISS COUNT", missCount[0]);
                super.outputDataQueue.put(count);
            }
            SingleResult dataItem = new SingleResult(request.messageId(), request.clientId(),
                    DataStoreSource.MEMORY, request.key(), value);

            outputDataQueue.put(dataItem);
        } else {
            nextDataStoreGet(request);
        }

        this.readOperationsQueue.offer(request);
    }

    @Override
    public void search(SearchRequest searchRequest) {

    }


    int[] batchReadMissCount() {
        int[] count = batchReadMissCount.get();

        if (count == null) {
            count = new int[1];
            batchReadMissCount.set(count);
        }
        return count;
    }

    @Override
    public void batchRead(ReadBatchRequest request) {


        int maxReadBatch = dataStoreConfig.dbMaxReadBatch();

        long messageId = request.messageId();
        String clientId = request.clientId();


        List<String> keysToFetch = new ArrayList<>(maxReadBatch);
        for (String key : request.keys()) {
            keysToFetch.add(key);
            if (keysToFetch.size() > maxReadBatch) {

                quickBatchRead(messageId, clientId, new ArrayList<>(keysToFetch));

                keysToFetch.clear();
            }

        }


        if (keysToFetch.size() > 0) {
            quickBatchRead(messageId, clientId, new ArrayList<>(keysToFetch));

        }


        this.readOperationsQueue.offer(request);

    }


    public int quickBatchRead(long messageId, final String clientId, final Collection<String> keys) {


        List<String> keysNotFound = new ArrayList<>();
        int count = 0;


        Map<String, String> results = (Map) new LazyMap();
        for (String key : keys) {

            String value = get(key);
            if (value != null) {
                count++;
                results.put(key, value);
            } else {
                keysNotFound.add(key);
            }
        }


        if (results.size() > 0) {
            BatchResult batchResult = new BatchResult(messageId, clientId,
                    DataStoreSource.MEMORY, results);
            outputDataQueue.put(batchResult);


        }

        if (keysNotFound.size() > 0) {


            final int[] missCount = batchReadMissCount();
            missCount[0] += keysNotFound.size();
            StatCount statCount = new StatCount(super.source, Action.BATCH_READ, "MISS COUNT", missCount[0]);
            super.outputDataQueue.put(statCount);

            nextDataStoreReadBatch(new ReadBatchRequest(messageId, clientId, keys));
        }
        return count;


    }

    @Override
    public void remove(RemoveRequest request) {
        map.remove(request.key());

        this.writeOperationsQueue.offer(request);
    }

    public void remove(String key) {
        map.remove(key);
    }


    @Override
    public void start() {
        //no op
    }

    @Override
    protected void recievedReadBatch(List<DataStoreRequest> dataItems) {
        //no op
    }


    @Override
    protected void recievedWriteBatch(List<DataStoreRequest> dataItems) {
        //no op
    }

    @Override
    public void stop() {
        //no op
    }


    public boolean exists(String key) {
        return map.contains(key);
    }


    public String get(String key) {

        final byte[] bytes = map.get(key);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }


    public long count() {
        return map.size();
    }
}