package org.boon.etcd.examples;

import org.boon.core.Sys;
import org.boon.etcd.EtcdClient;
import org.boon.etcd.Response;

import static org.boon.Boon.puts;

/**
 * Created by rhightower on 10/8/14.
 */
public class ExampleMain {


    public static void main(String... args) {

        Response response;

        EtcdClient client = new EtcdClient("localhost", 4001);
        response = client.get("foo");

        puts(response);

        response = client.set("foo", "Rick Was here");

        puts(response);


        response = client.get("foo");



        puts(response);


        response = client.delete("foo");


        puts(response);


        client.setTemp("tempKey", "tempValue", 5);

        puts(client.get("tempKey").node().getValue());

        Sys.sleep(1000);


        puts(client.get("tempKey").node().getValue());

        Sys.sleep(1000);


        puts(client.get("tempKey").node().getValue());

        Sys.sleep(4000);


        puts(client.get("tempKey"));


        Response waitOnKey = client.wait("waitOnKey");

        puts("GOT KEY WE ARE WAITING ONE", waitOnKey);

        puts("Create a dir");

        client.createDir("conf");


        client.createDir("conf/foo1");

        client.createDir("conf/foo2");


        client.createDir("conf/foo3");


        response = client.listRecursive("");

        puts(response);


        response = client.deleteDir("conf");


        puts(response);

        response = client.deleteDirRecursively("conf");


        puts(response);


        response = client.listRecursive("");

        puts(response);

        response = client.createDir("queue");
        puts(response);

        response = client.createDir("queue/job1");
        puts(response);


        response = client.set("queue/job1/mom", "mom");
        puts(response);

        response = client.createDir("queue/job29");
        puts(response);


        response = client.createDir("queue/job3");
        puts(response);


        response = client.listSorted("queue");
        puts(response);





    }

}
