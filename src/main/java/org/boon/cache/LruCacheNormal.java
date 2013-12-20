package org.boon.cache;


import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LruCacheNormal<KEY, VALUE> implements LruCache<KEY, VALUE> {

    Map<KEY, VALUE> map = new HashMap<> ();
    Deque<KEY> queue = new LinkedList<> ();
    final int limit;


<<<<<<< HEAD
    public LruCacheNormal ( int limit ) {
        this.limit = limit;
    }

    public void put ( KEY key, VALUE value ) {
=======
    public LruCacheNormal( int limit ) {
        this.limit = limit;
    }

    public void put( KEY key, VALUE value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        VALUE oldValue = map.put ( key, value );

            /*If there was already an object under this key,
             then remove it before adding to queue
             Frequently used keys will be at the top so the search could be fast.
             */
        if ( oldValue != null ) {
            queue.removeFirstOccurrence ( key );
        }
        queue.addFirst ( key );

        if ( map.size () > limit ) {
            final KEY removedKey = queue.removeLast ();
            map.remove ( removedKey );
        }

    }


<<<<<<< HEAD
    public VALUE get ( KEY key ) {
=======
    public VALUE get( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

            /* Frequently used keys will be at the top so the search could be fast.*/
        queue.removeFirstOccurrence ( key );
        queue.addFirst ( key );
        return map.get ( key );
    }


<<<<<<< HEAD
    public VALUE getSilent ( KEY key ) {
=======
    public VALUE getSilent( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        return map.get ( key );
    }

<<<<<<< HEAD
    public void remove ( KEY key ) {
=======
    public void remove( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

            /* Frequently used keys will be at the top so the search could be fast.*/
        queue.removeFirstOccurrence ( key );
        map.remove ( key );
    }

<<<<<<< HEAD
    public int size () {
        return map.size ();
    }

    public String toString () {
=======
    public int size() {
        return map.size ();
    }

    public String toString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return map.toString ();
    }
}
