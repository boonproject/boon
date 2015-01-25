package org.boon.slumberdb.service.client;

import org.boon.Logger;
import org.boon.Str;
import org.boon.core.Sys;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.service.config.Bucket;
import org.boon.slumberdb.service.config.DataStoreClientConfig;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStoreSource;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Boon.configurableLogger;
import static org.boon.Boon.puts;


/**
 * Created by Richard on 7/1/14.
 */
public class DataStoreVertxWebSocketClient implements DataStoreClient {

    private final Vertx vertx;

    private final DataStoreClientConfig config;

    private final boolean debug = GlobalConfig.DEBUG;

    private final Logger logger = configurableLogger(this.getClass());

    private final boolean verbose;

    private final ConcurrentHashMap<Bucket, ServerProxy> serverProxyMap = new ConcurrentHashMap<>();
    private final String clientId;
    private final boolean inVertx;
    private DataOutputQueue queue;
    private JsonSerializer serializer;

    private String uri;


    DataStoreVertxWebSocketClient(Vertx vertx, final DataStoreClientConfig config,
                                  DataOutputQueue queue, boolean verbose,
                                  String context, boolean inVertx) {
        this.vertx = vertx;
        this.queue = queue;
        this.verbose = verbose;

        this.inVertx = inVertx;


        this.clientId = context == null ? Str.add(config.clientId()) : Str.add(config.clientId(), ".", context);

        if (config == null) {

            this.config = DataStoreClientConfig.load();
        } else {
            this.config = config;

        }

        uri = this.config.websocketURI();
        JsonSerializerFactory factory = new JsonSerializerFactory();
        serializer = factory.create();

        for (Bucket bucket : config.buckets()) {
            serverProxy(bucket);
        }

        vertx.setPeriodic(10_000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {


                for (Bucket bucket : config.buckets()) {
                    if (!serverProxy(bucket).connected()) {
                        serverProxy(bucket).connect();
                        Sys.sleep(1000);
                        serverProxy(bucket).flush();

                    }
                }

            }
        });

    }


    private ServerProxy serverProxy(Bucket bucket) {
        ServerProxy serverProxy;

        synchronized (bucket) {

            serverProxy = serverProxyMap.get(bucket);
            if (serverProxy == null) {

                serverProxy = new ServerProxy(bucket, vertx, verbose, uri, queue, inVertx, clientId, config.maxFrameSize());
                serverProxyMap.put(bucket, serverProxy);
                serverProxy.connect();
            }
        }
        return serverProxy;
    }

    @Override
    public boolean connected(final String key) {
        boolean connected = serverProxy(pickBucket(key)).connected();

        serverProxy(pickBucket(key)).connect();
        return connected;
    }

    public void flush() {

        for (Bucket bucket : config.buckets()) {
            if (serverProxy(bucket).connected()) {
                serverProxy(bucket).flush();
            }
        }

    }

    @Override
    public void get(String key) {

        if (!get(pickBucket(key), key)) {
            connected(key);
        }

    }


    @Override
    public void getFromMemory(String key) {

        if (!getFromMemory(pickBucket(key), key)) {
            connected(key);
        }

    }


    @Override
    public void getFromLocalDB(String key) {

        if (!getFromFile(pickBucket(key), key)) {
            connected(key);
        }

    }


    public boolean getFromFile(final Bucket bucket, String key) {
        if (debug) logger.info("getFromMemory()", key, bucket);
        if (verbose) puts("getFromMemory()", key, bucket);

        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.getFromFile(clientId, key);

    }

    public boolean getFromMemory(final Bucket bucket, String key) {
        if (debug) logger.info("getFromMemory()", key, bucket);
        if (verbose) puts("getFromMemory()", key, bucket);

        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.getFromMemory(clientId, key);

    }


    @Override
    public void setBatch(Map<String, Object> batch) {
        setBatch(null, batch);
    }

    @Override
    public void setBatch(DataStoreSource source, Map<String, Object> batch) {
        Map<Bucket, List<Entry<String, String>>> map = buildBucketListMap(batch);
        for (Map.Entry<Bucket, List<Entry<String, String>>> entry : map.entrySet()) {
            serverProxy(entry.getKey()).batchSet(source, clientId, entry.getValue());
        }
    }

    @Override
    public void setBatchIfNotExists(Map<String, Object> batch) {
        setBatchIfNotExists(null, batch);
    }

    @Override
    public void setBatchIfNotExists(DataStoreSource source, Map<String, Object> batch) {
        Map<Bucket, List<Entry<String, String>>> map = buildBucketListMap(batch);
        for (Map.Entry<Bucket, List<Entry<String, String>>> entry : map.entrySet()) {
            serverProxy(entry.getKey()).batchSetIfNotExists(source, clientId, entry.getValue());
        }
    }

    protected Map<Bucket, List<Entry<String, String>>> buildBucketListMap(Map<String, Object> batch) {
        Map<Bucket, List<Entry<String, String>>> map = new LinkedHashMap<>();
        Set<String> keys = batch.keySet();
        for (String key : keys) {
            Bucket bucket = config.pickBucket(key);
            List<Entry<String, String>> bucketItems = map.get(bucket);
            if (bucketItems == null) {
                bucketItems = new ArrayList<>();
                map.put(bucket, bucketItems);
            }
            bucketItems.add(new Entry<>(key, serializer.serialize(batch.get(key)).toString()));
        }
        return map;
    }

    @Override
    public void batchLoad(final Collection<String> keys) {

        Map<Bucket, List<String>> map = new HashMap<>();
        Bucket bucket;
        List<String> bucketKeys;

        for (String key : keys) {

            bucket = config.pickBucket(key);
            bucketKeys = map.get(bucket);

            if (bucketKeys == null) {
                bucketKeys = new ArrayList<>();
                map.put(bucket, bucketKeys);
            }
            bucketKeys.add(key);
        }


        for (Map.Entry<Bucket, List<String>> entry : map.entrySet()) {

            serverProxy(entry.getKey()).batchGet(clientId, entry.getValue());

        }
    }


    private boolean remove(Bucket bucket, DataStoreSource source, String key) {

        if (verbose) {
            puts("Sending payload to server", bucket, key);
        }

        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.remove(clientId, source, key);
    }


    private boolean getStats(Bucket bucket, DataStoreSource source) {


        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.getStats(clientId, source);
    }


    private boolean clearStats(Bucket bucket, DataStoreSource source) {


        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.clearStats(clientId, source);
    }

    private boolean get(final Bucket bucket, final String key) {

        if (verbose) {
            puts("Sending payload to server", bucket, key);
        }

        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.get(clientId, key);
    }

    private boolean get(final Bucket bucket, DataStoreSource source, final String key) {

        if (verbose) {
            puts("Sending payload to server", source, bucket, key);
        }

        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.get(clientId, source, key);
    }


    private boolean set(final Bucket bucket, DataStoreSource source, final String key, final String value) {

        if (verbose) {
            puts("Sending payload to server", source, bucket, value);
        }

        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.set(source, clientId, key, value);
    }

    private boolean set(final Bucket bucket, final String key, final String value) {

        if (verbose) {
            puts("Sending payload to server", bucket, value);
        }

        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.set(clientId, key, value);
    }

    private boolean setIfNotExists(final Bucket bucket, final String key, final String value) {

        if (verbose) {
            puts("Sending payload to server set if not exists", bucket, value);
        }

        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.setIfNotExists(clientId, key, value);
    }

    private boolean broadcastSet(final Bucket bucket, final String key, final String value) {

        if (verbose) {
            puts("Sending broadcast payload to server", bucket, value);
        }

        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.broadcastSet(clientId, key, value);
    }

    private Bucket pickBucket(String key) {
        return config.pickBucket(key);
    }


    @Override
    public void set(DataStoreSource source, String key, Object value) {

        String svalue = serializer.serialize(value).toString();

        if (!set(pickBucket(key), source, key, svalue)) {
            connected(key);
        }


    }

    @Override
    public void get(DataStoreSource source, String key) {


        if (!get(pickBucket(key), source, key)) {
            connected(key);
        }

    }

    @Override
    public void remove(DataStoreSource source, String key) {


        if (!remove(pickBucket(key), source, key)) {
            connected(key);
        }
    }


    @Override
    public void remove(String key) {

        if (!remove(pickBucket(key), DataStoreSource.ALL, key)) {
            connected(key);
        }
    }

    @Override
    public void getStats() {

    }

    @Override
    public void clearStats() {

    }

    @Override
    public void getStats(DataStoreSource source) {

    }

    @Override
    public void clearStats(DataStoreSource source) {

    }


    @Override
    public void set(String key, Object value) {

        String svalue = serializer.serialize(value).toString();

        if (!set(pickBucket(key), key, svalue)) {
            connected(key);
        }

    }


    @Override
    public void setIfNotExists(String key, Object value) {
        String svalue = serializer.serialize(value).toString();

        if (!setIfNotExists(pickBucket(key), key, svalue)) {
            connected(key);
        }

    }


    @Override
    public void broadcastSet(String key, Object value) {

        String svalue = serializer.serialize(value).toString();

        if (!broadcastSet(pickBucket(key), key, svalue)) {
            connected(key);
        }

    }


    @Override
    public DataOutputQueue queue() {
        return queue;
    }

    @Override
    public DataStoreAdminClient admin(String shardId, String password) {
        return new DataStoreAdminClient() {
            @Override
            public void turnOnRequestLogging() {
                doTurnOnRequestLogging();
            }

            @Override
            public void turnOnMetricsTracking() {
                doTurnOnMetricsTracking();

            }

            @Override
            public void turnOnSendLogsToClient() {
                doTurnOnSendLogsToClient();

            }
        };
    }


    private void doTurnOnSendLogsToClient() {


        for (Bucket bucket : config.buckets()) {

            serverProxy(bucket).admin().turnOnRequestLogging();
        }

    }

    private void doTurnOnMetricsTracking() {

        for (Bucket bucket : config.buckets()) {

            serverProxy(bucket).admin().turnOnRequestLogging();
        }

    }

    public void connectAll() {
        final List<Bucket> buckets = config.buckets();
        for (Bucket bucket : buckets) {
            serverProxy(bucket).connect();
        }

    }


    private void doTurnOnRequestLogging() {

        for (Bucket bucket : config.buckets()) {

            serverProxy(bucket).admin().turnOnRequestLogging();
        }

    }


    private boolean turnOnRequestLogging(final Bucket bucket, String key) {
        if (debug) logger.info("getFromMemory()", key, bucket);
        if (verbose) puts("getFromMemory()", key, bucket);

        final ServerProxy serverProxy = serverProxy(bucket);
        return serverProxy.getFromFile(clientId, key);

    }

}
