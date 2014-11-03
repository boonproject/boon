package org.boon.slumberdb.utility;

import org.boon.core.Sys;
import org.boon.slumberdb.service.client.DataStoreClient;
import org.boon.slumberdb.service.client.DataStoreFactory;
import org.boon.slumberdb.service.client.DataStoreVertxWebSocketClientProvider;
import org.boon.slumberdb.service.config.Bucket;
import org.boon.slumberdb.service.config.DataStoreClientConfig;
import org.boon.slumberdb.stores.queue.DataOutputQueueTransferQueue;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

import static org.boon.Boon.println;

/**
 * Created by Scott on 10/21/14
 */
public class ClientHelper {

    public static DataStoreClient getSimpleClient(String contextName, Bucket... buckets) {

        /* Create a new instance of Vertx. */
        Vertx vertx = VertxFactory.newVertx();

        /* CONFIG: Initialize the client provider. */
        DataStoreVertxWebSocketClientProvider provider = new DataStoreVertxWebSocketClientProvider();
        provider.registerSupplier(); //register the provider.

        DataStoreClientConfig config;
        if (buckets == null || buckets.length == 0) {
            config = DataStoreClientConfig.load();
        }
        else {
            config = DataStoreClientConfig.config();
            config.buckets(buckets);
        }
        println(config);

        provider.init(vertx, new DataOutputQueueTransferQueue(10), config, false, false);

        DataStoreClient client = DataStoreFactory.createClient(contextName, null);
        Sys.sleep(1000);
        return client;
    }
}
