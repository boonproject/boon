package org.boon.cache;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LruSimpleConcurrentCache<K, V> implements LruCache<K, V> {

    final CacheMap<K, V>[] cacheRegions;


    private static class CacheMap<K, V> extends LinkedHashMap<K, V> {
        private final ReadWriteLock readWriteLock;
        private final int limit;

<<<<<<< HEAD
        CacheMap ( final int limit, boolean fair ) {
=======
        CacheMap( final int limit, boolean fair ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            super ( 16, 0.75f, true );
            this.limit = limit;
            readWriteLock = new ReentrantReadWriteLock ( fair );

        }
<<<<<<< HEAD
=======

        protected boolean removeEldestEntry( final Map.Entry<K, V> eldest ) {
            return super.size () > limit;
        }
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        protected boolean removeEldestEntry ( final Map.Entry<K, V> eldest ) {
            return super.size () > limit;
        }

<<<<<<< HEAD

        @Override
        public V put ( K key, V value ) {
            readWriteLock.writeLock ().lock ();

=======
        @Override
        public V put( K key, V value ) {
            readWriteLock.writeLock ().lock ();

>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            V old;
            try {

                old = super.put ( key, value );
            } finally {
                readWriteLock.writeLock ().unlock ();
            }
            return old;

        }


        @Override
<<<<<<< HEAD
        public V get ( Object key ) {
=======
        public V get( Object key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
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
<<<<<<< HEAD
        public V remove ( Object key ) {
=======
        public V remove( Object key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

            readWriteLock.writeLock ().lock ();
            V value;

            try {

                value = super.remove ( key );
            } finally {
                readWriteLock.writeLock ().unlock ();
            }
            return value;

        }

<<<<<<< HEAD
        public V getSilent ( K key ) {
=======
        public V getSilent( K key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            readWriteLock.writeLock ().lock ();

            V value;

            try {

                value = this.get ( key );
                if ( value != null ) {
                    this.remove ( key );
                    this.put ( key, value );
                }
            } finally {
                readWriteLock.writeLock ().unlock ();
            }
            return value;

        }

<<<<<<< HEAD
        public int size () {
=======
        public int size() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            readWriteLock.readLock ().lock ();
            int size = -1;
            try {
                size = super.size ();
            } finally {
                readWriteLock.readLock ().unlock ();
            }
            return size;
        }

<<<<<<< HEAD
        public String toString () {
=======
        public String toString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
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

<<<<<<< HEAD
    public LruSimpleConcurrentCache ( final int limit, boolean fair ) {
        int cores = Runtime.getRuntime ().availableProcessors ();
        int stripeSize = cores < 2 ? 4 : cores * 2;
        stripeSize = roundUpToPowerOf2 ( stripeSize );
        cacheRegions = new CacheMap[ stripeSize ];
        for ( int index = 0; index < cacheRegions.length; index++ ) {
            cacheRegions[ index ] = new CacheMap<> ( limit / cacheRegions.length, fair );
        }
    }

    public LruSimpleConcurrentCache ( final int concurrency, final int limit, boolean fair ) {


        final int stripeSize = roundUpToPowerOf2 ( concurrency );
        cacheRegions = new CacheMap[ stripeSize ];
        for ( int index = 0; index < cacheRegions.length; index++ ) {
            cacheRegions[ index ] = new CacheMap<> ( limit / cacheRegions.length, fair );
        }
    }


    private CacheMap<K, V> map ( K key ) {
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
        for ( CacheMap<K, V> cache : cacheRegions ) {
            size += cache.size ();
        }
        return size;
    }

    public String toString () {

        StringBuilder builder = new StringBuilder ();
        for ( CacheMap<K, V> cache : cacheRegions ) {
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

    private static int randomHashSeed ( LruSimpleConcurrentCache instance ) {


        if ( useFastHash ) {
            return sun.misc.Hashing.randomHashSeed ( instance );
=======
    public LruSimpleConcurrentCache( final int limit, boolean fair ) {
        int cores = Runtime.getRuntime ().availableProcessors ();
        int stripeSize = cores < 2 ? 4 : cores * 2;
        cacheRegions = new CacheMap[stripeSize];
        for ( int index = 0; index < cacheRegions.length; index++ ) {
            cacheRegions[index] = new CacheMap<> ( limit / cacheRegions.length, fair );
        }
    }

    public LruSimpleConcurrentCache( final int concurrency, final int limit, boolean fair ) {

        cacheRegions = new CacheMap[concurrency];
        for ( int index = 0; index < cacheRegions.length; index++ ) {
            cacheRegions[index] = new CacheMap<> ( limit / cacheRegions.length, fair );
        }
    }

    private int stripeIndex( K key ) {
        int hashCode = key.hashCode () * 31;
        return hashCode % ( cacheRegions.length );
    }

    private CacheMap<K, V> map( K key ) {
        return cacheRegions[stripeIndex ( key )];
    }

    @Override
    public void put( K key, V value ) {

        map ( key ).put ( key, value );
    }

    @Override
    public V get( K key ) {
        return map ( key ).get ( key );
    }

    //For testing only
    @Override
    public V getSilent( K key ) {
        return map ( key ).getSilent ( key );

    }

    @Override
    public void remove( K key ) {
        map ( key ).remove ( key );
    }

    @Override
    public int size() {
        int size = 0;
        for ( CacheMap<K, V> cache : cacheRegions ) {
            size += cache.size ();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }
        return size;
    }

    public String toString() {

        StringBuilder builder = new StringBuilder ();
        for ( CacheMap<K, V> cache : cacheRegions ) {
            builder.append ( cache.toString () ).append ( '\n' );
        }

        return builder.toString ();
    }

        return 0;
    }


    final int hash ( Object k ) {
        int h = hashSeed;

        h ^= k.hashCode ();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
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