package org.boon.collections;

import org.boon.core.reflection.Reflection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MultiMap<K, V> implements Iterable<Map.Entry<K, Collection<V>>> {

    private int initialSize = 10;
    private Map<K, Collection<V>> map = new ConcurrentHashMap();
    private Class<? extends Collection> collectionClass = ConcurrentLinkedHashSet.class;


    public MultiMap( Class<? extends Collection> collectionClass, int initialSize ) {
        this.collectionClass = collectionClass;
        this.initialSize = initialSize;
    }

    public MultiMap( Class<? extends Collection> collectionClass ) {
        this.collectionClass = collectionClass;
    }


    public MultiMap() {
    }

    @Override
    public Iterator<Map.Entry<K, Collection<V>>> iterator() {
        return map.entrySet().iterator();
    }


    public void put( K key, V v ) {
        Collection<V> collection = map.get( key );
        if ( collection == null ) {
            collection = createCollection( key );
        }
        collection.add( v );
    }



    public V get( K key ) {
        Collection<V> collection = map.get( key );
        if ( collection == null || collection.size()==0 ) {
            return null;
        }
        return collection.iterator().next();
    }



    public boolean containsKey( K key ) {
        Collection<V> collection = map.get( key );
        if ( collection == null || collection.size()==0 ) {
            return false;
        }
        return true;
    }



    public Collection<V> getAll( K key ) {
        Collection<V> collection = map.get( key );
        if ( collection == null  ) {
            return Collections.EMPTY_LIST;
        }
        return collection;
    }

    public boolean remove( K key, V v ) {
        Collection<V> collection = map.get( key );
        if (collection == null) {
            return false;
        }
        return collection.remove( v );
    }



    public boolean remove( K key ) {
        return map.remove( key ) != null;
    }

    private Collection<V> createCollection( K key ) {
        Collection<V> collection = ( Collection<V> ) Reflection.createCollection( collectionClass, initialSize );
        map.put( key, collection );
        return collection;
    }

    public Iterable<K> keySet() {
      return map.keySet();
    }

    public Iterable<Object> values() {

        List list = new ArrayList(  );
        Collection<Collection<V>> values = map.values();

        for (Collection c : values) {
            for (Object o : c) {
                list.add( o );
            }
        }
        return list;
    }
}
