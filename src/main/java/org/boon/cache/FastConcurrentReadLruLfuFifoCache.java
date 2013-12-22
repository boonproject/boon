package org.boon.cache;

import org.boon.core.timer.TimeKeeper;
import org.boon.core.timer.TimeKeeperBasic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class FastConcurrentReadLruLfuFifoCache<KEY, VALUE> implements Cache<KEY, VALUE> {


    private final ConcurrentHashMap<KEY, CacheEntry<KEY, VALUE>> map = new ConcurrentHashMap<>();

    private final SortableConcurrentList<CacheEntry<KEY, VALUE>> list;
    private final int evictSize;
    private final AtomicInteger count = new AtomicInteger();
    private final CacheType type;

    private final TimeKeeper timeKeeper;

    public FastConcurrentReadLruLfuFifoCache ( int evictSize ) {
        this.evictSize = ( int ) ( evictSize + ( evictSize * 0.20f ) );
        list = new SortableConcurrentList<>();
        this.type = CacheType.LFU;
        timeKeeper = new TimeKeeperBasic();

    }


    public FastConcurrentReadLruLfuFifoCache ( int evictSize, Tradeoffs tradeoffs, CacheType type ) {
        this.evictSize = ( int ) ( evictSize + ( evictSize * 0.20f ) );

        this.type = type;

        if ( tradeoffs == Tradeoffs.FAST_REMOVE ) {
            list = new SortableConcurrentList<>( new LinkedList<CacheEntry<KEY, VALUE>>() );
        } else if ( tradeoffs == Tradeoffs.FAST_SORT ) {
            list = new SortableConcurrentList<>( new ArrayList<CacheEntry<KEY, VALUE>>() );
        } else {
            list = new SortableConcurrentList<>();
        }


        timeKeeper = new TimeKeeperBasic();

    }

    /**
     * Just for testing
     *
     * @param evictSize
     * @param type
     */
    FastConcurrentReadLruLfuFifoCache ( boolean test, int evictSize, CacheType type ) {
        this.evictSize = ( int ) ( evictSize + ( evictSize * 0.20f ) );
        list = new SortableConcurrentList<>();
        this.type = type;

        timeKeeper = new TimeKeeper() {
            int i;

            @Override
            public long time () {
                return System.currentTimeMillis() + i++;
            }
        };

    }

    public VALUE get ( KEY key ) {
        CacheEntry<KEY, VALUE> cacheEntry = map.get( key );
        if ( cacheEntry != null ) {
            cacheEntry.readCount.incrementAndGet();
            return cacheEntry.value;
        } else {
            return null;
        }

    }

    public VALUE getSilent ( KEY key ) {
        CacheEntry<KEY, VALUE> cacheEntry = map.get( key );
        if ( cacheEntry != null ) {
            return cacheEntry.value;
        } else {
            return null;
        }

    }

    @Override
    public void remove ( KEY key ) {
        CacheEntry<KEY, VALUE> entry = map.remove( key );
        if ( entry != null ) {
            list.remove( entry );
        }
    }

    public void put ( KEY key, VALUE value ) {
        CacheEntry<KEY, VALUE> entry = map.get( key );


        if ( entry == null ) {
            entry = new CacheEntry<>( key, value, order(), type, time() );
            list.add( entry );
            map.put( key, entry );
        } else {
            entry.readCount.incrementAndGet();
            entry.value = value;
        }
        evictIfNeeded();
    }

    private long time () {
        return this.timeKeeper.time();
    }


    private final int order () {
        int order = count.incrementAndGet();
        if ( order > Integer.MAX_VALUE - 100 ) {
            count.set( 0 );
        }
        return order;
    }

    private final void evictIfNeeded () {
        if ( list.size() > evictSize ) {

            final List<CacheEntry<KEY, VALUE>> killList = list.sortAndReturnPurgeList( 0.1f );

            for ( CacheEntry<KEY, VALUE> cacheEntry : killList ) {
                map.remove( cacheEntry.key );
            }
        }

    }

    public String toString () {
        return map.toString();
    }


    public int size () {
        return this.map.size();
    }
}
