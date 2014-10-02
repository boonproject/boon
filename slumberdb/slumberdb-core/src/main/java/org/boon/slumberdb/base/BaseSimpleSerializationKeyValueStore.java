package org.boon.slumberdb.base;


import org.boon.cache.SimpleCache;
import org.boon.core.Function;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;
import org.boon.slumberdb.KeyValueStore;

import java.util.*;

/**
 * Created by Richard on 4/9/14.
 */
public abstract class BaseSimpleSerializationKeyValueStore<K, V> {
    /**
     * Store that does the actual writing to DB (likely).
     */
    protected final KeyValueStore<byte[], byte[]> store;

    protected Function<byte[], K> keyObjectConverter;
    protected Function<K, byte[]> keyToByteArrayConverter;

    protected Function<byte[], V> valueObjectConverter;
    protected Function<V, byte[]> valueToByteArrayConverter;
    protected SimpleCache<K, byte[]> keyCache = new SimpleCache<>(1_000);


    public BaseSimpleSerializationKeyValueStore(final KeyValueStore<byte[], byte[]> store) {
        this.store = store;
    }

    /**
     * Remove a key from the store.
     *
     * @param key
     */
    public void remove(K key) {

        store.remove(toKeyBytes(key));
    }

    /**
     * Search for a key in the key / value store.
     *
     * @param startKey
     * @return
     */
    public KeyValueIterable<K, V> search(K startKey) {
        final KeyValueIterable<byte[], byte[]> search = store.search(toKeyBytes(startKey));
        final Iterator<Entry<byte[], byte[]>> iterator = search.iterator();
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
                        final Entry<byte[], byte[]> next = iterator.next();

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
        final KeyValueIterable<byte[], byte[]> search = store.loadAll();
        final Iterator<Entry<byte[], byte[]>> iterator = search.iterator();
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
                        final Entry<byte[], byte[]> next = iterator.next();

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
        final Collection<byte[]> keys = store.loadAllKeys();

        final Set<K> set = new HashSet<>();

        for (byte[] key : keys) {
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
        final byte[] bytes = store.load(toKeyBytes(key));
        if (bytes != null) {
            return toValueObject(bytes);
        } else {
            return null;
        }
    }

    public Map<K, V> loadAllByKeys(Collection<K> keys) {

        Set<K> keySet = new TreeSet<>(keys);

        List<byte[]> byteKeys = new ArrayList<>();

        for (K key : keySet) {
            byte[] bKey = toKeyBytes(key);
            byteKeys.add(bKey);
        }
        final Map<byte[], byte[]> map = store.loadAllByKeys(byteKeys);
        final Map<K, V> results = new LinkedHashMap<>();
        for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
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
    protected K toKeyObject(byte[] key) {
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
        return this.valueToByteArrayConverter.apply(v);
    }


    /**
     * Convert a String key to bytes.
     *
     * @param key key to convert
     * @return value
     */
    protected byte[] toKeyBytes(K key) {

        byte[] keyBytes = keyCache.get(key);
        if (keyBytes == null) {
            keyBytes = this.keyToByteArrayConverter.apply(key);
            keyCache.put(key, keyBytes);
        }
        return keyBytes;

    }


    /**
     * Put a value in the key/value store.
     *
     * @param key   key
     * @param value value
     */
    public void put(K key, V value) {
        store.put(toKeyBytes(key), toValueBytes(value));
    }

    /**
     * Put all of these values in the key value store.
     *
     * @param values values
     */
    public void putAll(Map<K, V> values) {
        Set<Map.Entry<K, V>> entries = values.entrySet();
        Map<byte[], byte[]> map = new HashMap<>(values.size());

        for (Map.Entry<K, V> entry : entries) {
            map.put(toKeyBytes(entry.getKey()), toValueBytes(entry.getValue()));
        }

        store.putAll(map);
    }

    /**
     * Remove all of these values from the key value store.
     *
     * @param keys
     */
    public void removeAll(Iterable<K> keys) {
        List<byte[]> list = new ArrayList<>();

        for (K key : keys) {
            list.add(toKeyBytes(key));
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
