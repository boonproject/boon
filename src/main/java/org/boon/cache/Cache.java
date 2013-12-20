package org.boon.cache;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Cache<KEY, VALUE> {


    private final ConcurrentHashMap<KEY, CacheEntry<KEY, VALUE>> map = new ConcurrentHashMap<> ();

    private final SortableConcurrentList<CacheEntry<KEY, VALUE>> list;
    private final int evictSize;
    private final AtomicInteger count = new AtomicInteger ();
    private final CacheType type;
    private final int timeSpeed;


<<<<<<< HEAD
    public Cache ( int evictSize ) {
=======
    public Cache( int evictSize ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        this.evictSize = ( int ) ( evictSize + ( evictSize * 0.20f ) );
        list = new SortableConcurrentList<> ();
        this.type = CacheType.LFU;
        timeSpeed = 5;

    }


<<<<<<< HEAD
    public Cache ( int evictSize, CacheType type ) {
=======
    public Cache( int evictSize, CacheType type ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        this.evictSize = ( int ) ( evictSize + ( evictSize * 0.20f ) );
        list = new SortableConcurrentList<> ();
        this.type = type;
        timeSpeed = 5;

    }

<<<<<<< HEAD
    public Cache ( int evictSize, Tradeoffs tradeoffs, CacheType type ) {
=======
    public Cache( int evictSize, Tradeoffs tradeoffs, CacheType type ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        this.evictSize = ( int ) ( evictSize + ( evictSize * 0.20f ) );

        this.type = type;
        timeSpeed = 5;

        if ( tradeoffs == Tradeoffs.FAST_REMOVE ) {
            list = new SortableConcurrentList<> ( new LinkedList<CacheEntry<KEY, VALUE>> () );
        } else if ( tradeoffs == Tradeoffs.FAST_SORT ) {
            list = new SortableConcurrentList<> ( new ArrayList<CacheEntry<KEY, VALUE>> () );
        } else {
            list = new SortableConcurrentList<> ();
        }

    }

    /**
     * Just for testing
     *
     * @param timeSpeed
     * @param evictSize
     * @param type
     */
<<<<<<< HEAD
    public Cache ( int timeSpeed, int evictSize, CacheType type ) {
=======
    public Cache( int timeSpeed, int evictSize, CacheType type ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        this.evictSize = ( int ) ( evictSize + ( evictSize * 0.20f ) );
        list = new SortableConcurrentList<> ();
        this.type = type;
        this.timeSpeed = timeSpeed;

    }

<<<<<<< HEAD
    public VALUE get ( KEY key ) {
=======
    public VALUE get( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        CacheEntry<KEY, VALUE> cacheEntry = map.get ( key );
        if ( cacheEntry != null ) {
            cacheEntry.readCount.incrementAndGet ();
            return cacheEntry.value;
        } else {
            return null;
        }

    }

<<<<<<< HEAD
    public VALUE getSilent ( KEY key ) {
=======
    public VALUE getSilent( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        CacheEntry<KEY, VALUE> cacheEntry = map.get ( key );
        if ( cacheEntry != null ) {
            return cacheEntry.value;
        } else {
            return null;
        }

    }

<<<<<<< HEAD
    public void put ( KEY key, VALUE value ) {
=======
    public void put( KEY key, VALUE value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        CacheEntry<KEY, VALUE> entry = map.get ( key );


        if ( entry == null ) {
            entry = new CacheEntry<> ( key, value, order (), type, time () );
            list.add ( entry );
            map.put ( key, entry );
        } else {
            entry.readCount.incrementAndGet ();
            entry.value = value;
        }
        evictIfNeeded ();
    }


    //Override this if you have a system timer    TODO make plug-able
    private final AtomicInteger callEvery10 = new AtomicInteger ();
    private final AtomicLong time = new AtomicLong ();

<<<<<<< HEAD
    private final long time () {
=======
    private final long time() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        callEvery10.incrementAndGet ();
        if ( callEvery10.get () > timeSpeed ) {
            callEvery10.set ( 0 );
            time.set ( System.currentTimeMillis () );
        }
        return time.get ();
    }

<<<<<<< HEAD
    private final int order () {
=======
    private final int order() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        int order = count.incrementAndGet ();
        if ( order > Integer.MAX_VALUE - 100 ) {
            count.set ( 0 );
        }
        return order;
    }

<<<<<<< HEAD
    private final void evictIfNeeded () {
=======
    private final void evictIfNeeded() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( list.size () > evictSize ) {

            final List<CacheEntry<KEY, VALUE>> killList = list.sortAndReturnPurgeList ( 0.1f );

            for ( CacheEntry<KEY, VALUE> cacheEntry : killList ) {
                map.remove ( cacheEntry.key );
            }
        }

    }

<<<<<<< HEAD
    public String toString () {
=======
    public String toString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return map.toString ();
    }


<<<<<<< HEAD
    public int size () {
=======
    public int size() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.map.size ();
    }
}
