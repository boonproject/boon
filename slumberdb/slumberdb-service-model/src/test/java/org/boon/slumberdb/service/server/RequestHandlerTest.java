package org.boon.slumberdb.service.server;

import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.boon.slumberdb.service.protocol.requests.MethodCall;
import org.boon.slumberdb.stores.MasterDataStore;
import org.boon.slumberdb.stores.queue.MapOutputQueue;
import org.boon.core.Sys;
import org.junit.Test;

/**
 * Created by Richard on 9/15/14.
 */
public class RequestHandlerTest {

    @Test
    public void test() {
        RequestHandler handler = new RequestHandler();

        MethodCall call = new MethodCall("me", 1L, "admin", "methodList", true, "\"dataStore\"");
        MethodCall call2 = new MethodCall("me", 1L, "admin", "methodList", true, "\"server\"");
        MethodCall call3 = new MethodCall("me", 1L, "admin", "methodList", true, "\"admin\"");

        //MethodCall call = new MethodCall("me", 1L, "dataStore", "countLocalDB", true);

        DataStoreServerConfig config = new DataStoreServerConfig();
        MockDataStoreServer mockDataStoreServer = new MockDataStoreServer();
        MapOutputQueue mapOutputQueue = new MapOutputQueue();
        MasterDataStore dataStore = new MasterDataStore();

        /**
         *
         public void init(DataStoreServer dataStoreServer,
         DataStoreServerConfig config,
         MasterDataStore masterDataStore,
         DataOutputQueueTransferQueue queue) {
         */

        handler.init(mockDataStoreServer, config, dataStore, mapOutputQueue);

        handler.handleCallFromClient(call.formTextRequest(), new Object());


        handler.handleCallFromClient(call2.formTextRequest(), new Object());


        handler.handleCallFromClient(call3.formTextRequest(), new Object());

        Sys.sleep(1000);
    }
}
