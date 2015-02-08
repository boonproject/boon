package org.boon.slumberdb.service.client;

import org.boon.HTTP;
import org.boon.Lists;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.BatchResult;
import org.boon.slumberdb.service.results.SingleResult;
import org.boon.slumberdb.service.results.StatsResults;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.slumberdb.stores.queue.DataOutputQueueTransferQueue;

import java.util.*;

import static org.boon.Boon.puts;

/**
 * Just for testing
 * Created by Richard on 7/2/14.
 */
public class DataStoreSimpleHttpClient implements DataStoreClient {

    private DataOutputQueue queue;
    private String url;
    private String clientId;
    private JsonSerializer serializer;

    private long messageId;

    private boolean verbose;


    private void send(DataStoreRequest request) {
        String message = request.formTextRequest();
        if (verbose) {
            puts("POST", url, ProtocolConstants.prettyPrintMessage(message));
            puts("POST", url);
            puts(ProtocolConstants.prettyPrintMessageWithLinesTabs(message));
        }
        String results = HTTP.post(url, message);
        handleMessageFromServer(results);
    }


    /**
     * RESPONSES FROM SERVER.
     *
     * @param textResponse
     */
    private void handleMessageFromServer(String textResponse) {

        if (verbose) {
            puts("RESPONSE", url, ProtocolConstants.prettyPrintMessage(textResponse));
            puts("RESPONSE", url);
            puts(ProtocolConstants.prettyPrintMessageWithLinesTabs(textResponse));
        }


        if (textResponse.startsWith(Action.GET.response().startsWith()) ||
                textResponse.startsWith(Action.SET_BROADCAST.response().startsWith())) {
            queue.put(SingleResult.fromTextMessage(textResponse));


        } else if (textResponse.startsWith(Action.BATCH_READ.response().startsWith())) {
            queue.put(BatchResult.fromTextMessage(textResponse));

        } else if (textResponse.startsWith(Action.GET_STATS.response().startsWith())) {
            queue.put(StatsResults.fromTextMessage(textResponse));

        } else {
            if (verbose) {
                puts(textResponse);
            }
        }

    }


    @Override
    public void get(String key) {
        GetRequest request = new GetRequest(Action.GET, messageId++, clientId, key);
        send(request);
    }


    @Override
    public void getFromMemory(String key) {
        GetRequest request = new GetRequest(Action.GET_MEM, messageId++, clientId, key);
        send(request);

    }

    @Override
    public void getFromLocalDB(String key) {
        GetRequest request = new GetRequest(Action.GET_LOCAL_DB, messageId++, clientId, key);
        send(request);

    }

    /**
     * public SetRequest(DataStoreSource source, Action action,
     * long id, String clientId, long version,
     * long  createTimeStamp, long updateTimeStamp,
     * String key, String payload) {
     *
     * @param source
     * @param key
     * @param value
     */
    @Override
    public void set(DataStoreSource source, String key, Object value) {
        SetRequest request = new SetRequest(source, Action.SET_SOURCE,
                messageId, clientId, 0, 0, 0, key, serializer.serialize(value).toString());
        send(request);

    }

    @Override
    public void get(DataStoreSource source, String key) {
        GetRequest request = new GetRequest(source, Action.GET_SOURCE, messageId++, clientId, key);
        send(request);
    }

    @Override
    public void set(String key, Object value) {
        SetRequest request = new SetRequest(messageId++, clientId, key, serializer.serialize(value).toString());
        send(request);
    }

    @Override
    public void setIfNotExists(String key, Object value) {
        SetRequest request = new SetRequest(Action.SET_IF_NOT_EXIST, messageId++, clientId, key, serializer.serialize(value).toString());
        send(request);

    }


    @Override
    public void broadcastSet(String key, Object value) {
        SetRequest request = new SetRequest(Action.SET_BROADCAST, messageId++, clientId, key, serializer.serialize(value).toString());
        send(request);

    }

    @Override
    public void setBatch(Map<String, Object> batch) {
        setBatch(null, batch);
    }

    @Override
    public void setBatch(DataStoreSource source, Map<String, Object> batch) {
        List<String> keys = Lists.list(batch.keySet());
        List<String> values = new ArrayList<>(keys.size());

        for (String key : keys) {
            values.add(serializer.serialize(batch.get(key)).toString());
        }

        BatchSetRequest request = new BatchSetRequest(source, messageId++, clientId, keys, values);
        send(request);
    }

    @Override
    public void setBatch(BatchSetRequest request) {
        send(request);
    }

    @Override
    public void remove(DataStoreSource source, String key) {

        RemoveRequest removeRequest = new RemoveRequest(source, Action.REMOVE_SOURCE, messageId++, clientId, key);
        send(removeRequest);

    }

    @Override
    public void remove(String key) {

        RemoveRequest removeRequest = new RemoveRequest(null, Action.REMOVE, messageId++, clientId, key);
        send(removeRequest);

    }

    @Override
    public void getStats() {

        StatsRequest request = new StatsRequest(null, Action.GET_STATS, messageId++, clientId);
        send(request);
    }

    @Override
    public void clearStats() {

        StatsRequest request = new StatsRequest(null, Action.CLEAR_STATS, messageId++, clientId);
        send(request);

    }

    @Override
    public void getStats(DataStoreSource source) {

        StatsRequest request = new StatsRequest(source, Action.GET_STATS, messageId++, clientId);
        send(request);

    }

    @Override
    public void clearStats(DataStoreSource source) {

        StatsRequest request = new StatsRequest(source, Action.CLEAR_STATS, messageId++, clientId);
        send(request);


    }

    @Override
    public void setBatchIfNotExists(Map<String, Object> batch) {
        setBatchIfNotExists(null, batch);
    }

    @Override
    public void setBatchIfNotExists(DataStoreSource source, Map<String, Object> batch) {
        List<String> keys = Lists.list(batch.keySet());
        List<String> values = new ArrayList<>(keys.size());

        for (String key : keys) {
            values.add(serializer.serialize(batch.get(key)).toString());
        }

        BatchSetRequest request = new BatchSetRequest(source, Action.SET_BATCH_IF_NOT_EXISTS, messageId, clientId, keys, values);
        send(request);
    }

    @Override
    public void batchLoad(Collection<String> keys) {

        ReadBatchRequest request = new ReadBatchRequest(messageId++, clientId, keys);
        send(request);
    }


    @Override
    public DataOutputQueue queue() {
        return queue;
    }

    @Override
    public DataStoreAdminClient admin(String shardId, String password) {
        return null;
    }


    public void init(DataOutputQueue queue, String clientId, String url, boolean verbose) {
        this.queue = queue;
        this.url = url;


        if (clientId == null) {
            this.clientId = UUID.randomUUID().toString();
        } else {
            this.clientId = clientId;
        }


        if (queue == null) {
            this.queue = new DataOutputQueueTransferQueue(1000);
        }
        JsonSerializerFactory factory = new JsonSerializerFactory();
        serializer = factory.create();
        this.verbose = verbose;
    }

    @Override
    public void flush() {

    }

    @Override
    public boolean connected(String key) {
        return true;
    }

}
