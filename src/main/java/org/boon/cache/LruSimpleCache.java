package org.boon.cache;


import java.util.LinkedHashMap;
import java.util.Map;

public class LruSimpleCache<K, V> implements LruCache<K, V> {

    Map<K, V> map = new LinkedHashMap ();


<<<<<<< HEAD
    public LruSimpleCache ( final int limit ) {
        map = new LinkedHashMap<K, V> ( 16, 0.75f, true ) {
            @Override
            protected boolean removeEldestEntry ( final Map.Entry<K, V> eldest ) {
=======
    public LruSimpleCache( final int limit ) {
        map = new LinkedHashMap<K, V> ( 16, 0.75f, true ) {
            @Override
            protected boolean removeEldestEntry( final Map.Entry<K, V> eldest ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return super.size () > limit;
            }
        };
    }

    @Override
<<<<<<< HEAD
    public void put ( K key, V value ) {
=======
    public void put( K key, V value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        map.put ( key, value );
    }

    @Override
<<<<<<< HEAD
    public V get ( K key ) {
=======
    public V get( K key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return map.get ( key );
    }

    //For testing only
    @Override
<<<<<<< HEAD
    public V getSilent ( K key ) {
=======
    public V getSilent( K key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        V value = map.get ( key );
        if ( value != null ) {
            map.remove ( key );
            map.put ( key, value );
        }
        return value;
    }

    @Override
<<<<<<< HEAD
    public void remove ( K key ) {
=======
    public void remove( K key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
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
