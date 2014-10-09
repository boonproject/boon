*Boon etcd is a Java client for etcd.*


*What is etcd?*
etcd is a highly-available key value store for shared configuration and service discovery.

etcd is part of the coreos project.

etcd is inspired by Apache ZooKeeper and doozer, with a focus on being:

Simple: REST like and curl'able user facing API (HTTP+JSON); Secure: optional SSL client cert authentication; Fast: benchmarked 1000s of writes/s per instance; Reliable: properly distributed using Raft.


etcd is written in Go and uses the Raft consensus
algorithm to manage a highly-available replicated log.

You can learn more about etcd at https://github.com/coreos/etcd.


*Boon etcd client for Java*

Unlike most etcd Java clients (perhaps all), it supports wait, which is it allows
you to wait on a key or key directory changing.
Also unlike most etcd Java clients, it supports both async and sync mode.
Like all boon projects, it is easy to use, and fast. :)

Rather then give you a very thin (hard to use) API that merely allows you to pass the various flags and switches, we create a task oriented Java API so all of the most common task are at your finger tips.

There is an async version and a synchronous version of each method for your convienience.


```java

interface Etcd {


    /**
     * Create a directory using async handler
     * @param responseHandler handler
     * @param name name of dir
     */
    void createDir(Handler<Response> responseHandler, String name);

    /**
     * Create a directory (blocking)
     * @param name name of dir
     * @return response
     */
    Response createDir(String name);


    /**
     * Create a temp directory, i.e., one with a time to live TTL
     * @param name name of dir
     * @param ttl ttl
     * @return
     */
    Response createTempDir(String name, long ttl);

    /**
     * Create a temp dir async.
     * @param responseHandler async handler
     * @param name name of dir
     * @param ttl time to live
     */
    void createTempDir(Handler<Response> responseHandler, String name, long ttl);

    /**
     * Update a directories time to live.
     * @param name dir name (path)
     * @param ttl ttl
     * @return
     */
    Response updateDirTTL(String name, long ttl);

    /**
     * Update a directories time to live.
     * @param responseHandler
     * @param name
     * @param ttl
     */
    void updateDirTTL(Handler<Response> responseHandler, String name, long ttl);

    /**
     * Delete a dir
     * @param name
     * @return
     */
    Response deleteDir(String name);

    /**
     * Delete a dir async.
     * @param responseHandler
     * @param name
     */
    void deleteDir(Handler<Response> responseHandler, String name);


    /**
     * Delete a dir and all of its children recursively.
     * @param name
     * @return
     */
    Response deleteDirRecursively(String name);
    void deleteDirRecursively(Handler<Response> responseHandler, String name);


    /**
     * List keys and value
     * @param key
     * @return
     */
    Response list(String key);


    /**
     * List keys and values asycn
     * @param responseHandler
     * @param key
     */
    void list(Handler<Response> responseHandler, String key);

    /**
     * List dir recursively.
     * @param key
     * @return
     */
    Response listRecursive(String key);
    void listRecursive(Handler<Response> responseHandler, String key);

    /**
     * List dir sorted for order so we can pull things out FIFO for job queuing.
     * @param key
     * @return
     */
    Response listSorted(String key);
    void listSorted(Handler<Response> responseHandler, String key);


    /**
     * Set a key
     * @param key
     * @param value
     * @return
     */
    Response set(String key, String value);
    void set(Handler<Response> responseHandler, String key, String value);

    /**
     * Add a config under this key
     * @param key
     * @param fileName
     * @return
     */
    Response setConfigFile(String key, String fileName);
    void  setConfigFile(Handler<Response> responseHandler, String key, String fileName);

    /**
     * Update the key with a new value if it already exists
     * @param key
     * @param value
     * @return
     */
    Response setIfExists(String key, String value);
    void  setIfExists(Handler<Response> responseHandler, String key, String value);


    /**
     * Create the new key value only if it does not already exist.
     * @param key
     * @param value
     * @return
     */
    Response setIfNotExists(String key, String value);
    void  setIfNotExists(Handler<Response> responseHandler, String key, String value);

    /**
     * Create a temporary value with ttl set
     * @param key
     * @param value
     * @param ttl
     * @return
     */
    Response setTemp(String key, String value, int ttl);
    void  setTemp(Handler<Response> responseHandler, String key, String value, int ttl);

    /**
     * Remove TTL from key/value
     * @param key
     * @param value
     * @return
     */
    Response removeTTL(String key, String value);
    void removeTTL(Handler<Response> responseHandler, String key, String value);


    /**
     * Compare and swap if the previous value is the same
     * @param key
     * @param preValue
     * @param value
     * @return
     */
    Response compareAndSwapByValue(String key, String preValue, String value);
    void compareAndSwapByValue(Handler<Response> responseHandler, String key, String preValue, String value);

    /**
     * Compare and swap if the modified index has not changed.
     * @param key
     * @param prevIndex
     * @param value
     * @return
     */
    Response compareAndSwapByModifiedIndex(String key, long prevIndex, String value);
    void compareAndSwapByModifiedIndex(Handler<Response> responseHandler, String key, long prevIndex, String value);


    /**
     * Get the value
     * @param key
     * @return
     */
    Response get(String key);
    void get(Handler<Response> responseHandler, String key);


    /**
     * Get the value and ensure it is consistent. (Slow but consistent)
     * @param key
     * @return
     */
    Response getConsistent(String key);
    void getConsistent(Handler<Response> responseHandler, String key);

    /**
     * Wait for this key to change
     * @param key
     * @return
     */
    Response wait(String key);
    void wait(Handler<Response> responseHandler, String key);


    /**
     * Wait for this key to change and you can ask for the past key value based on index just in case you missed it.
     * @param key
     * @param index
     * @return
     */
    Response wait(String key, long index);
    void wait(Handler<Response> responseHandler, String key, long index);


    /**
     * Wait for this key to change and any key under this key dir recursively.
     * @param key
     * @return
     */
    Response waitRecursive(String key);
    void waitRecursive(Handler<Response> responseHandler, String key);


    /**
     * Wait for this key to change and any key under this key dir recursively, and
     * ask for the past key value based on index just in case you missed it.
     * @param key
     * @param index
     * @return
     */
    Response waitRecursive(String key, long index);
    void waitRecursive(Handler<Response> responseHandler, String key, long index);

    /**
     * Delete the key.
     * @param key
     * @return
     */
    Response delete(String key);
    void delete(Handler<Response> responseHandler, String key);

    /** Delete the key only if it is at this index
     *
     * @param key
     * @param index
     * @return
     */
    Response deleteIfAtIndex(String key, long index);
    void deleteIfAtIndex(Handler<Response> responseHandler, String key, long index);

    /**
     * Delete the value but only if it is at the previous value
     * @param key
     * @param prevValue
     * @return
     */
    Response deleteIfValue(String key, String prevValue);
    void deleteIfValue(Handler<Response> responseHandler, String key, String prevValue);

}
```

As you can see, the interface tries to spell out all of the main etcd operations
form the etcd tutorial. If we are missing any, let us know.


You can use boon etcd client synchronously as follows:

```java

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

```


Or you can use it asynchronously as follows:

```java

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

```
