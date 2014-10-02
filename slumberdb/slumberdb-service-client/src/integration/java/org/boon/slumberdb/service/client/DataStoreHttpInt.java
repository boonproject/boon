package org.boon.slumberdb.service.client;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.results.*;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.slumberdb.stores.queue.DataOutputQueueTransferQueue;
import org.boon.Lists;
import org.boon.Str;
import org.boon.core.Sys;
import org.boon.datarepo.Repo;
import org.boon.datarepo.Repos;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Maps.map;
import static org.boon.criteria.ObjectFilter.eq;

/**
 * Created by Richard on 9/5/14.
 */
public class DataStoreHttpInt {

    DataStoreClient client;
    boolean ok;

    @Before
    public void setup() {


        DataStoreSimpleHttpClientProvider provider = new DataStoreSimpleHttpClientProvider();
        provider.registerSupplier(); //register the provider.

        provider.init("http://localhost:10100" + ProtocolConstants.DEFAULT_REST_URI,
                "httpTest", new DataOutputQueueTransferQueue(10), true);



        /* Create the client. */
        client = DataStoreFactory.createClient("TEST" + System.currentTimeMillis(), null);
        client.set("knownkey", "value");
        Sys.sleep(1000);
        client.queue().take(); //clear it
        Sys.sleep(1000);

        client = DataStoreFactory.createClient("TEST" + System.currentTimeMillis() + 100, null);
        Sys.sleep(100);

    }

    @Test
    public void getTest() {

        puts("Going to do GET now");

        client.get("knownkey");
        Sys.sleep(10);

        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"value\"") || die(dataItem.getValue());

    }

    @Test
    public void shallowGet() {


        puts("Going to do GET now");

        client.getFromMemory("knownkey");
        Sys.sleep(10);

        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"value\"") || die(dataItem.getValue());

    }

    @Test
    public void doesNotExists() {

        client.get("DOES NOT EXISTS" + System.currentTimeMillis());


        SingleResult dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("NULL") || die();
    }


    @Test
    public void levelDBGet() {


        puts("Going to do GET now");

        client.getFromLocalDB("knownkey");
        Sys.sleep(10);

        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"value\"") || die(dataItem.getValue());

    }


    @Test
    public void getStats() {


        puts("Going to do GET now");

        client.getStats();
        Sys.sleep(10);

        final Response take = client.queue().take();

        ok |= take instanceof StatsResults || die(take);


        StatsResults results = (StatsResults) take;


        final Repo<StatKey, StatCount> repo = Repos.builder().primaryKey("key")
                .lookupIndex("count")
                .lookupIndex("action")
                .lookupIndex("source")
                .searchIndex("count")
                .build(StatKey.class, StatCount.class);

        repo.addAll(results.getResults().values());

        List<StatCount> actionData = repo.query(eq("action", Action.SET));

        for (StatCount count : actionData) {
            puts(Str.rpad(count.source()), Str.rpad(count.action()), Str.rpad(count.area()), Str.rpad(count.count()));

        }


        actionData = repo.query(eq("action", Action.GET));

        for (StatCount count : actionData) {
            puts(Str.rpad(count.source()), Str.rpad(count.action()), Str.rpad(count.area()), Str.rpad(count.count()));

        }


    }

    @Test
    public void batchRead() {

        client.queue().take();


        client.set("foo.bar.1", 1);
        client.set("foo.bar.2", 2);
        client.set("foo.bar.3", 3);

        Sys.sleep(10);

        client.queue().take();

        client.batchLoad(Lists.list("foo.bar.1", "foo.bar.2", "foo.bar.3"));

        BatchResult batchResult = (BatchResult) client.queue().take();

        puts(batchResult);

    }

    @Test
    public void batchSet() {

        client.queue().take();

        final Map<String, Object> map = map("batchh.foo.bar.1",
                (Object) 1, "batchh.foo.bar.2", 2, "batchh.foo.bar.3", 3);

        client.setBatch(map);

        Sys.sleep(10);

        client.batchLoad(Lists.list("batchh.foo.bar.1", "batchh.foo.bar.2", "batchh.foo.bar.3"));


        BatchResult batchResult = (BatchResult) client.queue().take();

        ok |= map.size() == batchResult.getResults().size() || die(batchResult.getResults().size(), batchResult.getResults());


        ok |= batchResult.getResults().get("batchh.foo.bar.1").equals("1")
                || die(batchResult.getResults().size(), batchResult.getResults());

        ok |= batchResult.getResults().get("batchh.foo.bar.2").equals("2")
                || die(batchResult.getResults().size(), batchResult.getResults());


        ok |= batchResult.getResults().get("batchh.foo.bar.3").equals("3")
                || die(batchResult.getResults().size(), batchResult.getResults());


    }


    @Test
    public void basicTest() {


        client.queue().take();

        client.set("hi", "mom");

        Sys.sleep(10);

        client.get("hi");


        Sys.sleep(10);


        SingleResult dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());

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

        Sys.sleep(500);


        client.get("basicTest2");

        dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("NULL") || die(dataItem.getValue());


    }


    @Test
    public void basicReadFromMySQL() {

        String mysqlKey = "mySQL" + System.currentTimeMillis();

        client.set(DataStoreSource.REMOTE_DB, mysqlKey, "mom");

        Sys.sleep(10);

        client.get(mysqlKey);


        Sys.sleep(10);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.REMOTE_DB || die(dataItem.source());

        client.get(mysqlKey);

        Sys.sleep(10);

        dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.MEMORY || die(dataItem.source());

    }


    @Test
    public void basicReadWriteFromLevelDB() {

        client.queue().take();

        String key = "LEVEL_DB_READ_TEST-" + System.currentTimeMillis();

        client.set(DataStoreSource.LOCAL_DB, key, "mom");

        Sys.sleep(10);

        client.get(DataStoreSource.LOCAL_DB, key);


        Sys.sleep(10);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.LOCAL_DB || die(dataItem.source());

        client.get(key);

        Sys.sleep(10);

        dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.MEMORY || die(dataItem.source());

    }


    @Test
    public void basicReadWriteFromMySQL() {

        client.queue().take();

        String key = "LEVEL_DB_READ_TEST-" + System.currentTimeMillis();

        client.set(DataStoreSource.REMOTE_DB, key, "mom");

        Sys.sleep(10);

        client.get(DataStoreSource.REMOTE_DB, key);


        Sys.sleep(10);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.REMOTE_DB || die(dataItem.source());

        client.get(key);

        Sys.sleep(10);

        dataItem = (SingleResult) client.queue().take();

        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.MEMORY || die(dataItem.source());

    }

    @Test
    public void basicSetIntoMemory() {

        client.queue().take(); //clear

        String key = "MEMORY_SET_TEST-" + System.currentTimeMillis();

        client.set(DataStoreSource.MEMORY, key, "mom");

        Sys.sleep(10);

        client.get(key);


        Sys.sleep(10);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"mom\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.MEMORY || die(dataItem.source());


    }


    @Test
    public void makeSureLevelDBIsWrittenToAfterFallbackReadToMySQL() {

        client.queue().take(); //clear


        String key = "MYSQL_FALLBACK_POPULATE_LEVELDB_TEST" + System.currentTimeMillis();

        client.set(DataStoreSource.REMOTE_DB, key, "fallback");


        Sys.sleep(10);

        client.get(key);

        client.queue().take(); //clear call that gets from DB


        Sys.sleep(10);

        client.get(DataStoreSource.LOCAL_DB, key);


        Sys.sleep(10);


        SingleResult dataItem = (SingleResult) client.queue().take();


        ok = dataItem.getValue().equals("\"fallback\"") || die(dataItem.getValue());


        ok = dataItem.source() == DataStoreSource.LOCAL_DB || die(dataItem.source());


    }


    @Test
    public void batchGetPopulatesLevelDB() {


        String key1 = "key-1-" + System.currentTimeMillis();
        String key2 = "key-2-" + System.currentTimeMillis();
        String key3 = "key-3-" + System.currentTimeMillis();
        String key4 = "key-4-" + System.currentTimeMillis();

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

}
