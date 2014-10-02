package org.boon.slumberdb.impl;

import org.boon.cache.SimpleCache;
import org.boon.slumberdb.KeyValueStore;
import org.boon.slumberdb.StringKeyValueStore;
import org.boon.slumberdb.base.BaseSimpleSerializationKeyValueStore;
import org.boon.slumberdb.serialization.ByteArrayToStringConverter;
import org.boon.slumberdb.serialization.StringToByteArrayConverter;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.boon.primitive.Byt.bytes;

/**
 * This represents a simple key / value store for strings.
 * You can combine this with any key value binary store (KeyValueStore<byte[], byte[]>).
 * It will encode the strings using UTF-8 encoding.
 */
public class SimpleStringKeyValueStore extends BaseSimpleSerializationKeyValueStore<String, String> implements StringKeyValueStore {

    protected SimpleCache<String, byte[]> keyCache = new SimpleCache<>(1_000);

    public SimpleStringKeyValueStore(KeyValueStore<byte[], byte[]> store) {

        super(store);
        this.valueObjectConverter = new ByteArrayToStringConverter();
        this.valueToByteArrayConverter = new StringToByteArrayConverter();

        this.keyObjectConverter = new ByteArrayToStringConverter();
        this.keyToByteArrayConverter = new StringToByteArrayConverter();

    }


    @Override
    public String load(String key) {
        byte[] bytes = store.load(keyToBytes(key));
        if (bytes == null) {
            return null;
        }
        return toString(bytes);
    }

    public String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public Map<String, String> loadAllByKeys(Collection<String> keys) {

        Set<String> keySet = new TreeSet<>(keys);

        List<byte[]> byteKeys = new ArrayList<>();

        for (String key : keySet) {
            byte[] bKey = bytes(key);
            byteKeys.add(bKey);
        }
        final Map<byte[], byte[]> map = store.loadAllByKeys(byteKeys);
        final Map<String, String> results = new LinkedHashMap<>();
        for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
            results.put(toString(entry.getKey()), toString(entry.getValue()));
        }

        return results;
    }

    private byte[] keyToBytes(String key) {
        byte[] value = keyCache.get(key);
        if (value == null) {
            value = key.getBytes(StandardCharsets.UTF_8);
            keyCache.put(key, value);
        }
        return value;
    }


    @Override
    public void close() {
        store.close();
    }

}

