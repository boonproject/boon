package org.boon.slumberdb.noop;


import org.boon.slumberdb.KeyValueIterable;
import org.boon.slumberdb.StringKeyValueStore;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Richard on 9/3/14.
 */
public class StringKeyValueStoreNoOp implements StringKeyValueStore {


    public static final StringKeyValueStoreNoOp SINGLETON = new StringKeyValueStoreNoOp();

    @Override
    public void put(String key, String value) {

    }

    @Override
    public void putAll(Map<String, String> values) {

    }

    @Override
    public void removeAll(Iterable<String> keys) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public KeyValueIterable<String, String> search(String startKey) {
        return StringStringKeyValueIterableNoOp.SINGLETON;
    }

    @Override
    public KeyValueIterable<String, String> loadAll() {
        return StringStringKeyValueIterableNoOp.SINGLETON;
    }

    @Override
    public Collection<String> loadAllKeys() {
        return Collections.emptyList();
    }

    @Override
    public String load(String key) {
        return null;
    }

    @Override
    public Map<String, String> loadAllByKeys(Collection<String> keys) {
        return Collections.emptyMap();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isClosed() {
        return false;
    }
}
