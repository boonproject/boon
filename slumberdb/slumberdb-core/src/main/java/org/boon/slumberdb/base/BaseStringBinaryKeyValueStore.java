package org.boon.slumberdb.base;

import org.boon.core.Function;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;
import org.boon.slumberdb.KeyValueStore;
import org.boon.slumberdb.serialization.NoOpSerializer;

import java.util.*;

/**
 * Created by Richard on 4/9/14.
 */
public class BaseStringBinaryKeyValueStore<K, V> {
    /**
     * Store that does the actual writing to DB (likely).
     */
    protected final KeyValueStore<String, byte[]> store;

    protected Function<String, K> keyObjectConverter = new NoOpSerializer<>();
    protected Function<K, String> keyToByteArrayConverter = new NoOpSerializer<>();

    protected Function<byte[], V> valueObjectConverter;
    protected Function<V, byte[]> valueSerializer;


    public BaseStringBinaryKeyValueStore(final KeyValueStore<String, byte[]> store) {
        this.store = store;
    }

    /**
     * Remove a key from the store.
     *
     * @param key
     */
    public void remove(K key) {

        store.remove(toKeyString(key));
    }

    /**
     * Search for a key in the key / value store.
     *
     * @param startKey
     * @return
     */
    public KeyValueIterable<K, V> search(K startKey) {
        final KeyValueIterable<String, byte[]> search = store.search(toKeyString(startKey));
        final Iterator<Entry<String, byte[]>> iterator = search.iterator();
        return new KeyValueIterable<K, V>() {
            @Override
            public void close() {
                search.close();
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        final Entry<String, byte[]> next = iterator.next();

                        return new Entry<>(toKeyObject(next.key()),
                                toValueObject(next.value()));
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }

    /**
     * Load all of the key / values from the store.
     *
     * @return
     */
    public KeyValueIterable<K, V> loadAll() {
        final KeyValueIterable<String, byte[]> search = store.loadAll();
        final Iterator<Entry<String, byte[]>> iterator = search.iterator();
        return new KeyValueIterable<K, V>() {
            @Override
            public void close() {
                search.close();
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        final Entry<String, byte[]> next = iterator.next();

                        return new Entry<>(toKeyObject(next.key()),
                                toValueObject(next.value()));
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }

    public Collection<K> loadAllKeys() {
        final Collection<String> keys = store.loadAllKeys();

        final Set<K> set = new HashSet<>();

        for (String key : keys) {
            set.add(toKeyObject(key));
        }

        return set;
    }

    /**
     * Get a value from the key value store.
     *
     * @param key key
     * @return
     */
    public V load(K key) {
        final byte[] bytes = store.load(toKeyString(key));
        if (bytes != null) {
            return toValueObject(bytes);
        } else {
            return null;
        }
    }

    public Map<K, V> loadAllByKeys(Collection<K> keys) {

        Set<K> keySet = new TreeSet<>(keys);

        List<String> byteKeys = new ArrayList<>();

        for (K key : keySet) {
            String bKey = toKeyString(key);
            byteKeys.add(bKey);
        }
        final Map<String, byte[]> map = store.loadAllByKeys(byteKeys);
        final Map<K, V> results = new LinkedHashMap<>();
        for (Map.Entry<String, byte[]> entry : map.entrySet()) {
            results.put(toKeyObject(entry.getKey()), toValueObject(entry.getValue()));
        }

        return results;
    }

    /**
     * Close the store.
     */
    public void close() {
        store.close();

    }


    /**
     * Convert a binary array to a String.
     *
     * @param key key
     * @return
     */
    protected K toKeyObject(String key) {
        return keyObjectConverter.apply(key);
    }

    /**
     * Use serializer to read this byte array as an object.
     *
     * @param value value read
     * @return new object read from key/value store
     */
    protected V toValueObject(byte[] value) {
        return valueObjectConverter.apply(value);
    }


    /**
     * Converts an object to a byte array.
     *
     * @param v object to convert
     * @return byte array representation of object.
     */
    protected byte[] toValueBytes(V v) {
        return this.valueSerializer.apply(v);
    }


    /**
     * Convert a String key to bytes.
     *
     * @param key key to convert
     * @return value
     */
    protected String toKeyString(K key) {
        return this.keyToByteArrayConverter.apply(key);
    }


    /**
     * Put a value in the key/value store.
     *
     * @param key   key
     * @param value value
     */
    public void put(K key, V value) {
        store.put(toKeyString(key), toValueBytes(value));
    }

    /**
     * Put all of these values in the key value store.
     *
     * @param values values
     */
    public void putAll(Map<K, V> values) {
        Set<Map.Entry<K, V>> entries = values.entrySet();
        Map<String, byte[]> map = new HashMap<>(values.size());

        for (Map.Entry<K, V> entry : entries) {
            map.put(toKeyString(entry.getKey()), toValueBytes(entry.getValue()));
        }

        store.putAll(map);
    }

    /**
     * Remove all of these values from the key value store.
     *
     * @param keys
     */
    public void removeAll(Iterable<K> keys) {
        List<String> list = new ArrayList<>();

        for (K key : keys) {
            list.add(toKeyString(key));
        }

        store.removeAll(list);
    }

    public boolean isOpen() {
        return store.isOpen();
    }


    public boolean isClosed() {
        return store.isClosed();
    }


}
