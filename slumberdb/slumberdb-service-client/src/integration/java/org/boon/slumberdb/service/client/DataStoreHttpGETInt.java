package org.boon.slumberdb.service.client;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.results.BatchResult;
import org.boon.slumberdb.service.results.StatCount;
import org.boon.slumberdb.service.results.StatKey;
import org.boon.slumberdb.service.results.StatsResults;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.*;
import org.boon.datarepo.Repo;
import org.boon.datarepo.Repos;
import org.boon.sort.Sort;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.criteria.ObjectFilter.*;


/**
 * Created by Richard on 9/5/14.
 */
public class DataStoreHttpGETInt {


    String request;
    String response;
    boolean ok;
    String url = "http://localhost:10100" + ProtocolConstants.DEFAULT_REST_URI;

    @Before
    public void setup() {


    }


    @Test
    public void callRESTJmxDump() {

        String url = "http://localhost:11110/slumberdb/admin/jmxstatspretty";
        final String jmxDUMP = HTTP.get(url);

        puts(jmxDUMP);


    }
    @Test
    public void basicTest() {

        request = Str.add(url, "?action=", "set", "&key=", "knownkey", "&value=", "mom");
        send(Action.SET);


        request = Str.add(url, "?action=", "get", "&key=", "knownkey");
        send(Action.GET);

        ok = response.endsWith("mom") || die(response);


    }

    @Test
    public void doesNotExists() {

        request = Str.add(url, "?action=", "get", "&key=", "DOES_NOT_EXISTS-" + System.currentTimeMillis());
        send(Action.GET);

        ok = response.endsWith("NULL") || die(response);


    }


    @Test
    public void getStats() {

        final Class<StatCount> stat = StatCount.class;

        request = Str.add(url, "?action=", Action.GET_STATS.verb());
        send(Action.GET_STATS);


        StatsResults results = StatsResults.fromTextMessage(response);


        final Repo<StatKey, StatCount> repo = Repos.builder().primaryKey("key")
                .lookupIndex("count")
                .lookupIndex("action")
                .lookupIndex("source")
                .searchIndex("count")
                .build(StatKey.class, StatCount.class);

        repo.addAll(results.getResults().values());
        List<StatCount> actionData;


        puts("SERVER STATS CPU");


        actionData = repo.results(
                eq("action", Action.GET_STATS),
                eq("source", DataStoreSource.SERVER),
                startsWith("area", "CPU ")
        ).sort(Sort.sortByDesc("count")).asList(stat);


        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.action(), 10),
                    Str.rpad(count.area().trim(), 50), Str.lpad(Str.num(count.count()), 20));

        }


        puts();


        puts("SERVER STATS GC");
        actionData = repo.results(
                eq("action", Action.GET_STATS),
                eq("source", DataStoreSource.SERVER),
                startsWith("area", "GC ")
        ).sort(Sort.sortBy("area")).asList();


        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.action(), 10), Str.rpad(count.area().trim(), 50), Str.lpad(Str.num(count.count()), 20));

        }


        puts();


        puts("SERVER STATS Time");
        actionData = repo.results(
                eq("action", Action.GET_STATS),
                eq("source", DataStoreSource.SERVER),
                startsWith("area", "Time ")
        ).sort(Sort.sortByDesc("count")).asList();


        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.action(), 10), Str.rpad(count.area().trim(), 50), Str.lpad(Str.num(count.count()), 20));

        }


        puts();


        puts("SERVER STATS Thread");
        actionData = repo.results(
                eq("action", Action.GET_STATS),
                eq("source", DataStoreSource.SERVER),
                startsWith("area", "Thread ")
        ).sort(Sort.sortByDesc("count")).asList();


        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.action(), 10), Str.rpad(count.area().trim(), 50), Str.lpad(Str.num(count.count()), 20));

        }

        puts();

        puts("SERVER STATS FILE");
        actionData = repo.results(
                eq("action", Action.GET_STATS),
                startsWith("area", "FILE")
        ).sort(Sort.sortByDesc("count")).asList();


        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 20), Str.rpad(count.area().trim(), 50), Str.lpad(Str.num(count.count()), 20));

        }


        puts("FILE SYSTEM STATS");

        actionData = repo.results(eq("action", Action.GET_STATS),
                eq("source", DataStoreSource.FILE_SYSTEM)).sort(Sort.sortByDesc("count")).asList();

        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.action(), 10), Str.rpad(count.area().trim(), 50), Str.lpad(Str.num(count.count()), 20));

        }


        puts();


        puts("SERVER STATS Memory");
        actionData = repo.results(
                eq("action", Action.GET_STATS),
                eq("source", DataStoreSource.SERVER),
                startsWith("area", "Memory")
        ).sort(Sort.sortByDesc("count")).asList(stat);


        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.action(), 10), Str.rpad(count.area().trim(), 50), Str.lpad(Str.num(count.count()), 20));

        }

        puts();

        puts("----------------------------------");


        puts();


        puts("MEMORY STORE STATS");

        actionData = repo.results(
                eq("source", DataStoreSource.MEMORY)).sort(Sort.sortBy("area")).asList();

        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.action(), 10), Str.rpad(count.area().trim(), 50), Str.lpad(Str.num(count.count()), 20));

        }
        puts();


        puts("MYSQL STORE STATS");

        actionData = repo.results(
                eq("source", DataStoreSource.REMOTE_DB)).sort(Sort.sortBy("area")).asList();

        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.action(), 10), Str.rpad(count.area().trim(), 50), Str.lpad(Str.num(count.count()), 20));

        }

        puts();

        puts("SERVER Thread CPU Time");


        actionData = repo.results(and(
                        eq("action", Action.GET_STATS),
                        startsWith("area", "Thread CPU Time"))
        ).sort(Sort.sortBy("area")).asList();

        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.action(), 10), Str.rpad(count.area().trim(), 50), Str.lpad(Str.num(count.count()), 20));

        }


        puts();
        puts("-------------------");

        puts();

        puts("CONFIG");


        actionData = repo.results(
                startsWith("area", "CONFIG")
        ).sort(Sort.sortBy("area")).asList();

        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.area().trim(), 60), Str.lpad(Str.num(count.count()), 20));

        }


        puts("\nDATA STORE STATS ALL");

        actionData = repo.results(
        ).sort(
                Sort.sortBy("source").then("action").then("area").then("count")
        ).asList(stat);

        for (StatCount count : actionData) {
            puts(Str.rpad(count.source(), 10), Str.rpad(count.action(), 10), Str.rpad(count.area().trim(), 60), Str.lpad(Str.num(count.count()), 20));

        }


        puts();


    }


    private void send(Action action) {

        puts("Going to send DataStore Action", action);

        puts("HTTP GET for", action, "\n", request, "\n");
        response = HTTP.get(request);
        puts("HTTP RESPONSE (200) for", action, "\n",
                response.replace(ProtocolConstants.DELIMITER_STR, " | ")
                        .replace(ProtocolConstants.SUB_DELIM_STR, " ~ "), "\n"
        );


        puts("--------------------------------\n\n");

    }

    @Test
    public void shallowGet() {

        request = Str.add(url, "?action=", "set", "&key=", "knownkey", "&value=", "mom");
        send(Action.SET);


        puts("Going to do MEM_GET now");

        request = Str.add(url, "?action=", "getMem", "&key=", "knownkey");

        send(Action.GET_MEM);

        final String[] split = StringScanner.split(response, ProtocolConstants.DELIMITER);
        String source = split[3];

        ok = source.equals("MEMORY");

        ok = response.endsWith("mom") || die(response);

    }

    @Test
    public void levelDBGet() {

        request = Str.add(url, "?action=", "set", "&key=", "knownkey", "&value=", "mom");
        send(Action.SET);


        puts("Going to do LEVEL DB GET now");

        request = Str.addObjects(url, "?action=", "getLocalDB", "&key=", "knownkey", "&source=", DataStoreSource.LOCAL_DB);

        send(Action.GET_LOCAL_DB);

        final String[] split = StringScanner.split(response, ProtocolConstants.DELIMITER);
        String source = split[3];

        ok = source.equals("LOCAL_DB");

        ok = response.endsWith("mom") || die(response);

    }


    @Test
    public void batchRead() {


        puts("Going to do SET now");

        String keyPrefix = "foo.bar.";

        for (int index = 0; index < 5; index++) {

            request = Str.add(url, "?action=", ProtocolConstants.SET_VERB, "&key=", keyPrefix + index,
                    "&value=", keyPrefix + index + "mom");
            puts("SET", request);
            response = HTTP.get(request);
            puts("RESPONSE", response);
        }


        List<String> keyList = Lists.list("foo.bar.1", "foo.bar.2", "foo.bar.3");

        String keys = Boon.joinBy(ProtocolConstants.HUMAN_RECORD_DELIM.charAt(0), keyList);

        request = Str.add(url, "?action=", ProtocolConstants.BATCH_READ_VERB, "&keys=", keys);

        send(Action.BATCH_READ);


        final BatchResult batchResult = BatchResult.fromTextMessage(response);
        ok = batchResult.getResults().get("foo.bar.1").equals("foo.bar.1mom") || die();
        ok = batchResult.getResults().get("foo.bar.2").equals("foo.bar.2mom") || die();
        ok = batchResult.getResults().get("foo.bar.3").equals("foo.bar.3mom") || die();

    }

    @Test
    public void batchSet() {


        List<String> keyList = Lists.list("apple", "oranges", "bananas");

        List<String> valueList = Lists.list("hi", "mom", "how%20are%20you%3F");


        String keys = Boon.joinBy(ProtocolConstants.HUMAN_RECORD_DELIM.charAt(0), keyList);

        String values = Boon.joinBy(ProtocolConstants.HUMAN_RECORD_DELIM.charAt(0), valueList);


        request = Str.add(url, "?action=", ProtocolConstants.SET_BATCH_VERB, "&keys=", keys,
                "&values=", values);

        send(Action.SET_BATCH);


        request = Str.add(url, "?action=", ProtocolConstants.BATCH_READ_VERB, "&keys=", keys);

        send(Action.BATCH_READ);


        final BatchResult batchResult = BatchResult.fromTextMessage(response);

        ok |= batchResult.getResults().get("apple").equals("hi")
                || die(batchResult.getResults().size(), batchResult.getResults());

        ok |= batchResult.getResults().get("oranges").equals("mom")
                || die(batchResult.getResults().size(), batchResult.getResults());


        ok |= batchResult.getResults().get("bananas").equals("how are you?")
                || die(batchResult.getResults().size(), batchResult.getResults());


    }


    @Test
    public void basicLocalSourceTest() {
        puts("Going to do SET now");

        request = Str.add(url,
                "?", ProtocolConstants.ACTION_MAP_KEY, "=", ProtocolConstants.SET_SOURCE_VERB,
                "&", ProtocolConstants.KEY_KEY, "=", "sourceKeyTest",
                "&", ProtocolConstants.SOURCE_KEY, "=", DataStoreSource.LOCAL_DB.toString(),
                "&", ProtocolConstants.VALUE_KEY, "=", "mom");


        send(Action.SET_SOURCE);


        request = Str.add(url,
                "?", ProtocolConstants.ACTION_MAP_KEY, "=", ProtocolConstants.GET_SOURCE_VERB,
                "&", ProtocolConstants.SOURCE_KEY, "=", DataStoreSource.LOCAL_DB.toString(),
                "&", ProtocolConstants.KEY_KEY, "=", "sourceKeyTest");


        send(Action.GET_SOURCE);

        final String[] split = StringScanner.split(response, ProtocolConstants.DELIMITER);
        String source = split[3];

        ok = source.equals(DataStoreSource.LOCAL_DB.toString());

        ok = response.endsWith("mom") || die(response);


    }


    @Test
    public void basicMemSourceTest() {

        request = Str.add(url,
                "?", ProtocolConstants.ACTION_MAP_KEY, "=", ProtocolConstants.SET_SOURCE_VERB,
                "&", ProtocolConstants.KEY_KEY, "=", "sourceKeyTest",
                "&", ProtocolConstants.SOURCE_KEY, "=", DataStoreSource.MEMORY.toString(),
                "&", ProtocolConstants.VALUE_KEY, "=", "mom");


        send(Action.SET_SOURCE);

        request = Str.add(url,
                "?", ProtocolConstants.ACTION_MAP_KEY, "=", ProtocolConstants.GET_SOURCE_VERB,
                "&", ProtocolConstants.SOURCE_KEY, "=", DataStoreSource.MEMORY.toString(),
                "&", ProtocolConstants.KEY_KEY, "=", "sourceKeyTest");


        send(Action.GET_SOURCE);


        final String[] split = StringScanner.split(response, ProtocolConstants.DELIMITER);
        String source = split[3];

        ok = source.equals(DataStoreSource.MEMORY.toString());

        ok = response.endsWith("mom") || die(response);


    }


    @Test
    public void basicRemoteDBSourceTest() {
        puts("Going to do SET now");

        request = Str.add(url,
                "?", ProtocolConstants.ACTION_MAP_KEY, "=", ProtocolConstants.SET_SOURCE_VERB,
                "&", ProtocolConstants.KEY_KEY, "=", "sourceKeyTest",
                "&", ProtocolConstants.SOURCE_KEY, "=", DataStoreSource.REMOTE_DB.toString(),
                "&", ProtocolConstants.VALUE_KEY, "=", "mom");

        send(Action.SET_SOURCE);


        request = Str.add(url,
                "?", ProtocolConstants.ACTION_MAP_KEY, "=", ProtocolConstants.GET_SOURCE_VERB,
                "&", ProtocolConstants.SOURCE_KEY, "=", DataStoreSource.REMOTE_DB.toString(),
                "&", ProtocolConstants.KEY_KEY, "=", "sourceKeyTest");


        send(Action.GET_SOURCE);


        final String[] split = StringScanner.split(response, ProtocolConstants.DELIMITER);
        String source = split[3];

        ok = source.equals(DataStoreSource.REMOTE_DB.toString());

        ok = response.endsWith("mom") || die(response);


    }


}

