package org.boon.slumberdb.mysql;

import org.boon.Maps;
import org.boon.Str;
import org.boon.core.Sys;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Ok.okOrDie;


public class SimpleStringKeyValueStoreMySQLTest {
    String url = "jdbc:mysql://localhost:3306/slumberdb";
    String userName = "slumber";
    String password = "slumber1234";
    String table = "string-test";
    boolean ok;
    private SimpleStringKeyValueStoreMySQL store;

    @Before
    public void setup() {

        store = new SimpleStringKeyValueStoreMySQL(url, userName, password, table, 20);

    }

    @After
    public void close() {


        store.close();
    }


    @Test
    public void testCrud() {
        store.put("hello1",
                "world"
        );

        String world = store.load("hello1");
        Str.equalsOrDie("world", world);

        store.remove("hello1");

        world = store.load("hello1");
        ok = world == null || die(world);
    }

    @Test
    public void test() {
        store.put("hello",
                "world"
        );

        String world = store.load("hello");
        Str.equalsOrDie("world", world);

    }


    @Test
    public void testBulkPut() {

        Map<String, String> map = Maps.map("hello1", "hello1",
                "hello2", "hello2");


        store.putAll(map);


        String value;

        value = store.load("hello1");
        Str.equalsOrDie("hello1", value);


        value = store.load("hello2");
        Str.equalsOrDie("hello2", value);


        store.remove("hello2");
        value = store.load("hello2");
        okOrDie(value == null);
    }


    @Test
    public void testBulkRemove() {

        Map<String, String> map = Maps.map("hello1", "hello1",
                "hello2", "hello2");


        store.putAll(map);
        store.put("somethingElse", "1");


        String value;

        value = store.load("hello1");
        Str.equalsOrDie("hello1", value);


        value = store.load("hello2");
        Str.equalsOrDie("hello2", value);


        store.removeAll(map.keySet());


        value = store.load("hello1");

        ok = value == null || die();

        value = store.load("hello2");


        ok = value == null || die();


        Str.equalsOrDie("1", store.load("somethingElse"));


    }


    @Test
    public void testSearch() {
        for (int index = 0; index < 100; index++) {
            store.put("key" + index, "value" + index);
        }

        KeyValueIterable<String, String> entries = store.search("key50");
        for (Entry<String, String> entry : entries) {
            puts(entry.key(), entry.value());
        }

        entries.close();
    }


    @Test
    public void testSearch2() {
        for (int index = 0; index < 100; index++) {
            store.put("key" + index, "value" + index);
        }

        Sys.sleep(5000);
        KeyValueIterable<String, String> entries = store.search("key50");
        for (Entry<String, String> entry : entries) {
            puts(entry.key(), entry.value());
        }

        entries.close();
        store.close();
    }


    @Test
    public void testLoadAllKeys() {

        List<String> keys38 = new ArrayList<>();
        List<String> keys41 = new ArrayList<>();
        List<String> keys77 = new ArrayList<>();
        List<String> keys83 = new ArrayList<>();

        for (int index = 0; index < 100; index++) {

            String key = "key.load.all" + index;
            if (keys38.size() < 38 + 1) {
                keys38.add(key);
            }

            if (keys41.size() < 41 + 1) {
                keys41.add(key);
            }

            if (keys77.size() < 77 + 1) {
                keys77.add(key);
            }


            if (keys83.size() < 83 + 1) {
                keys83.add(key);
            }

            store.put(key, key);
        }

        Map<String, String> results = store.loadAllByKeys(keys38);

        puts(results);

        ok = results.containsKey("key.load.all38") || die();

        ok = !results.containsKey("key.load.all39") || die();


        results = store.loadAllByKeys(keys41);

        puts(results);

        ok = results.containsKey("key.load.all39") || die();
        ok = results.containsKey("key.load.all40") || die();
        ok = results.containsKey("key.load.all41") || die();
        ok = !results.containsKey("key.load.all42") || die();


        results = store.loadAllByKeys(keys77);

        puts(results);

        ok = results.containsKey("key.load.all70") || die();
        ok = results.containsKey("key.load.all75") || die();
        ok = results.containsKey("key.load.all77") || die();
        ok = !results.containsKey("key.load.all78") || die();


        results = store.loadAllByKeys(keys83);

        puts(results);

        ok = results.containsKey("key.load.all80") || die();
        ok = results.containsKey("key.load.all81") || die();
        ok = results.containsKey("key.load.all83") || die();
        ok = !results.containsKey("key.load.all84") || die();
    }


}
