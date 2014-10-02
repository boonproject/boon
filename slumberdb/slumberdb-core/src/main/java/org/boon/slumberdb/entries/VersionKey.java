package org.boon.slumberdb.entries;

/**
 * Created by Richard on 9/23/14.
 */
public class VersionKey implements Comparable<VersionKey>{


    private final String key;
    private final long createTimestamp;
    private final long updateTimestamp;
    private final long version;
    private final int size;

    public static VersionKey notFound(String key) {
         return new VersionKey(key, -1, 0L, 0);
    }

    public VersionKey(String key, long version, long updateTimestamp, long createTimestamp, int size) {
        this.key = key;
        this.updateTimestamp = updateTimestamp;
        this.createTimestamp = createTimestamp;
        this.version = version;
        this.size = size;
    }

    public VersionKey(String key, long version, long updateTimestamp, long createTimestamp) {
        this.key = key;
        this.updateTimestamp = updateTimestamp;
        this.version = version;
        this.createTimestamp = createTimestamp;
        this.size=-1;
    }


    public String key() {
        return key;
    }


    public long updatedOn() {
        return updateTimestamp;
    }

    public long version() {
        return version;
    }


    public int size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VersionKey)) return false;

        VersionKey that = (VersionKey) o;

        if (updateTimestamp != that.updateTimestamp) return false;
        if (version != that.version) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (int) (updateTimestamp ^ (updateTimestamp >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    @Override
    public int compareTo(VersionKey that) {
        if (this.version > that.version) {
            return 1;
        } else if (this.version < that.version) {
            return -1;
        } else {

            if (updateTimestamp == -1 || that.updateTimestamp == -1) {
                return 0;
            }

            if (this.updateTimestamp > that.updateTimestamp) {
                return 1;
            } else if (this.updateTimestamp < that.updateTimestamp) {
                return -1;
            } else {

               return 0;
            }
        }
    }

    public long createdOn() {
        return createTimestamp;
    }

    @Override
    public String toString() {
        return "VersionKey{" +
                "key='" + key + '\'' +
                ", createTimestamp=" + createTimestamp +
                ", updateTimestamp=" + updateTimestamp +
                ", version=" + version +
                ", size=" + size +
                '}';
    }
}
