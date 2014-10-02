package org.boon.slumberdb.service.store;

import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.Response;
import org.boon.slumberdb.stores.ConcurrentMapDataStore;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStore;
import org.boon.core.Sys;
import org.junit.Before;
import org.junit.Test;

import static org.boon.Exceptions.die;

/**
 * Created by Richard on 6/27/14.
 */
public class ConcurrentMapDataStoreTest {

    ConcurrentMapDataStore dataStore;

    boolean dataOutputQueueCalled;
    DataOutputQueue dataOutputQueue = new DataOutputQueue() {

        @Override
        public void put(Response result) {
            dataOutputQueueCalled = true;
        }

        @Override
        public Response poll() {
            return null;
        }

        @Override
        public Response take() {
            return null;
        }

    };
    boolean nextReaderDataStoreGetCalled;
    DataStore nextReaderDataStore = new DataStore() {
        @Override
        public void clearStats() {

        }

        @Override
        public void sendStats(long now) {

        }

        @Override
        public void set(SetRequest setRequest) {

        }


        @Override
        public void get(GetRequest request) {
            nextReaderDataStoreGetCalled = true;
        }

        @Override
        public void search(SearchRequest searchRequest) {

        }

        @Override
        public void batchRead(ReadBatchRequest request) {

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
    };
    boolean ok;

    @Before
    public void setup() {
        dataStore = new ConcurrentMapDataStore();
        dataStore.init(DataStoreConfig.config(), dataOutputQueue, nextReaderDataStore);
        dataStore.set(new SetRequest(0, "clientId", "Jason", "Jason"));
        Sys.sleep(100);
        dataOutputQueueCalled = false;
        nextReaderDataStoreGetCalled = false;
    }


    @Test
    public void store() {
        dataStore.set(new SetRequest(0, "clientId", "Rick", "Was Here"));
    }


    @Test
    public void getNotFound() {
        dataStore.get(new GetRequest(0, "clientId", "Rick999"));
        ok = nextReaderDataStoreGetCalled || die("Next reader should have been called");
        ok = !dataOutputQueueCalled || die("Output outputDataQueue should not be called");
    }


    @Test
    public void getFound() {
        dataStore.get(new GetRequest(0, "clientId", "Jason"));
        ok = !nextReaderDataStoreGetCalled || die("Next reader should *NOT* have been called");
        ok = dataOutputQueueCalled || die("Data output outputDataQueue should have been called");
    }

}
