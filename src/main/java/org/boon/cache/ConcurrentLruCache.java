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

package org.boon.cache;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentLruCache<KEY, VALUE> implements Cache<KEY, VALUE> {

    private final ReentrantLock lock = new ReentrantLock();


    private final Map<KEY, VALUE> map = new ConcurrentHashMap<>();
    private final Deque<KEY> queue = new LinkedList<>();
    private final int limit;


    public ConcurrentLruCache( int limit ) {
        this.limit = limit;
    }

    @Override
    public void put( KEY key, VALUE value ) {
        VALUE oldValue = map.put( key, value );
        if ( oldValue != null ) {
            removeThenAddKey( key );
        } else {
            addKey( key );
        }
        if ( map.size() > limit ) {
            map.remove( removeLast() );
        }
    }


    @Override
    public VALUE get( KEY key ) {
        removeThenAddKey( key );
        return map.get( key );
    }


    private void addKey( KEY key ) {
        lock.lock();
        try {
            queue.addFirst( key );
        } finally {
            lock.unlock();
        }


    }

    private KEY removeLast() {
        lock.lock();
        try {
            final KEY removedKey = queue.removeLast();
            return removedKey;
        } finally {
            lock.unlock();
        }
    }

    private void removeThenAddKey( KEY key ) {
        lock.lock();
        try {
            queue.removeFirstOccurrence( key );
            queue.addFirst( key );
        } finally {
            lock.unlock();
        }

    }

    private void removeFirstOccurrence( KEY key ) {
        lock.lock();
        try {
            queue.removeFirstOccurrence( key );
        } finally {
            lock.unlock();
        }

    }


    @Override
    public VALUE getSilent( KEY key ) {
        return map.get( key );
    }

    @Override
    public void remove( KEY key ) {
        removeFirstOccurrence( key );
        map.remove( key );
    }

    @Override
    public int size() {
        return map.size();
    }

    public String toString() {
        return map.toString();
    }
}
