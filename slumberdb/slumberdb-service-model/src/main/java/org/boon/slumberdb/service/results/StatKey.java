package org.boon.slumberdb.service.results;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.stores.DataStoreSource;

/**
 * Created by Richard on 9/9/14.
 */
public class StatKey {

    Action action;
    DataStoreSource source;
    String area;

    public StatKey(Action action, DataStoreSource source) {
        this.action = action;
        this.source = source;
    }

    public StatKey(Action action, DataStoreSource source, String area) {
        this.action = action;
        this.source = source;
        this.area = area;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatKey)) return false;

        StatKey statKey = (StatKey) o;

        if (action != statKey.action) return false;
        if (area != null ? !area.equals(statKey.area) : statKey.area != null) return false;
        if (source != statKey.source) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (area != null ? area.hashCode() : 0);
        return result;
    }
}
