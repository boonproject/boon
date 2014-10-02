package org.boon.slumberdb.service.results;

import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.stores.DataStoreSource;
import org.boon.Str;
import org.boon.json.annotations.JsonIgnore;

/**
 * Created by Richard on 9/4/14.
 */
public class StatCount extends Stat {


    @JsonIgnore
    private transient StatKey _key;

    private String area = null;


    @JsonIgnore
    private Number _count;

    private Action action;

    private long time;

    public StatCount(DataStoreSource source, Action action, int count) {
        this.source = source;
        this._count = count;
        this.action = action;

        this._key = new StatKey(action, source);
    }

    public StatCount(DataStoreSource source, Action action, String area, long count) {
        this.source = source;
        this._count = count;
        this.action = action;
        this.area = area;

        this._key = new StatKey(action, source, area);

    }


    public StatCount(long time, DataStoreSource source, Action action, String area, long count) {
        this.source = source;
        this._count = count;
        this.action = action;
        this.area = area;
        this.time = time;

        this._key = new StatKey(action, source, area);

    }

    public StatCount(long time, DataStoreSource source, Action action, String area, Number count) {
        this.source = source;
        this._count = count;
        this.action = action;
        this.area = area;
        this.time = time;

        this._key = new StatKey(action, source, area);

    }

    @Override
    public String toTextMessage() {
        return "count=count";
    }

    public Number count() {
        return _count;
    }

    public StatKey getKey() {

        if (_key == null) {
            _key = new StatKey(action, source, area);
        }
        return _key;
    }

    public Action action() {
        return action;
    }

    public String area() {
        return area;
    }

    public String toKey() {
        return Str.joinObjects('~', source, action, area);
    }


    public Number getCount() {
        return _count == null ? 0 : _count;
    }


    public void setCount(Number count) {
        this._count = count;
    }

    @Override
    public String toString() {
        return "StatCount{" +
                ", source='" + source + '\'' +
                ", area='" + area + '\'' +
                ", count=" + _count +
                ", action=" + action +
                ", time=" + time +
                "} " + super.toString();
    }
}
