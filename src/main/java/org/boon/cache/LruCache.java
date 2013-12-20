package org.boon.cache;

public interface LruCache<KEY, VALUE> {
    void put ( KEY key, VALUE value );

    VALUE get ( KEY key );

    VALUE getSilent ( KEY key );

    void remove ( KEY key );

    int size ();
}
