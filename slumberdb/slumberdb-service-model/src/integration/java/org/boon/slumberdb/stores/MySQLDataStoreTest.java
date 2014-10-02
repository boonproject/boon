package org.boon.slumberdb.stores;


import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.Response;
import org.boon.slumberdb.stores.mysql.MySQLDataStore;
import org.boon.slumberdb.stores.queue.DataOutputQueueTransferQueue;
import org.boon.slumberdb.stores.queue.MapOutputQueue;
import org.boon.core.Sys;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.boon.Exceptions.die;

/**
 * Created by Richard on 6/27/14.
 */
public class MySQLDataStoreTest {


    static MySQLDataStore ds;
    static Map<String, String> outQueueMap = Collections.synchronizedMap(new HashMap<String, String>());
    static Map<String, String> nextReaderMap = Collections.synchronizedMap(new HashMap<String, String>());
    MySQLDataStore dataStore;
    boolean ok;
    DataOutputQueue dataOutputQueue = new MapOutputQueue();

    {
        outQueueMap = MapOutputQueue.map;
    }

    DataStore nextReaderStore = new DataStore() {


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
        public void get(GetRequest getRequest) {

            nextReaderMap.put(getRequest.key(), getRequest.key());

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


    {

        if (ds == null) {
            dataStore = new MySQLDataStore();


            String url = "jdbc:mysql://localhost:3306/slumberdb";
            String user = "slumber";
            String password = "slumber1234";
            String table = "slumberdb_user";


            dataStore.init(
                    DataStoreConfig.config().dbUrl(url).dbUser(user).dbPassword(password).dbTable(table)
                    , dataOutputQueue, nextReaderStore);
            dataStore.start();
            ds = dataStore;
            Sys.sleep(1000);
        } else {
            dataStore = ds;
        }

    }


    @Before
    public synchronized void setup() {
        dataStore.set(new SetRequest(0, "", "Jason", "Jason"));
        dataStore.remove(new RemoveRequest(0, "", "Odd User"));
        Sys.sleep(1000);

    }


    @Test
    public synchronized void store() {
        dataStore.set(new SetRequest(0, "", "Rick", "Was Here"));
        Sys.sleep(100);
        dataStore.get(new GetRequest(0, "", "Rick"));
        Sys.sleep(1000);

        ok = outQueueMap.containsKey("Rick") || die(outQueueMap);

    }


    @Test
    public synchronized void getNotFound() {
        dataStore.get(new GetRequest(0, "", "Odd User"));
        Sys.sleep(1000);


        ok = outQueueMap.get("Odd User").equals("NULL") || die("We have output", outQueueMap.get("Odd User"));


        ok = !nextReaderMap.containsKey("Odd User") || die("Next reader should NOT have been called as MySQL is terminating");
    }


    @Test
    public synchronized void getFound() {
        dataStore.get(new GetRequest(0, "pe-instance1", "Jason"));
        Sys.sleep(1000);


        ok = outQueueMap.containsKey("Jason") || die(outQueueMap);
    }


    @Test
    public synchronized void batchSaveRead() {

        MySQLDataStore theDataStore = new MySQLDataStore();

        final DataOutputQueueTransferQueue queue = new DataOutputQueueTransferQueue(1000);


        String url = "jdbc:mysql://localhost:3306/slumberdb";
        String user = "slumber";
        String password = "slumberdb1234";
        String table = "slumberdb_user";


        theDataStore.init(
                DataStoreConfig.config().dbUrl(url).dbUser(user).dbPassword(password).dbTable(table),
                queue, nextReaderStore);
        theDataStore.start();


        Sys.sleep(1000);

        String id = "foo";
        List<String> list = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            list.add(id + index);
            theDataStore.set(new SetRequest(0, "", id + index, "value" + index));
        }

        Sys.sleep(1000);

        list.add("DUD" + System.currentTimeMillis());

        list.add("DUD2-" + System.currentTimeMillis());

        list.add("DUD3-" + System.currentTimeMillis());
        theDataStore.batchRead(new ReadBatchRequest(0, "test", list));

        Sys.sleep(1_000);

        Response item = queue.take();

        while (item != null) {

            item = queue.take();
        }


    }
}