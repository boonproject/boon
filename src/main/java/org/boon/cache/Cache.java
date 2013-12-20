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


    public Cache( int evictSize ) {
        this.evictSize = ( int ) ( evictSize + ( evictSize * 0.20f ) );
        list = new SortableConcurrentList<> ();
        this.type = CacheType.LFU;
        timeSpeed = 5;

    }


    public Cache( int evictSize, CacheType type ) {
        this.evictSize = ( int ) ( evictSize + ( evictSize * 0.20f ) );
        list = new SortableConcurrentList<> ();
        this.type = type;
        timeSpeed = 5;

    }

    public Cache( int evictSize, Tradeoffs tradeoffs, CacheType type ) {
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
    public Cache( int timeSpeed, int evictSize, CacheType type ) {
        this.evictSize = ( int ) ( evictSize + ( evictSize * 0.20f ) );
        list = new SortableConcurrentList<> ();
        this.type = type;
        this.timeSpeed = timeSpeed;

    }

    public VALUE get( KEY key ) {
        CacheEntry<KEY, VALUE> cacheEntry = map.get ( key );
        if ( cacheEntry != null ) {
            cacheEntry.readCount.incrementAndGet ();
            return cacheEntry.value;
        } else {
            return null;
        }

    }

    public VALUE getSilent( KEY key ) {
        CacheEntry<KEY, VALUE> cacheEntry = map.get ( key );
        if ( cacheEntry != null ) {
            return cacheEntry.value;
        } else {
            return null;
        }

    }

    public void put( KEY key, VALUE value ) {
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

    private final long time() {

        callEvery10.incrementAndGet ();
        if ( callEvery10.get () > timeSpeed ) {
            callEvery10.set ( 0 );
            time.set ( System.currentTimeMillis () );
        }
        return time.get ();
    }

    private final int order() {
        int order = count.incrementAndGet ();
        if ( order > Integer.MAX_VALUE - 100 ) {
            count.set ( 0 );
        }
        return order;
    }

    private final void evictIfNeeded() {
        if ( list.size () > evictSize ) {

            final List<CacheEntry<KEY, VALUE>> killList = list.sortAndReturnPurgeList ( 0.1f );

            for ( CacheEntry<KEY, VALUE> cacheEntry : killList ) {
                map.remove ( cacheEntry.key );
            }
        }

    }

    public String toString() {
        return map.toString ();
    }


    public int size() {
        return this.map.size ();
    }
}
