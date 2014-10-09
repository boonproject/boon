package org.boon.etcd.examples;

import org.boon.core.Handler;
import org.boon.core.Sys;
import org.boon.etcd.EtcdClient;
import org.boon.etcd.Response;

import static org.boon.Boon.puts;


/**
 * Created by rhightower on 10/8/14.
 */
public class ExampleMain2 {


    public static void main(String... args) {

        Handler<Response> handler = new Handler<Response>() {
            @Override
            public void handle(Response event) {

                if (event.node() != null) {
                    puts(event.action(), event.node().key(), event);
                } else {
                    puts(event);
                }
            }
        };

        EtcdClient client = new EtcdClient("localhost", 4001);
        client.get("foo");


        client.set(handler, "foo", "Rick Was here");

        Sys.sleep(1_000);


        client.get(handler, "foo");

        Sys.sleep(1_000);


        client.delete(handler, "foo");

        Sys.sleep(1_000);



        client.setTemp(handler, "tempKey", "tempValue", 5);

        Sys.sleep(1_000);

        client.get(handler, "tempKey");

        Sys.sleep(1000);


        client.get(handler, "tempKey");

        Sys.sleep(1000);


        client.get(handler, "tempKey");

        Sys.sleep(4000);


        client.get(handler, "tempKey");


        Sys.sleep(1000);

        client.get(handler, "tempKey");

        Sys.sleep(1000);


        puts("WAITING ON KEY");

        client.wait(handler, "waitOnKey");

        Sys.sleep(10_000);

        client.createDir(handler, "conf");

        Sys.sleep(1000);



        client.createDir(handler, "conf/foo1");
        client.createDir(handler, "conf/foo2");
        client.createDir(handler, "conf/foo3");

        puts ("LIST RECURSIVE");
        client.listRecursive(handler, "");


        Sys.sleep(3_000);

        client.deleteDir(handler, "conf");

        Sys.sleep(1_000);


        client.deleteDirRecursively(handler, "conf");
        Sys.sleep(1_000);


        client.listRecursive(handler, "");

        Sys.sleep(1_000);

        client.createDir(handler, "queue");
        Sys.sleep(1_000);


        client.createDir(handler, "queue");
        Sys.sleep(1_000);


        client.createDir(handler, "queue/job1");
        Sys.sleep(1_000);


        client.set(handler, "queue/job1/mom", "mom");
        Sys.sleep(1_000);

        client.createDir(handler, "queue/job29");
        Sys.sleep(1_000);


        client.createDir(handler, "queue/job3");
        Sys.sleep(1_000);


        client.listSorted(handler, "queue");
        Sys.sleep(1_000);





    }

}
