package org.boon.cache;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentLruCache<KEY, VALUE> implements LruCache<KEY, VALUE> {

    private final ReentrantLock lock = new ReentrantLock ();


    private final Map<KEY, VALUE> map = new ConcurrentHashMap<> ();
    private final Deque<KEY> queue = new LinkedList<> ();
    private final int limit;


<<<<<<< HEAD
    public ConcurrentLruCache ( int limit ) {
=======
    public ConcurrentLruCache( int limit ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        this.limit = limit;
    }

    @Override
<<<<<<< HEAD
    public void put ( KEY key, VALUE value ) {
=======
    public void put( KEY key, VALUE value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        VALUE oldValue = map.put ( key, value );
        if ( oldValue != null ) {
            removeThenAddKey ( key );
        } else {
            addKey ( key );
        }
        if ( map.size () > limit ) {
            map.remove ( removeLast () );
        }
    }


    @Override
<<<<<<< HEAD
    public VALUE get ( KEY key ) {
=======
    public VALUE get( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        removeThenAddKey ( key );
        return map.get ( key );
    }


<<<<<<< HEAD
    private void addKey ( KEY key ) {
=======
    private void addKey( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        lock.lock ();
        try {
            queue.addFirst ( key );
        } finally {
            lock.unlock ();
        }


    }

<<<<<<< HEAD
    private KEY removeLast () {
=======
    private KEY removeLast() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        lock.lock ();
        try {
            final KEY removedKey = queue.removeLast ();
            return removedKey;
        } finally {
            lock.unlock ();
        }
    }

<<<<<<< HEAD
    private void removeThenAddKey ( KEY key ) {
=======
    private void removeThenAddKey( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        lock.lock ();
        try {
            queue.removeFirstOccurrence ( key );
            queue.addFirst ( key );
        } finally {
            lock.unlock ();
        }

    }

<<<<<<< HEAD
    private void removeFirstOccurrence ( KEY key ) {
=======
    private void removeFirstOccurrence( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        lock.lock ();
        try {
            queue.removeFirstOccurrence ( key );
        } finally {
            lock.unlock ();
        }

    }


    @Override
<<<<<<< HEAD
    public VALUE getSilent ( KEY key ) {
=======
    public VALUE getSilent( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return map.get ( key );
    }

    @Override
<<<<<<< HEAD
    public void remove ( KEY key ) {
=======
    public void remove( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        removeFirstOccurrence ( key );
        map.remove ( key );
    }

    @Override
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
