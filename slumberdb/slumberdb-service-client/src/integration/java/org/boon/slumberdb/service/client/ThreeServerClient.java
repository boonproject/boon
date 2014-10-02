package org.boon.slumberdb.service.client;

import org.boon.slumberdb.service.config.Bucket;
import org.boon.slumberdb.service.config.DataStoreClientConfig;
import org.boon.slumberdb.service.results.Response;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.queue.DataOutputQueueTransferQueue;
import org.boon.Lists;
import org.boon.core.Sys;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

import java.io.IOException;
import java.util.List;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 7/6/14.
 */
public class ThreeServerClient {


    public static void main(String... args) throws IOException {

        /* Create a new instance of Vertx. */
        Vertx vertx = VertxFactory.newVertx();

        /* CONFIG: Initialize the client provider. */
        DataStoreVertxWebSocketClientProvider provider = new DataStoreVertxWebSocketClientProvider();
        provider.registerSupplier(); //register the provider.
        DataStoreClientConfig config = DataStoreClientConfig.config();

        puts(config);
        /* CONFIG: Simple one bucket for hits. You have to run the data store client. */
        config.buckets(

                Bucket.bucket("localhost", 10100, "localhost", 10200),

                Bucket.bucket("localhost", 10200, "localhost", 10300),

                Bucket.bucket("localhost", 10300, "localhost", 10100)
        );


        puts("Config", config);
        provider.init(vertx, new DataOutputQueueTransferQueue(2000), config, true, false);

        /* Create the client. */
        DataStoreClient client = DataStoreFactory.createClient("TEST", null);

        Sys.sleep(100);


        List<String> names = Lists.list("Harry", "Larry", "Mary", "Bary", "Gary", "Carey", "Jerry", "Perry", "Terry");

        for (String name : names) {

        /* Send the value. */
            client.set("testSubKey." + name, "value." + name);

        }


        for (String name : names) {

            /* Send the value. */
            client.get("testSubKey." + name);

        }



        /* Get the response. You can poll the queue in another thread. */
        DataOutputQueue queue = client.queue();


        Response dataItem = queue.take(); //Take is a blocking call. But poll is not blocking.


        while (dataItem != null) {

            puts(dataItem);
            dataItem = queue.poll();
        }


        for (int index = 0; index < 100; index++) {
            for (String name : names) {

                /* Send the value. */
                client.set("testSubKey." + name + index, "value." + name + index);

            }


            for (String name : names) {

            /* Send the value. */
                client.get("testSubKey." + name + index);

            }
        }


        while (dataItem != null) {

            puts(dataItem);
            dataItem = queue.poll();
        }


    }
}
