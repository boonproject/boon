package org.boon.slumberdb.utility;

import org.boon.core.Sys;
import org.boon.slumberdb.service.client.DataStoreClient;
import org.boon.slumberdb.service.client.DataStoreFactory;
import org.boon.slumberdb.service.client.DataStoreSimpleHttpClientProvider;
import org.boon.slumberdb.service.client.DataStoreVertxWebSocketClientProvider;
import org.boon.slumberdb.service.config.Bucket;
import org.boon.slumberdb.service.config.DataStoreClientConfig;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.queue.DataOutputQueueTransferQueue;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

import static org.boon.Boon.println;

/**
 * Created by Scott on 10/21/14
 */
public class ClientHelper {
    public static DataStoreClient getVertxWebSocketClient(String contextName, Bucket... buckets) {
        return getVertxWebSocketClient(contextName, true, buckets);
    }

    public static DataStoreClient getVertxWebSocketClient(String contextName, boolean verbose, Bucket... buckets) {
        return getVertxWebSocketClient(contextName, getDataStoreClientConfig(buckets), verbose);
    }

    public static DataStoreClient getVertxWebSocketClient(String contextName, DataStoreClientConfig dssc) {
        return getVertxWebSocketClient(contextName, dssc, true);
    }

    public static DataStoreClient getVertxWebSocketClient(String contextName, DataStoreClientConfig dssc, boolean verbose) {

        /* Create a new instance of Vertx. */
        Vertx vertx = VertxFactory.newVertx();

        /* CONFIG: Initialize the client provider. */
        DataStoreVertxWebSocketClientProvider provider = new DataStoreVertxWebSocketClientProvider();
        provider.registerSupplier(); //register the provider.
        println(dssc);

        provider.init(vertx, new DataOutputQueueTransferQueue(10), dssc, verbose, false);

        DataStoreClient client = DataStoreFactory.createClient(contextName, null);
        Sys.sleep(1000);
        return client;
    }

    public static DataStoreClient getSimpleHttpClient() {
        return getSimpleHttpClient(ProtocolConstants.DEFAULT_REST_URI, getDataStoreClientConfig(null), true);
    }

    public static DataStoreClient getSimpleHttpClient(boolean verbose) {
        return getSimpleHttpClient(ProtocolConstants.DEFAULT_REST_URI, getDataStoreClientConfig(null), verbose);
    }

    public static DataStoreClient getSimpleHttpClient(String clientId, Bucket... buckets) {
        return getSimpleHttpClient(clientId, true, buckets);
    }

    public static DataStoreClient getSimpleHttpClient(String clientId, boolean verbose, Bucket... buckets) {
        DataStoreClientConfig dssc = getDataStoreClientConfig(buckets);
        dssc.clientId(clientId);
        return getSimpleHttpClient(ProtocolConstants.DEFAULT_REST_URI, dssc, verbose);
    }

    public static DataStoreClient getSimpleHttpClient(String uri, DataStoreClientConfig dssc) {
        return getSimpleHttpClient(uri, dssc, true);
    }

    public static DataStoreClient getSimpleHttpClient(String uri, DataStoreClientConfig dssc, boolean verbose) {
        DataStoreSimpleHttpClientProvider provider = new DataStoreSimpleHttpClientProvider();
        Bucket bucket = dssc.buckets().get(0);
        provider.init("http://" + bucket.server() + ":" + bucket.serverPort() + uri, dssc.clientId(), new DataOutputQueueTransferQueue(10), verbose);
        provider.registerSupplier();
        return provider.get();
    }

    protected static DataStoreClientConfig getDataStoreClientConfig(Bucket[] buckets) {
        DataStoreClientConfig dssc;
        if (buckets == null || buckets.length == 0) {
            dssc = DataStoreClientConfig.load();
        }
        else {
            dssc = DataStoreClientConfig.config();
            dssc.buckets(buckets);
        }
        return dssc;
    }
}
