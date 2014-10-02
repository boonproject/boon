package org.boon.slumberdb.service.client;

import org.boon.slumberdb.service.config.Bucket;
import org.boon.slumberdb.service.config.DataStoreClientConfig;
import org.boon.slumberdb.service.results.BatchResult;
import org.boon.slumberdb.service.results.Response;
import org.boon.slumberdb.service.results.SingleResult;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.slumberdb.stores.queue.DataOutputQueueTransferQueue;
import org.boon.Lists;
import org.boon.core.Sys;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Maps.map;

/**
 * Created by Richard on 8/19/14.
 */
public class DataStoreWebSocketInt {

    DataStoreClient client;
    boolean ok;

    @Before
    public void setup() {


        /* Create a new instance of Vertx. */
        Vertx vertx = VertxFactory.newVertx();

        /* CONFIG: Initialize the client provider. */
        DataStoreVertxWebSocketClientProvider provider = new DataStoreVertxWebSocketClientProvider();
        provider.registerSupplier(); //register the provider.
        DataStoreClientConfig config = DataStoreClientConfig.config();

        puts(config);
        /* CONFIG: Simple one bucket for hits. You have to run the data store client. */
        config.buckets(Bucket.bucket("localhost", 10100));


        provider.init(vertx, new DataOutputQueueTransferQueue(10), config, true, false);


        client = DataStoreFactory.createClient("TEST" + System.currentTimeMillis() + 100, null);
        Sys.sleep(1000);

    }

    @Test
    public void getTest() {

        puts("Going to do GET now");
        client.set("getTest", "value2");
        Sys.sleep(500);

        client.get("getTest");
        Sys.sleep(500);

        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"value2\"") || die(dataItem.getValue());

    }

    @Test
    public void shallowGet() {
        client.set("shallowGet", "value2");
        Sys.sleep(500);


        puts("Going to do GET now");

        client.getFromMemory("shallowGet");
        Sys.sleep(500);

        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"value2\"") || die(dataItem.getValue());

    }

    @Test
    public void levelDBGet() {
        client.set("levelDBGet", "value2");
        Sys.sleep(500);


        puts("Going to do GET now");

        client.getFromLocalDB("levelDBGet");
        Sys.sleep(500);

        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"value2\"") || die(dataItem.getValue());

    }

    @Test
    public void batchRead() {
        client.queue().take();

        client.set("foo.bar.1", 1);
        client.set("foo.bar.2", 2);
        client.set("foo.bar.3", 3);

        Sys.sleep(1000);

        client.queue().take();

        client.batchLoad(Lists.list("foo.bar.1", "foo.bar.2", "foo.bar.3"));

        BatchResult batchResult = (BatchResult) client.queue().take();

        puts(batchResult);

    }

    @Test
    public void batchSet() {

        client.queue().take();

        final Map<String, Object> map = map(
                "batch.foo.bar.1", (Object) 1,
                "batch.foo.bar.2", 2,
                "batch.foo.bar.3", 3);

        client.setBatch(map);

        Sys.sleep(500);

        client.batchLoad(Lists.list("batch.foo.bar.1", "batch.foo.bar.2", "batch.foo.bar.3"));


        Sys.sleep(500);

        BatchResult batchResult = (BatchResult) client.queue().take();

        ok |= map.size() == batchResult.getResults().size() || die(batchResult.getResults().size(), batchResult.getResults());


        ok |= batchResult.getResults().get("batch.foo.bar.1").equals("1")
                || die(batchResult.getResults().size(), batchResult.getResults());

        ok |= batchResult.getResults().get("batch.foo.bar.2").equals("2")
                || die(batchResult.getResults().size(), batchResult.getResults());


        ok |= batchResult.getResults().get("batch.foo.bar.3").equals("3")
                || die(batchResult.getResults().size(), batchResult.getResults());


    }


    @Test
    public void basicTest() {


        client.queue().take();

        client.set("basicTest", "mom");

        Sys.sleep(500);

        client.get("basicTest");


        Sys.sleep(500);


        SingleResult dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());

    }


    @Test
    public void basicReadFromMySQL() {

        String mysqlKey = "mySQL" + System.currentTimeMillis();

        client.set(DataStoreSource.REMOTE_DB, mysqlKey, "mom");

        Sys.sleep(2_000);

        client.get(mysqlKey);


        Sys.sleep(2_000);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.REMOTE_DB || die(dataItem.source());

        client.get(mysqlKey);

        Sys.sleep(1_000);

        dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.MEMORY || die(dataItem.source());

    }


    @Test
    public void basicReadFromLevelDB() {

        client.queue().take(); //clear

        String key = "LEVEL_DB_READ_TEST-" + System.currentTimeMillis();

        client.set(DataStoreSource.LOCAL_DB, key, "mom");

        Sys.sleep(2_000);

        client.get(DataStoreSource.LOCAL_DB, key);


        Sys.sleep(2_000);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.LOCAL_DB || die(dataItem.source());

        client.get(key);

        Sys.sleep(1_000);

        dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.MEMORY || die(dataItem.source());

    }


    @Test
    public void basicReadWriteFromLevelDB() {

        client.queue().take();

        String key = "LEVEL_DB_READ_TEST-" + System.currentTimeMillis();

        client.set(DataStoreSource.LOCAL_DB, key, "mom");

        Sys.sleep(2_000);

        client.get(DataStoreSource.LOCAL_DB, key);


        Sys.sleep(2_000);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.LOCAL_DB || die(dataItem.source());

        client.get(key);

        Sys.sleep(1_000);

        dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.MEMORY || die(dataItem.source());

    }


    @Test
    public void basicReadWriteFromMySQL() {

        client.queue().take();

        String key = "LEVEL_DB_READ_TEST-" + System.currentTimeMillis();

        client.set(DataStoreSource.REMOTE_DB, key, "mom");

        Sys.sleep(2_000);

        client.get(DataStoreSource.REMOTE_DB, key);


        Sys.sleep(2_000);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.REMOTE_DB || die(dataItem.source());

        client.get(key);

        Sys.sleep(1_000);

        dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.MEMORY || die(dataItem.source());

    }

    @Test
    public void basicSetIntoMemory2() {

        client.queue().take(); //clear

        String key = "MEMORY_SET_TEST-" + System.currentTimeMillis();

        client.set(DataStoreSource.MEMORY, key, "mom");

        Sys.sleep(200);

        client.get(key);


        Sys.sleep(200);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.MEMORY || die(dataItem.source());


    }


    @Test
    public void basicSetIntoMemory() {

        client.queue().take(); //clear

        String key = "MEMORY_SET_TEST-" + System.currentTimeMillis();

        client.set(DataStoreSource.MEMORY, key, "mom");

        Sys.sleep(200);

        client.get(key);


        Sys.sleep(200);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.MEMORY || die(dataItem.source());


    }


    @Test
    public void makeSureLevelDBIsWrittenToAfterFallbackReadToMySQL() {

        client.queue().take(); //clear


        String key = "MYSQL_FALLBACK_POPULATE_LEVELDB_TEST" + System.currentTimeMillis();

        client.set(DataStoreSource.REMOTE_DB, key, "fallback");


        Sys.sleep(2_000);

        client.get(key);

        client.queue().take(); //clear call that gets from DB


        Sys.sleep(2_000);

        client.get(DataStoreSource.LOCAL_DB, key);


        Sys.sleep(7_000);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"fallback\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.LOCAL_DB || die(dataItem.source());


    }


    @Test
    public void batchGetPopulatesLevelDB() {


        String key1 = "batchGetPopulatesLevelDB-1-" + System.currentTimeMillis();
        String key2 = "batchGetPopulatesLevelDB-2-" + System.currentTimeMillis();
        String key3 = "batchGetPopulatesLevelDB-3-" + System.currentTimeMillis();

        String key4 = "batchGetPopulatesLevelDB-3-" + System.currentTimeMillis();

        List<String> keys = Lists.list(key1, key2, key3, key4);


        client.set(DataStoreSource.REMOTE_DB, key1, "1");
        client.set(DataStoreSource.REMOTE_DB, key2, "2");
        client.set(DataStoreSource.REMOTE_DB, key3, "3");
        client.set(DataStoreSource.REMOTE_DB, key4, "4");

        Sys.sleep(2_000);

        client.batchLoad(keys);
        Sys.sleep(2_000);

        Response take = client.queue().take();

        Sys.sleep(1_000);


        ok = take.source() == DataStoreSource.REMOTE_DB || die();

        puts(take);


        client.get(DataStoreSource.LOCAL_DB, key1);


        take = client.queue().take();

        Sys.sleep(1000);
        puts(take);

        ok = take.source() == DataStoreSource.LOCAL_DB || die(take);

    }


    @Test
    public void basicTest2() {


        client.queue().take();

        client.set("basicTest2", "mom");

        Sys.sleep(10);

        client.get("basicTest2");


        Sys.sleep(10);


        SingleResult dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());

        client.remove("basicTest2");

        Sys.sleep(5000);


        client.get("basicTest2");

        Sys.sleep(5000);

        dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("NULL") || die(dataItem.getValue());


    }


    @Test
    public void broadCastSet() {

        client.broadcastSet("broadCastSet", "broadCastSet");
        Sys.sleep(500);


        puts("Going to do broadCastSet now");

        client.getFromMemory("broadCastSet");
        Sys.sleep(500);

        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"broadCastSet\"") || die(dataItem.getValue());


    }

    @Test
    public void basicTest3() {


        client.queue().take();

        client.set("basicTest3", "mom");

        Sys.sleep(10);

        client.get("basicTest3");


        Sys.sleep(10);


        SingleResult dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());

        client.remove(DataStoreSource.MEMORY, "basicTest3");

        Sys.sleep(500);


        client.get("basicTest3");

        dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());

        ok = dataItem.source().equals(DataStoreSource.LOCAL_DB) || die(dataItem.source());


        client.remove(DataStoreSource.MEMORY, "basicTest3");

        client.remove(DataStoreSource.LOCAL_DB, "basicTest3");

        Sys.sleep(5_000);


        client.get("basicTest3");

        dataItem = (SingleResult) client.queue().take();

        Sys.sleep(5_000);

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());

        ok = dataItem.source().equals(DataStoreSource.REMOTE_DB) || die(dataItem.source());

    }


}
