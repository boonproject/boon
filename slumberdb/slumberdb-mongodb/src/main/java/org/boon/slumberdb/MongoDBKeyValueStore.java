package org.boon.slumberdb;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.boon.Str;
import org.boon.slumberdb.entries.Entry;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.boon.Maps.map;
import static org.boon.Maps.toMap;


public class MongoDBKeyValueStore<K, V> implements KeyValueStore<K, V> {

    DBCollection collection;
    DB database;

    public MongoDBKeyValueStore(DBCollection collection, DB database) {
        this.collection = collection;
        this.database = database;
    }

    @Override
    public void put(K key, V value) {

        final BasicDBObject basicDBObject = new BasicDBObject(toMap(value));
        basicDBObject.put("key", Str.str(key));
        collection.insert(basicDBObject);
    }

    @Override
    public void putAll(Map<K, V> values) {

        database.requestStart();
        try {
            for (Map.Entry<K, V> entry : values.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        } finally {
            database.requestDone();
        }
    }

    @Override
    public void removeAll(Iterable<K> keys) {


        database.requestStart();
        try {
            for (K key : keys) {
                collection.remove(new BasicDBObject(map("key", Str.str(key))));
            }
        } finally {
            database.requestDone();
        }

    }

    @Override
    public void remove(K key) {
        collection.remove(new BasicDBObject(map("key", Str.str(key))));

    }

    @Override
    public KeyValueIterable<K, V> search(K startKey) {


        final DBCursor cursor = collection.find(new BasicDBObject(map("key", new BasicDBObject("$gt", startKey))));
        return new KeyValueIterable<K, V>() {
            @Override
            public void close() {
                cursor.close();
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    @Override
                    public boolean hasNext() {
                        return cursor.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        //TODO left off here
                        return null;
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        };
    }

    @Override
    public KeyValueIterable<K, V> loadAll() {
        return null;
    }

    @Override
    public Collection<K> loadAllKeys() {
        return null;
    }

    @Override
    public V load(K key) {
        return null;
    }

    @Override
    public Map<K, V> loadAllByKeys(Collection<K> keys) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isOpen() {
        //TODO
        return false;
    }

    @Override
    public boolean isClosed() {
        //TODO
        return false;
    }
}
