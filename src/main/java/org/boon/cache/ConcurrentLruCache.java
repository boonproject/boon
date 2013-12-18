package org.boon.cache;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentLruCache<KEY, VALUE> implements LruCache<KEY,VALUE> {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock ();


    private final Map<KEY, VALUE> map = new ConcurrentHashMap<> ();
    private final Deque<KEY> queue = new LinkedList<> ();
    private final int limit;


    public ConcurrentLruCache ( int limit ) {
        this.limit = limit;
    }

    @Override
    public void put ( KEY key, VALUE value ) {
        VALUE oldValue = map.put ( key, value );
        if ( oldValue != null ) {
            removeThenAddKey ( key );
        } else {
            addKey ( key );
        }
        if (map.size () > limit) {
            map.remove ( removeLast() );
        }
    }


    @Override
    public VALUE get ( KEY key ) {
        removeThenAddKey ( key );
        return map.get ( key );
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

    private void removeFirstOccurrence(KEY key) {
        readWriteLock.writeLock ().lock ();
        try {
            queue.removeFirstOccurrence ( key );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }

    }


    @Override
    public VALUE getSilent ( KEY key ) {
        return map.get ( key );
    }

    @Override
    public void remove ( KEY key ) {
        removeFirstOccurrence ( key );
        map.remove ( key );
    }

    @Override
    public int size () {
        return map.size ();
    }

    public String toString () {
        return map.toString ();
    }
}
