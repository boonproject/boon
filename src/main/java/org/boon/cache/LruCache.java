package org.boon.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LruCache <KEY, VALUE> {


    private final ConcurrentHashMap<KEY, CacheEntry<KEY, VALUE>> map = new ConcurrentHashMap <>();

    private final SortableConcurrentList<CacheEntry<KEY, VALUE>> list = new SortableConcurrentList <>(  );

    private final int evictSize;

    public LruCache(int evictSize) {
        this.evictSize = (int)(evictSize + (evictSize * 0.20f) );
    }

    public VALUE get (KEY key) {
        CacheEntry<KEY, VALUE> cacheEntry = map.get ( key );
        cacheEntry.readCount++;
        return cacheEntry.value;

    }

    public void put (KEY key, VALUE value) {
        CacheEntry<KEY, VALUE> entry = map.get ( key );


        if (entry == null) {
             entry = new CacheEntry<> ();
             entry.value = value;
             entry.key = key;
             map.put(key, entry);
             list.add( entry );
        }  else {
            entry.readCount++;
            entry.value = value;
        }
        evictIfNeeded ( );
    }

    private final void evictIfNeeded() {
        if ( list.size () > evictSize ) {

            final List<CacheEntry<KEY,VALUE>> killList = list.sortAndReturnPurgeList ( 0.40f );

            for (CacheEntry<KEY, VALUE> cacheEntry : killList) {
                map.remove ( cacheEntry.key );
            }
        }

    }
}
