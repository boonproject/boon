package org.boon.cache;


import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LruCache<KEY, VALUE> {

    Map<KEY, VALUE> map = new HashMap<> ();
    Deque<KEY> queue = new LinkedList<> ();
    final int limit;


    public LruCache ( int limit ) {
        this.limit = limit;
    }

    public void put ( KEY key, VALUE value ) {
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


    public VALUE get ( KEY key ) {

        /* Frequently used keys will be at the top so the search could be fast.*/
        queue.removeFirstOccurrence ( key );
        queue.addFirst ( key );
        return map.get ( key );
    }


    public VALUE getSilent ( KEY key ) {

        return map.get ( key );
    }

    public void remove ( KEY key ) {

        /* Frequently used keys will be at the top so the search could be fast.*/
        queue.removeFirstOccurrence ( key );
        map.remove ( key );
    }

    public int size () {
        return map.size ();
    }

    public String toString() {
        return map.toString ();
    }
}
