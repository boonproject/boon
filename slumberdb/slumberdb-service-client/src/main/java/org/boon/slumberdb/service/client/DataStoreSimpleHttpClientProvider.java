package org.boon.slumberdb.service.client;


import org.boon.slumberdb.stores.DataOutputQueue;

/**
 * Created by Richard on 7/2/14.
 */
public class DataStoreSimpleHttpClientProvider implements DataStoreClientProvider {

    String url;
    String clientId;

    DataOutputQueue queue;
    boolean debug;

    public void init(String url, String clientId, DataOutputQueue queue, boolean debug) {
        this.url = url;
        this.queue = queue;
        this.debug = debug;
        this.clientId = clientId;
    }

    @Override
    public DataStoreClient get(String context) {
        return get();
    }

    @Override
    public DataStoreClient get() {
        DataStoreSimpleHttpClient client = new DataStoreSimpleHttpClient();
        client.init(queue, clientId, url, debug);
        return client;
    }


    public void registerSupplier() {
        DataStoreFactory.registerClientSupplier(this);
    }

}
