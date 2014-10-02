package org.boon.slumberdb.service.protocol.requests;

/**
 * Created by Richard on 9/3/14.
 */
public class ObjectId {


    protected String key;
    private long version;
    private long updateTimeStamp;
    private long createTimeStamp;

    private int objectHash;

    public String key() {
        return key;
    }

    void key(String key) {
        this.key = key;
    }

    public long version() {
        return version;
    }

    void version(long objectVersion) {
        this.version = objectVersion;
    }

    public long updateTimeStamp() {
        return updateTimeStamp;
    }

    void updateTimeStamp(long updateTimeStamp) {
        this.updateTimeStamp = updateTimeStamp;
    }

    public long createTimeStamp() {
        return createTimeStamp;
    }

    void createTimeStamp(long createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }


    public int getObjectHash() {
        return objectHash;
    }

    public void setObjectHash(int objectHash) {
        this.objectHash = objectHash;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectId)) return false;

        ObjectId objectId = (ObjectId) o;

        if (createTimeStamp != objectId.createTimeStamp) return false;
        if (objectHash != objectId.objectHash) return false;
        if (updateTimeStamp != objectId.updateTimeStamp) return false;
        if (version != objectId.version) return false;
        if (key != null ? !key.equals(objectId.key) : objectId.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (int) (updateTimeStamp ^ (updateTimeStamp >>> 32));
        result = 31 * result + (int) (createTimeStamp ^ (createTimeStamp >>> 32));
        result = 31 * result + objectHash;
        return result;
    }

    @Override
    public String toString() {
        return "ObjectId{" +
                "key='" + key + '\'' +
                ", version=" + version +
                ", updateTimeStamp=" + updateTimeStamp +
                ", createTimeStamp=" + createTimeStamp +
                ", objectHash=" + objectHash +
                '}';
    }
}
