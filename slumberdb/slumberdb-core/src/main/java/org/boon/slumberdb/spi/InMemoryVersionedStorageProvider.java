package org.boon.slumberdb.spi;

import org.boon.collections.ConcurrentWeakHashMap;
import org.boon.collections.LazyMap;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;
import org.boon.slumberdb.entries.VersionKey;
import org.boon.slumberdb.entries.VersionedEntry;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.boon.concurrent.Timer;

/**
 * Created by Richard on 9/23/14.
 */
public class InMemoryVersionedStorageProvider implements VersionedStorageProvider {

    private static final int PADDING = 1024;

    private final ConcurrentNavigableMap<String, ByteBuffer> searchMap = new ConcurrentSkipListMap<>();
    private final ConcurrentHashMap<String, ByteBuffer> map = new ConcurrentHashMap<>(10_000);
    private final ConcurrentWeakHashMap<String, VersionedEntry<String, byte[]>> weakHashMap = new ConcurrentWeakHashMap<>(10_000);



    @Override
    public long totalConnectionOpen() {
        return 1;
    }

    @Override
    public long totalClosedConnections() {
        return 0;
    }

    @Override
    public long totalErrors() {
        return 0;
    }

    @Override
    public void removeAll(Iterable<String> keys) {

        for (String key : keys) {
            map.remove(key);
            searchMap.remove(key);
            weakHashMap.remove(key);
        }
    }

    @Override
    public void remove(String key) {

        map.remove(key);
        searchMap.remove(key);
        weakHashMap.remove(key);

    }

    @Override
    public KeyValueIterable<String, VersionedEntry<String, byte[]>> search(String startKey) {


        final ConcurrentNavigableMap<String,ByteBuffer> subMap = searchMap.tailMap(startKey);
        final Iterator<Map.Entry<String, ByteBuffer>> iterator = subMap.entrySet().iterator();

        return new KeyValueIterable<String, VersionedEntry<String, byte[]>>() {
            @Override
            public void close() {

            }

            @Override
            public Iterator<Entry<String, VersionedEntry<String, byte[]>>> iterator() {
                return new Iterator<Entry<String, VersionedEntry<String, byte[]>>>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<String, VersionedEntry<String, byte[]>> next() {
                        final Map.Entry<String, ByteBuffer> entry = iterator.next();


                        final ByteBuffer byteBuffer = entry.getValue();
                        return new Entry<>(entry.getKey(), readEntry(entry.getKey(), byteBuffer));

                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }

    private VersionedEntry<String, byte[]> readEntry(final String key, ByteBuffer byteBuffer) {
        VersionedEntry<String, byte[]> versionedEntry = new VersionedEntry<>(key, null);


        byteBuffer.rewind();
        versionedEntry.setVersion(byteBuffer.getLong());
        versionedEntry.setCreateTimestamp(byteBuffer.getLong());
        versionedEntry.setUpdateTimestamp(byteBuffer.getLong());
        final int size = byteBuffer.getInt();
        byte[] bytes = new byte[size];
        byteBuffer.get(bytes);
        versionedEntry.setValue(bytes);

        return versionedEntry;
    }

    private void writeEntry(
            VersionedEntry<String, byte[]> versionedEntry, ByteBuffer byteBuffer) {



        byteBuffer.rewind();
        byteBuffer.putLong(versionedEntry.version());
        byteBuffer.putLong(versionedEntry.createdOn());
        byteBuffer.putLong(versionedEntry.updatedOn());
        byteBuffer.putInt(versionedEntry.getValue().length);
        byteBuffer.put(versionedEntry.getValue());

    }

    @Override
    public void close() {

    }

    @Override
    public Collection<String> loadAllKeys() {
        return searchMap.keySet();
    }

    @Override
    public VersionedEntry<String, byte[]> load(String key) {

        VersionedEntry<String, byte[]> entry = weakHashMap.get(key);

        if (entry == null) {

            ByteBuffer byteBuffer = map.get(key);
            entry =  readEntry(key, byteBuffer);
            weakHashMap.put(key, entry);
        }


        return entry;

    }



    @Override
    public void put(String key, VersionedEntry<String, byte[]> entry) {

        weakHashMap.put(key, entry);

        checkFlush();



        ByteBuffer byteBuffer = map.get(key);
        if (byteBuffer==null) {
            byteBuffer = ByteBuffer.allocate(255 + entry.getValue().length);
        }


        if (byteBuffer.capacity() < (entry.getValue().length + 32)) {
            byteBuffer = ByteBuffer.allocateDirect(32 + PADDING + entry.getValue().length);
        }

        writeEntry(entry, byteBuffer);

        map.put(key, byteBuffer);
        searchMap.put(key, byteBuffer);

    }


    private static final int CHECK_EVERY_X_PUTS = 10_000;
    private long lastFlushTime = Timer.timer().now();
    int numberOfPuts;
    private void checkFlush() {
        numberOfPuts++;
        if(numberOfPuts > CHECK_EVERY_X_PUTS) {
            long now = Timer.timer().now();
            long duration = now - lastFlushTime;
            numberOfPuts=0;
            if (duration > (60 * 1_000 * 60)) {
                if (this.weakHashMap.size()>10_000_000) {
                    this.weakHashMap.clear();
                    lastFlushTime = now;
                }
            }
        }
    }

    @Override
    public void putAll(Map<String, VersionedEntry<String, byte[]>> values) {


        for (Map.Entry<String, VersionedEntry<String, byte[]>> entry : values.entrySet()) {

            put(entry.getKey(), entry.getValue());
        }

    }

    @Override
    public Map<String, VersionedEntry<String, byte[]>> loadAllByKeys(Collection<String> keys) {
        LazyMap outputMap = new LazyMap(keys.size());

        for (String key : keys) {

            outputMap.put(key, load(key));
        }

        return (Map<String, VersionedEntry<String, byte[]>>) (Object) outputMap;
    }

    @Override
    public KeyValueIterable<String, VersionedEntry<String, byte[]>> loadAll() {

        final Iterator<Map.Entry<String, ByteBuffer>> iterator = searchMap.entrySet().iterator();

        return new KeyValueIterable<String, VersionedEntry<String, byte[]>>() {
            @Override
            public void close() {

            }

            @Override
            public Iterator<Entry<String, VersionedEntry<String, byte[]>>> iterator() {
                return new Iterator<Entry<String, VersionedEntry<String, byte[]>>>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<String, VersionedEntry<String, byte[]>> next() {
                        final Map.Entry<String, ByteBuffer> entry = iterator.next();


                        final ByteBuffer byteBuffer = entry.getValue();
                        return new Entry<>(entry.getKey(), readEntry(entry.getKey(), byteBuffer));

                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };

    }

    @Override
    public List<VersionKey> loadAllVersionInfoByKeys(Collection<String> keys) {

        List<VersionKey> versionKeys = new ArrayList<>(keys.size());

        for (String key : keys) {

            VersionKey versionKey = readVersion(key);
            versionKeys.add(versionKey);

        }
        return versionKeys;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public VersionKey loadVersion(String key) {

        VersionKey versionKey = readVersion(key);
        return versionKey;
    }


    private VersionKey readVersion(final String key) {

        final VersionedEntry<String, byte[]> entry = weakHashMap.get(key);


        if (entry!=null) {

            return new VersionKey(key, entry.version(), entry.updatedOn(), entry.createdOn(), entry.getValue().length);
        } else {

            final ByteBuffer byteBuffer = map.get(key);

            if (byteBuffer == null) {
                return VersionKey.notFound(key);
            }

            byteBuffer.rewind();
            long version = byteBuffer.getLong();
            long createTime = byteBuffer.getLong();
            long updateTimestamp = byteBuffer.getLong();
            final int size = byteBuffer.getInt();

            return new VersionKey(key, version, updateTimestamp, createTime, size);
        }

    }
}
