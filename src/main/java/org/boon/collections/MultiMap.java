/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.collections;

import org.boon.core.Conversions;

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



    public Iterable<V> getAll( K key ) {
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
        Collection<V> collection = ( Collection<V> ) Conversions.createCollection( collectionClass, initialSize );
        map.put( key, collection );
        return collection;
    }

    public Iterable<K> keySet() {
      return map.keySet();
    }

    public Iterable<V> values() {

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
