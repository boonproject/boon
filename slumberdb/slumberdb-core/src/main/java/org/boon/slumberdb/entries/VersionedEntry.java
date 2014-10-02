package org.boon.slumberdb.entries;

import org.boon.concurrent.Timer;

import java.util.Map;

/**
 * Created by Richard on 9/23/14.
 */
public class VersionedEntry<K, V> extends Entry<K, V> {

    private long createTimestamp;
    private long updateTimestamp;
    private long version;

    public VersionedEntry(){
        
    }

    public VersionedEntry(Map.Entry<K, V> entry) {
        super(entry);
    }

    public VersionedEntry(K k, V v) {
        super(k, v);
    }


    public long updatedOn() {
        return updateTimestamp;
    }
    public long version() {
        return version;
    }
    public long createdOn() {
        return createTimestamp;
    }


    public VersionedEntry<K, V> setVersion(long version) {
        this.version = version;
        return this;
    }

    public VersionedEntry<K, V> setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
        return this;
    }

    public VersionedEntry<K, V> setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
        return this;
    }

    public VersionedEntry<K, V> setVersionMeta(VersionKey versionMeta) {

        if (versionMeta.version()==-1L) {
            this.setVersion(0L);
        } else {
            this.setVersion(versionMeta.version());
        }

        if (versionMeta.createdOn()==-1) {
            this.setCreateTimestamp(Timer.timer().now());
        } else {
            this.setCreateTimestamp(versionMeta.createdOn());
        }


        if (versionMeta.updatedOn()==-1) {
            this.setUpdateTimestamp(Timer.timer().now());
        } else {
            this.setUpdateTimestamp(versionMeta.updatedOn());
        }

        return this;
    }


    @Override
    public String toString() {
        return "VersionedEntry{" +
                "createTimestamp=" + createTimestamp +
                ", updateTimestamp=" + updateTimestamp +
                ", version=" + version +
                "} " + super.toString();
    }

    public VersionedEntry<K, V> value(V v) {
        this.setValue(v);
        return this;
    }
}
