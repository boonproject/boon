package org.boon.slumberdb.service.client;

import org.boon.slumberdb.service.config.DataStoreClientConfig;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.vertx.java.core.Vertx;

/**
 * Created by Richard on 7/2/14.
 */
public class DataStoreVertxWebSocketClientProvider implements DataStoreClientProvider {

    private Vertx vertx;
    private DataOutputQueue queue;
    private DataStoreClientConfig config;

    private boolean inVertx;
    private boolean verbose;

    public void init(Vertx vertx, DataOutputQueue queue, DataStoreClientConfig config, boolean verbose, boolean inVertx) {
        this.vertx = vertx;
        this.queue = queue;
        this.config = config;
        this.verbose = verbose;
        this.inVertx = inVertx;
    }


    public void registerSupplier() {
        DataStoreFactory.registerClientSupplier(this);
    }

    @Override
    public DataStoreClient get(String context) {


        DataStoreVertxWebSocketClient client = new DataStoreVertxWebSocketClient(vertx, config, queue, verbose, context, inVertx);
        client.connectAll();
        return client;
    }

    @Override
    public DataStoreClient get() {
        return get(null);
    }


}
