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

package org.boon.datarepo.impl.maps;

import org.boon.datarepo.spi.TypedMap;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;

import static org.boon.core.Conversions.wrapAsObject;


public class JavaUtilNavigableMap<K, V> extends ConcurrentSkipListMap<K, V> implements TypedMap<K, V> {

    public JavaUtilNavigableMap() {
    }

    public JavaUtilNavigableMap( Comparator<? super K> comparator ) {
        super( comparator );
    }

    @Override
    public final boolean put( K key, boolean i ) {
        return ( Boolean ) super.put( key, ( V ) wrapAsObject( i ) );
    }

    @Override
    public final boolean getBoolean( K key ) {
        return ( Boolean ) super.get( key );
    }

    @Override
    public V put( byte key, V v ) {
        return super.put( ( K ) wrapAsObject( key ), v );
    }

    @Override
    public final byte put( K key, byte i ) {
        return ( Byte ) super.put( key, ( V ) wrapAsObject( i ) );
    }

    @Override
    public final byte getByte( K key ) {
        return ( Byte ) super.get( key );
    }

    @Override
    public V put( short key, V v ) {
        return super.put( ( K ) wrapAsObject( key ), v );
    }

    @Override
    public final short put( K key, short i ) {
        return ( Short ) super.put( key, ( V ) wrapAsObject( i ) );
    }

    @Override
    public final short getShort( K key ) {
        return ( Short ) super.get( key );
    }

    @Override
    public V put( int key, V v ) {
        return super.put( ( K ) wrapAsObject( key ), v );
    }

    @Override
    public final int put( K key, int i ) {
        return ( Integer ) super.put( key, ( V ) wrapAsObject( i ) );
    }

    @Override
    public final int getInt( K key ) {
        return ( Integer ) super.get( key );
    }

    @Override
    public V put( long key, V v ) {
        return super.put( ( K ) wrapAsObject( key ), v );
    }

    @Override
    public final long put( K key, long i ) {
        return ( Long ) super.put( key, ( V ) wrapAsObject( i ) );
    }

    @Override
    public final long getLong( K key ) {
        return ( Long ) super.get( key );
    }

    @Override
    public V put( float key, V v ) {
        return super.put( ( K ) wrapAsObject( key ), v );
    }

    @Override
    public final float put( K key, float i ) {
        return ( Float ) super.put( key, ( V ) wrapAsObject( i ) );
    }

    @Override
    public final float getFloat( K key ) {
        return ( Float ) super.get( key );
    }

    @Override
    public V put( double key, V v ) {
        return super.put( ( K ) wrapAsObject( key ), v );
    }

    @Override
    public final double put( K key, double i ) {
        return ( Double ) super.put( key, ( V ) wrapAsObject( i ) );
    }

    @Override
    public final double getDouble( K key ) {
        return ( Double ) super.get( key );
    }

    @Override
    public final V put( char key, V v ) {
        return super.put( ( K ) wrapAsObject( key ), v );
    }

    @Override
    public final char put( K key, char i ) {
        return ( Character ) super.put( key, ( V ) wrapAsObject( i ) );
    }

    @Override
    public final char getChar( K key ) {
        return ( Character ) super.get( key );
    }

}
