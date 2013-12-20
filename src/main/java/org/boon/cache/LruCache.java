package org.boon.cache;

public interface LruCache<KEY, VALUE> {
<<<<<<< HEAD
    void put ( KEY key, VALUE value );

    VALUE get ( KEY key );

    VALUE getSilent ( KEY key );

    void remove ( KEY key );

    int size ();
=======
    void put( KEY key, VALUE value );

    VALUE get( KEY key );

    VALUE getSilent( KEY key );

    void remove( KEY key );

    int size();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
}
