package org.boon.slumberdb.stores;

import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.BatchResult;
import org.boon.slumberdb.service.results.SingleResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Richard on 6/27/14.
 */
public class EndOfTheLineMapStore implements DataStore {


    protected DataOutputQueue outputDataQueue;

    public EndOfTheLineMapStore(DataOutputQueue outputDataQueue) {
        this.outputDataQueue = outputDataQueue;
    }

    @Override
    public void clearStats() {

    }

    @Override
    public void sendStats(long now) {

    }

    @Override
    public void set(SetRequest request) {

    }

    @Override
    public void get(GetRequest request) {

        SingleResult dataItem = new SingleResult(request.messageId(), request.clientId(), DataStoreSource.END, request.key(), null);
        outputDataQueue.put(dataItem);
    }

    @Override
    public void search(SearchRequest searchRequest) {

    }

    public void batchRead(ReadBatchRequest request) {

        if (request.keys().size() == 0) {
            return;
        }

        Map<String, String> results = new HashMap<>();
        for (String key : request.keys()) {
            results.put(key, null);
        }

        BatchResult batchResult = new BatchResult(request.messageId(), request.clientId(),
                DataStoreSource.END, results);
        outputDataQueue.put(batchResult);


    }

    @Override
    public void remove(RemoveRequest removeRequest) {

    }

    @Override
    public void addAll(BatchSetRequest batchSetRequest) {

    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }


}
