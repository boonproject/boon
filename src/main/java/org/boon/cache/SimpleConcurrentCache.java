package org.boon.cache;


import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleConcurrentCache<K, V> implements Cache<K, V> {

    final SimpleCache<K, V>[] cacheRegions;


    private static class SimpleThreadSafeCache<K, V> extends SimpleCache<K, V> {
        private final ReadWriteLock readWriteLock;

        SimpleThreadSafeCache ( final int limit, CacheType type, boolean fair ) {

            super ( limit, type );
            readWriteLock = new ReentrantReadWriteLock ( fair );
        }


        @Override
        public void put ( K key, V value ) {
            readWriteLock.writeLock ().lock ();
            try {

                super.put ( key, value );
            } finally {
                readWriteLock.writeLock ().unlock ();
            }
        }


        @Override
        public V get ( K key ) {
            readWriteLock.writeLock ().lock ();
            V value;

            try {

                value = super.get ( key );
            } finally {
                readWriteLock.writeLock ().unlock ();
            }
            return value;
        }

        @Override
        public void remove ( K key ) {

            readWriteLock.writeLock ().lock ();

            try {

                super.remove ( key );
            } finally {
                readWriteLock.writeLock ().unlock ();
            }

        }

        public V getSilent ( K key ) {
            readWriteLock.writeLock ().lock ();

            V value;


            try {
                value = super.getSilent ( key );
            } finally {
                readWriteLock.writeLock ().unlock ();
            }

            return value;

        }

        public int size () {
            readWriteLock.readLock ().lock ();
            int size = -1;
            try {
                size = super.size ();
            } finally {
                readWriteLock.readLock ().unlock ();
            }
            return size;
        }

        public String toString () {
            readWriteLock.readLock ().lock ();
            String str;
            try {
                str = super.toString ();
            } finally {
                readWriteLock.readLock ().unlock ();
            }
            return str;
        }


    }


    public SimpleConcurrentCache ( final int limit ) {
        this ( limit, false, CacheType.LRU );
    }

    public SimpleConcurrentCache ( final int limit, CacheType type ) {
        this ( limit, false, type );
    }

    public SimpleConcurrentCache ( final int limit, boolean fair, CacheType type ) {
        int cores = Runtime.getRuntime ().availableProcessors ();
        int stripeSize = cores < 2 ? 4 : cores * 2;
        stripeSize = roundUpToPowerOf2 ( stripeSize );
        cacheRegions = new SimpleCache[ stripeSize ];
        for ( int index = 0; index < cacheRegions.length; index++ ) {
            cacheRegions[ index ] = new SimpleThreadSafeCache<> ( limit / cacheRegions.length, type, fair );
        }
    }

    public SimpleConcurrentCache ( final int concurrency, final int limit, boolean fair, CacheType type ) {


        final int stripeSize = roundUpToPowerOf2 ( concurrency );
        cacheRegions = new SimpleCache[ stripeSize ];
        for ( int index = 0; index < cacheRegions.length; index++ ) {
            cacheRegions[ index ] = new SimpleThreadSafeCache<> ( limit / cacheRegions.length, type, fair );
        }
    }

    public SimpleConcurrentCache ( final int concurrency, final int limit, boolean fair ) {


        final int stripeSize = roundUpToPowerOf2 ( concurrency );
        cacheRegions = new SimpleCache[ stripeSize ];
        for ( int index = 0; index < cacheRegions.length; index++ ) {
            cacheRegions[ index ] = new SimpleThreadSafeCache<> ( limit / cacheRegions.length, CacheType.LRU, fair );
        }
    }

    private SimpleCache<K, V> map ( K key ) {
        return cacheRegions[ stripeIndex ( key ) ];
    }

    @Override
    public void put ( K key, V value ) {

        map ( key ).put ( key, value );
    }

    @Override
    public V get ( K key ) {
        return map ( key ).get ( key );
    }

    //For testing only
    @Override
    public V getSilent ( K key ) {
        return map ( key ).getSilent ( key );

    }

    @Override
    public void remove ( K key ) {
        map ( key ).remove ( key );
    }

    @Override
    public int size () {
        int size = 0;
        for ( SimpleCache<K, V> cache : cacheRegions ) {
            size += cache.size ();
        }
        return size;
    }

    public String toString () {

        StringBuilder builder = new StringBuilder ();
        for ( SimpleCache<K, V> cache : cacheRegions ) {
            builder.append ( cache.toString () ).append ( '\n' );
        }

        return builder.toString ();
    }


    private static final boolean useFastHash;

    static {

        boolean yes;
        try {
            Class.forName ( "sun.misc.Hashing" );
            yes = true;
        } catch ( Exception ex ) {
            yes = false;
        }

        useFastHash = yes;
    }

    private transient final int hashSeed = randomHashSeed ( this );

    private static int randomHashSeed ( SimpleConcurrentCache instance ) {


        if ( useFastHash ) {
            return sun.misc.Hashing.randomHashSeed ( instance );
        }

        return 0;
    }


    private final int hash ( Object k ) {
        int h = hashSeed;

        h ^= k.hashCode ();

        h ^= ( h >>> 20 ) ^ ( h >>> 12 );
        return h ^ ( h >>> 7 ) ^ ( h >>> 4 );
    }


    /**
     * Returns index for hash code h.
     */
    static int indexFor ( int h, int length ) {
        return h & ( length - 1 );
    }


    private static int roundUpToPowerOf2 ( int number ) {
        int rounded = number >= 1_000
                ? 1_000
                : ( rounded = Integer.highestOneBit ( number ) ) != 0
                ? ( Integer.bitCount ( number ) > 1 ) ? rounded << 1 : rounded
                : 1;

        return rounded;
    }


    private int stripeIndex ( K key ) {
        return indexFor ( hash ( key ), cacheRegions.length );
    }


}