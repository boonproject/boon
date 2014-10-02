package org.boon.slumberdb.entries;

/**
 * Created by Richard on 9/23/14.
 */
public class LoadedEntry <K, V>{

    VersionKey key;

    V value;


    public LoadedEntry(VersionKey key, V value) {
        this.key = key;
        this.value = value;
    }

    public VersionKey key() {
        return key;
    }

    public V value() {
        return value;
    }
}
