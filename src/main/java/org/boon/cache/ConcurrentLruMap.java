package org.boon.cache;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentLruMap<KEY, VALUE> {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock ();


    Map<KEY, VALUE> map = new ConcurrentHashMap<> ();
    Deque<KEY> queue = new LinkedList<> ();
    final int limit;


    public ConcurrentLruMap ( int limit ) {
        this.limit = limit;
    }

    public void put ( KEY key, VALUE value ) {
        VALUE oldValue = map.put ( key, value );


        if ( oldValue != null ) {
            removeThenAddKey ( key );
        } else {
            addKey ( key );
        }

        map.remove ( removeLast() );

    }

    private void addKey(KEY key) {
        readWriteLock.writeLock ().lock ();
        try {
            queue.addFirst ( key );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }


    }

    private KEY removeLast( ) {
        readWriteLock.writeLock ().lock ();
        try {
            final KEY removedKey = queue.removeLast ();
            return removedKey;
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
    }

    private void removeThenAddKey(KEY key) {
        readWriteLock.writeLock ().lock ();
        try {
            queue.removeFirstOccurrence ( key );
            queue.addFirst ( key );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }

    }


    public VALUE get ( KEY key ) {
        removeThenAddKey ( key );
        return map.get ( key );
    }


    public VALUE getSilent ( KEY key ) {
        return map.get ( key );
    }

    public void remove ( KEY key ) {
        queue.removeFirstOccurrence ( key );
        map.remove ( key );
    }

    public int size () {
        return map.size ();
    }

    public String toString () {
        return map.toString ();
    }
}
