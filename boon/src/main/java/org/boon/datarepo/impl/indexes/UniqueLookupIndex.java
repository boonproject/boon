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

package org.boon.datarepo.impl.indexes;

import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.spi.SPIFactory;
import org.boon.core.Function;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.boon.Lists.list;

public class UniqueLookupIndex<KEY, ITEM> implements LookupIndex<KEY, ITEM> {

    protected Function<ITEM, KEY> keyGetter;
    protected Map<KEY, ITEM> map = null;


    private Logger log = Logger.getLogger( UniqueLookupIndex.class.getName() );

    private Function<Object, KEY> keyTransformer;

    public UniqueLookupIndex( Class<?> keyType ) {
        if ( keyType == null ) {
            return;
        }
        map = SPIFactory.getMapCreatorFactory().get().createMap( keyType );

    }

    @Override
    public ITEM get( KEY key ) {
        key = getKey( key );
        return map.get( key );
    }

    @Override
    public void setKeyGetter( Function<ITEM, KEY> keyGetter ) {
        this.keyGetter = keyGetter;
    }

    @Override
    public boolean add( ITEM item ) {

        if ( log.isLoggable( Level.FINE ) ) {
            log.fine( String.format( "addObject item = %s", item ) );
        }

        KEY key = keyGetter.apply( item );
        if ( key == null ) {
            return false;
        }

        /* You can not addObject the same key twice. */
        if ( this.map.containsKey( key ) ) {
            return false;
        }

        map.put( key, item );
        return true;

    }

    @Override
    public boolean delete( ITEM item ) {

        if ( log.isLoggable( Level.FINE ) ) {
            log.fine( String.format( "delete item = %s", item ) );
        }

        KEY key = keyGetter.apply( item );
        key = getKey( key );
        return map.remove( key ) != null;
    }

    @Override
    public List<ITEM> all() {

        if ( log.isLoggable( Level.FINE ) ) {
            log.fine( "all called " );
        }

        return new ArrayList<>( map.values() );
    }

    @Override
    public List<ITEM> getAll( KEY key ) {

        if ( log.isLoggable( Level.FINE ) ) {
            log.fine( "getAll called " );
        }

        return list( this.get( key ) );
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Collection<ITEM> toCollection() {
        return new HashSet( this.map.values() );
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public boolean deleteByKey( KEY key ) {
        key = getKey( key );
        return this.map.remove( key ) != null;
    }

    @Override
    public boolean isPrimaryKeyOnly() {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public boolean has( KEY key ) {
        if ( key == null ) {
            return false;
        }
        return this.map.containsKey( key );
    }


    @Override
    public void setInputKeyTransformer( Function<Object, KEY> func ) {
        this.keyTransformer = func;
    }

    @Override
    public void setBucketSize( int size ) {

    }

    protected KEY getKey( KEY key ) {
        if ( keyTransformer != null ) {
            key = this.keyTransformer.apply( key );
        }
        return key;
    }


}
