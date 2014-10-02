package org.boon.slumberdb.entries;


public class UpdateStatus {

    final boolean successful;
    final VersionKey versionKey;

    public final static UpdateStatus SUCCESS = new UpdateStatus(true, null);



    public UpdateStatus(boolean successful, VersionKey versionKey) {
        this.successful = successful;
        this.versionKey = versionKey;
    }

    public UpdateStatus(VersionKey versionKey) {
        this.successful = false;
        this.versionKey = versionKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateStatus)) return false;

        UpdateStatus that = (UpdateStatus) o;

        if (successful != that.successful) return false;
        if (versionKey != null ? !versionKey.equals(that.versionKey) : that.versionKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (successful ? 1 : 0);
        result = 31 * result + (versionKey != null ? versionKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UpdateStatus{" +
                "successful=" + successful +
                ", versionKey=" + versionKey +
                '}';
    }

    public boolean isSuccessful() {
        return successful;
    }

    public VersionKey versionKey() {
        return versionKey;
    }
}
