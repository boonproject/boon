package org.boon.slumberdb.stores;

import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.Response;
import org.boon.slumberdb.service.results.SearchBatchResult;
import org.boon.slumberdb.service.search.BaseSearchHandler;
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
public class LevelDBDataStoreTest {


    static LevelDBDataStore ds;
    static Map<String, String> outQueueMap = Collections.synchronizedMap(new HashMap<String, String>());
    static Map<String, String> nextReaderMap = Collections.synchronizedMap(new HashMap<String, String>());
    static Map<String, String> nextWriterMap = Collections.synchronizedMap(new HashMap<String, String>());
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
        public void get(GetRequest getRequest) {
            nextReaderMap.put(getRequest.key(), "");
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
    LevelDBDataStore dataStore;
    boolean ok;
    DataOutputQueue dataOutputQueue = new MapOutputQueue();

    {
        outQueueMap = MapOutputQueue.map;
    }


    {

        if (ds == null) {
            dataStore = new LevelDBDataStore();
            dataStore.init(DataStoreConfig.config().levelDBFileName("leveldbtest" + System.currentTimeMillis()),
                    dataOutputQueue, nextReaderDataStore);
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
        Sys.sleep(50);
        dataStore.get(new GetRequest(0, "", "Rick"));
        Sys.sleep(50);

        ok = outQueueMap.containsKey("Rick") || die(outQueueMap);


    }


    @Test
    public synchronized void getNotFound() {
        dataStore.get(new GetRequest(0, "pe-instance1", "Odd User"));
        Sys.sleep(500);

        ok = !outQueueMap.containsKey("Odd User") || die("We have output");


        ok = nextReaderMap.containsKey("Odd User") || die("Next reader should have been called");


    }


    @Test
    public synchronized void getFound() {
        dataStore.get(new GetRequest(0, "pe-instance1", "Jason"));
        Sys.sleep(50);


        ok = outQueueMap.containsKey("Jason") || die(outQueueMap);


    }


    @Test
    public synchronized void batchSaveRead() {
        final DataOutputQueueTransferQueue queue = new DataOutputQueueTransferQueue(1000);


        LevelDBDataStore theDataStore = new LevelDBDataStore();

        final DataStoreConfig config = DataStoreConfig.config();

        final String outputDirectory = config.outputDirectory();

        if (outputDirectory.endsWith("/")) {
            config.outputDirectory(outputDirectory + "levelDBTEST/");
        } else {

            config.outputDirectory(outputDirectory + "/levelDBTEST/");
        }
        theDataStore.init(config, queue, nextReaderDataStore);
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

    @Test
    public synchronized void searchUser() {
        final DataOutputQueueTransferQueue queue = new DataOutputQueueTransferQueue(1000);
        Map<String, String> request = new HashMap<>();
        request.put("criteria", ".*foo.*");
        request.put("offset", "2");
        request.put("limit", "2");
        request.put("action", "search");
        request.put("clientId", "TEST");
        request.put("messageId", "0");
        request.put("handler", "BaseSearchHandler");

        LevelDBDataStore theDataStore = new LevelDBDataStore();
        theDataStore.init(DataStoreConfig.config(), queue, nextReaderDataStore);
        theDataStore.start();
        Sys.sleep(1000);

        String id = "foo";
        List<String> list = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            list.add(id + index);
            theDataStore.set(new SetRequest(0, "", id + index, "value" + index));
        }

        Sys.sleep(1000);
        theDataStore.search(SearchRequest.parse(Action.SEARCH, request));
        Sys.sleep(1_000);

        SearchBatchResult result = (SearchBatchResult) queue.take();

        ok = result.getResults().size() == 3 || die(result);

        final Set<Map.Entry<String, String>> entries = result.getResults().entrySet();

        for (Map.Entry<String, String> entry : entries) {
            if (entry.getKey().equals(BaseSearchHandler.TOTAL_RESULTS_KEY)) {
                ok = Integer.parseInt(entry.getValue()) == 10 || die(result);
            } else {
                ok = (entry.getKey().equals("foo0") || entry.getKey().equals("foo1")) || die(result);
            }
        }
    }


}
