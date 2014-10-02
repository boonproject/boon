Slumber DB - Key Value Store for JSON / REST
=========

The JSON/Java database for REST and Websocket storage.

Boon is the fastest JSON serialization for the JVM.
Kryo is the fastest Java serialization for the JVM.

This project marries Boon/Kryo with LevelDB, MySQL, RocksDB, and LMDB to provide simple key/value storage.


The focus is not on data grid usage, but just as a data safe reliable key/value store for Java.

We are at 95% plus code coverage. We care about providing a quality, simple fast storage mechanism with an easy to use interface for Java.


Features
=========

Provide a simple key/value store.

* Store by key.
* Read by key.
* Store many key/value pairs.
* Read many key/value pairs.
* Delete a key.
* Delete a batch of keys.
* Search by key.

We have an implementation in LevelDB and MySQL for Strings, JSON, serialized Java and binary.



Interface
==========

The main interface(s) for SlumberDB are as follows:

```java

public interface KeyValueStore <K, V> extends Closeable{

    void put(K key, V value);


    void putAll(Map<K, V> values);

    void removeAll(Iterable<K> keys);

    void remove(K key);

    KeyValueIterable<K, V> search(K startKey);

    KeyValueIterable<K, V> loadAll();

    V get(K key);


    void close();

    void flush();

}

```

The focus is on being a store.
There is some rudimentary read operations for faulting in-memory cache operations.



License
=========

SlumberDB is Apache 2.0 license.


Getting Started
===============

#### Creating a JSON Key/Value store that uses LevelDB

```java


    private JsonKeyValueStore<String, Employee> store;
    
    store = new SimpleJsonKeyValueStoreLevelDB(file.toString(), Employee.class);

```

Writing employees out to LevelDB

```java
     store.put("123", new Employee("Rick", "Hightower"));
```

Writing out many employees to LevelDB (using JSON).

```java
        Map<String, Employee> map = Maps.map(

                "123", new Employee("Rick", "Hightower"),
                "456", new Employee("Paul", "Tiger"),
                "789", new Employee("Jason", "Donner")

        );


        store.putAll(map);

```

Reading an employee from leveldb
```java

        employee = store.get("123");
        Str.equalsOrDie("Rick", employee.getFirstName());
        Str.equalsOrDie("Hightower", employee.getLastName());

```

Deleting a bunch of employees
```java

        store.removeAll(map.keySet());
```

Searching for employees with id "key.50"
```java
        KeyValueIterable<String, Employee> entries = store.search("key.50");

```

Iterating through every key in the key/value store

```java

        KeyValueIterable<String, Employee> entries = store.loadAll();


        for (Entry<String, Employee> entry : entries) {
            puts (entry.key(), entry.value());
        }

```

Now to do the same as above but use Kryo instead of JSON.

#### Kryo version

```java

private SimpleKyroKeyValueStoreLevelDB<Employee> store;
store = new SimpleKyroKeyValueStoreLevelDB(file.toString(), Employee.class);

//The rest of the CRUD code is the same except for Employee has to implement serializable

```

Kyro is the fastest Java binary serialization mechanism for the JVM and it works with iOS and Java.
Boon is the fastest JSON serialization mechanism for the JVM, and JSON works everywhere.

Now to do the above again but use MySQL instead of LevelDB.

#### MySQL and JSON 
```java

    private SimpleJsonKeyValueStoreMySQL<Employee> store;
    String url = "jdbc:mysql://localhost:3306/slumberdb";
    String userName = "slumber";
    String password = "slumber7890";
    String table = "json-employee-test";

    ...
    store = new SimpleJsonKeyValueStoreMySQL(url, userName, password, table, Employee.class);
    //The rest of the CRUD code is the same except

```


#### MySQL and Kryo 
```java

    private SimpleKyroKeyValueStoreMySQL<Employee> store;
    String url = "jdbc:mysql://localhost:3306/slumberdb";
    String userName = "slumber";
    String password = "slumber789";
    String table = "kyro-emp-test";
    
    ...
    ...
    store = new SimpleKyroKeyValueStoreMySQL(url, userName, password, table, Employee.class);

```


So far we have the following concrete classes:

LevelDB:

* **LevelDBKeyValueStore**  key value store that writes binary data (key and data are binary)
* **SimpleJavaSerializationKeyValueStoreLevelDB** Simple store that has a String key and uses plain Java serialization
* **SimpleJsonKeyValueStoreLevelDB** Simple store that uses Boon JSON serialization and LevelDB
* **SimpleKyroKeyValueStoreLevelDB** Simple store that uses Kyro Serialization and LevelDB
* **SimpleStringKeyValueStoreLevelDB** String keys and String values using LevelDB

I am considering an equal number of Simple stores that use Long as keys. Then beyond that you have to roll your own on top of these.

MySQL:

* **SimpleJavaSerializationKeyValueStoreMySQL** Simple store that has a String key and uses plain Java serialization
* **SimpleJsonKeyValueStoreMySQL** Simple store that uses Boon JSON serialization and MySQL
* **SimpleKyroKeyValueStoreMySQL** Simple store that uses Kyro Serialization and MySQL
* **SimpleStringKeyValueStoreMySQL** String keys and String values using MySQL

SlumberDB fits into the BerkerlyDB sort of use case. It is currently meant for embedded sorts of access.
SlumberDB will likely support LMDB and RocksDB in short order. Early RocksDB support was started but not complete.
I am also considering a wire protocol on top of JSON and Kyro using Vertx, and some replication using Vertx.


Related projects
=========

**LevelDB** is a lightweight database by **Google** modeled after BigTable tablet store.
LevelDB gets used by Chrome.

**RocksDB** is a server-side version of LevelDB by **Facebook** that reportedly is more scalable than LevelDB.

**MySQL** is well MySQL. We are using it as a table with two columns. One column is indexed.

**LMDB** is an fast, compact key/value store which gets used by the OpenLDAP Project.


**Kryo** is the fastest and efficient object graph serialization for the JVM.



See Kryo at: https://github.com/EsotericSoftware/kryo

See _**Boon**_ at: https://github.com/RichardHightower/org.boon

See LevelDBJNI at: https://github.com/fusesource/leveldbjni

See LevelDB at: https://code.google.com/p/leveldb/

See RocksDB at: http://rocksdb.org/

See RocksDBJNI at: https://github.com/fusesource/rocksdbjni

See LMDB: http://symas.com/mdb/

See LMDBJNI: https://github.com/chirino/lmdbjni


Thanks
=========

Special thanks to Hiram Chirino for writing **leveldbjni**, **lmdbjni** and **rocksdbjni**.
Without Hiram, Apache Apollo hero, none of this would be possible.

See Mr. Chirino at:
https://github.com/chirino


Special thanks to Tim Fox for writing Vertx. Tim is the author of Vertx which leads the charts for fast JVM based web servers.
Vertx is so much more than a web server.

https://github.com/purplefox

We plan on using Vertx for replication and client/server networking support.

http://www.infoq.com/news/2011/12/apollo-benchmarks


Primary Author
=========

Rick Hightower works on JSR-107 and JSR-347 as well as Boon
which has an in-memory query engine, and a fast JSON parser/serializer.


Other goals
=====


This is an effort to write key / value stores for JSR-107 RI
and one for HazelCast MapStore.

The goals are simple. Write a MySQL, LevelDB, LMDB, and RocksDB key / value store
for Java serialization and JSON for HazelCast MapStore and JSR-107 RI Cache Stores.

The MySQL version, for example, will use Boon JSON serialization and kryo.

The plan is to use **rocksdbjni** and **lmdbjni** for the LevelDB implementation, and later Vertx for server and replication support.

Currently we support MySQL and LevelDB.

We plan on using Vertx to provide a network interface using Kyro and Boon JSON as well as replication.

The focus is not on data grid usage, but just as a data safe reliable key/value store for Java.
