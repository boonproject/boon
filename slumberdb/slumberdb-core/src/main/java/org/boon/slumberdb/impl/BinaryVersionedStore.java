package org.boon.slumberdb.impl;

import org.boon.concurrent.Timer;
import org.boon.slumberdb.*;
import org.boon.slumberdb.entries.*;
import org.boon.slumberdb.spi.VersionedStorageProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Richard on 9/23/14.
 */
public class BinaryVersionedStore  implements KeyValueStoreWithVersion<String, byte[], VersionedEntry<String, byte[]>> {

    private final VersionedStorageProvider baseVersionedStorage;

    public BinaryVersionedStore(VersionedStorageProvider baseVersionedStorage) {
        this.baseVersionedStorage = baseVersionedStorage;
    }

    @Override
    public void put(String key, VersionedEntry<String, byte[]> value) {
            baseVersionedStorage.put(key, value);
    }

    @Override
    public void putAll(Map<String, VersionedEntry<String, byte[]>> values) {

        baseVersionedStorage.putAll(values);
    }

    @Override
    public void removeAll(Iterable<String> keys) {

        baseVersionedStorage.removeAll(keys);
    }

    @Override
    public void remove(String key) {

        baseVersionedStorage.remove(key);
    }

    @Override
    public KeyValueIterable<String, VersionedEntry<String, byte[]>> search(String startKey) {
        return baseVersionedStorage.search(startKey);
    }

    @Override
    public KeyValueIterable<String, VersionedEntry<String, byte[]>> loadAll() {
        return baseVersionedStorage.loadAll();
    }

    @Override
    public Collection<String> loadAllKeys() {
        return baseVersionedStorage.loadAllKeys();
    }

    @Override
    public VersionedEntry<String, byte[]> load(String key) {
        return baseVersionedStorage.load(key);
    }

    @Override
    public Map<String, VersionedEntry<String, byte[]>> loadAllByKeys(Collection<String> keys) {
        return baseVersionedStorage.loadAllByKeys(keys);
    }

    @Override
    public void close() {
        baseVersionedStorage.close();
    }

    @Override
    public boolean isOpen() {
        return baseVersionedStorage.isOpen();
    }

    @Override
    public boolean isClosed() {
        return baseVersionedStorage.isClosed();
    }

    private VersionKey loadVersion(String key) {

        return baseVersionedStorage.loadVersion(key);
    }


    @Override
    public LoadedEntry<String, byte[]> load(String key, long version) {
        final VersionKey versionKey = this.loadVersion(key);

        if (version != versionKey.version()) {
            final VersionedEntry<String, byte[]> load = this.load(key);
            return new LoadedEntry<>(versionKey, load == null ? null : load.getValue());
        } else {
            return new LoadedEntry<>(versionKey, null);
        }

    }

    @Override
    public UpdateStatus put(String key, long version,  byte[] value) {

        final VersionKey versionKey = loadVersion(key);

        if (version > versionKey.version()) {

            VersionedEntry<String, byte[]> entry = new VersionedEntry<>(key, value);

            entry.setVersionMeta(versionKey);

            entry.setVersion(version);

            entry.setUpdateTimestamp(Timer.timer().now());

            this.put(key, entry);

            return UpdateStatus.SUCCESS;
        } else {
            return new UpdateStatus(versionKey);
        }


    }

    @Override
    public void set(String key, byte[] value) {

        VersionedEntry<String, byte[]> load = baseVersionedStorage.load(key);

        long now = Timer.timer().now();

        if (load!=null) {
            load.setUpdateTimestamp(now);
            load.setVersion(load.version()+1);
        } else {

            load = new VersionedEntry<>(key, value);
            load.setCreateTimestamp(now);
            load.setUpdateTimestamp(now);
            load.setVersion(0);
        }
    }

    @Override
    public byte[] get(String key) {
        final VersionedEntry<String, byte[]> load = baseVersionedStorage.load(key);
        return load == null ? null : load.getValue();
    }

    @Override
    public UpdateStatus put(String key, long version, long updatedTime, byte[] value) {

        final VersionKey versionKey = loadVersion(key);

        if (version > versionKey.version() && updatedTime > versionKey.updatedOn()) {

            VersionedEntry<String, byte[]> entry = new VersionedEntry<>(key, value);

            entry.setVersionMeta(versionKey);

            entry.setVersion(version);
            entry.setUpdateTimestamp(updatedTime);

            this.put(key, entry);


            return UpdateStatus.SUCCESS;
        } else {
            return new UpdateStatus(versionKey);
        }


    }

    @Override
    public UpdateStatus put(VersionKey key,  byte[] value) {

        final VersionKey versionKey = loadVersion(key.key());

        if (key.compareTo(versionKey) > 0) {

            VersionedEntry<String, byte[]> entry = new VersionedEntry<>(key.key(), value);

            entry.setVersionMeta(versionKey);

            this.put(key.key(), entry);

            return UpdateStatus.SUCCESS;
        } else {
            return new UpdateStatus(versionKey);
        }
    }

    @Override
    public Collection<UpdateStatus> putAll(List<VersionedKeyValuePut<byte[]>> list) {

        List<String> keys = new ArrayList<>(list.size());
        List<UpdateStatus> status = new ArrayList<>(list.size());

        for (VersionedKeyValuePut put : list) {
            keys.add(put.getKey());
        }

        final List<VersionKey> versionKeys =
                baseVersionedStorage.loadAllVersionInfoByKeys(keys);


        for (int index = 0; index < list.size(); index++) {

            VersionKey keyInMemoryNow = versionKeys.get(index);
            VersionedKeyValuePut<byte[]> keyValuePut = list.get(index);

            if (keyValuePut.getVersionKey().compareTo(keyInMemoryNow) > 0) {

                VersionKey versionKey = keyValuePut.getVersionKey();
                byte[] value = keyValuePut.getValue();

                VersionedEntry<String, byte[]> versionedEntry =
                        new VersionedEntry<>(versionKey.key(), value);
                versionedEntry.setVersionMeta(versionKey);
                versionedEntry.setValue(value);

                this.put(versionKey.key(), versionedEntry);
            } else {
                status.add(new UpdateStatus(keyInMemoryNow));
            }

        }

        return status;

    }

}
