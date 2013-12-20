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

        CacheMap( final int limit, boolean fair ) {
            super ( 16, 0.75f, true );
            this.limit = limit;
            readWriteLock = new ReentrantReadWriteLock ( fair );

        }

        protected boolean removeEldestEntry( final Map.Entry<K, V> eldest ) {
            return super.size () > limit;
        }


        @Override
        public V put( K key, V value ) {
            readWriteLock.writeLock ().lock ();

            V old;
            try {

                old = super.put ( key, value );
            } finally {
                readWriteLock.writeLock ().unlock ();
            }
            return old;

        }


        @Override
        public V get( Object key ) {
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
        public V remove( Object key ) {

            readWriteLock.writeLock ().lock ();
            V value;

            try {

                value = super.remove ( key );
            } finally {
                readWriteLock.writeLock ().unlock ();
            }
            return value;

        }

        public V getSilent( K key ) {
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

        public int size() {
            readWriteLock.readLock ().lock ();
            int size = -1;
            try {
                size = super.size ();
            } finally {
                readWriteLock.readLock ().unlock ();
            }
            return size;
        }

        public String toString() {
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


}