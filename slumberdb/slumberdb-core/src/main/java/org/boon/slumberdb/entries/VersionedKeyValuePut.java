package org.boon.slumberdb.entries;

/**
 * Created by Richard on 9/23/14.
 */
public class VersionedKeyValuePut <V> {

    private final VersionKey key;
    private final V value;

    public VersionedKeyValuePut(VersionKey key, V value) {
        this.key = key;
        this.value = value;
    }

    public VersionKey getVersionKey() {
        return key;
    }


    public String getKey() {
        return key.key();
    }

    public V getValue() {
        return value;
    }


}
