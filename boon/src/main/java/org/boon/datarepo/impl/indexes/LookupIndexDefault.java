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

import org.boon.Exceptions;
import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.spi.SPIFactory;
import org.boon.core.Function;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A really simple lookupWithDefault index that uses a standard java.util.HashMap.
 *
 * @param <KEY>  key to lookupWithDefault
 * @param <ITEM> value
 */
public class LookupIndexDefault<KEY, ITEM> implements LookupIndex<KEY, ITEM> {


    protected Function<ITEM, KEY> keyGetter;
    protected Function<ITEM, KEY> primaryKeyGetter;

    protected Map<KEY, MultiValue> map;
    private Logger log = Logger.getLogger( LookupIndexDefault.class.getName() );
    protected boolean storeKeyInIndexOnly;
    private Function<Object, KEY> keyTransformer;

    protected int keyBucketSize = 3;


    public LookupIndexDefault( Class<?> keyType ) {


        if ( log.isLoggable( Level.FINE ) ) {
            log.fine( String.format( "key type %s ", keyType.getName() ) );
        }

        if ( keyType == null ) {
            return;
        }
        map = SPIFactory.getMapCreatorFactory().get().createMap( keyType );

    }


    protected void addManyKeys( ITEM item, List<KEY> keys ) {
        for ( KEY key : keys ) {
            if ( key != null ) {
                this.put( item, key );
            }
        }
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

        put( item, key );
        return true;

    }

    private void put( ITEM item, KEY key ) {

        MultiValue mv=null;
        Object primaryKey=null;

        try {

            if (log.isLoggable(Level.FINE)) {
                log.fine(String.format("put item = %s with key = %s ", item, key));
            }

            key = getKey(key);


            if (key instanceof Collection) {
                Collection collection = (Collection) key;

                for (Object keyComponent : collection) {

                    if (keyComponent == null) {
                        continue;
                    }
                    mv = map.get(keyComponent);
                    mv = mvCreateOrAddToMV(mv, item);
                    map.put((KEY) keyComponent, mv);
                }
                return;
            }


            mv = map.get(key);
            if (storeKeyInIndexOnly) {
                primaryKey = primaryKeyGetter.apply(item);

                mv = mvCreateOrAddToMV(mv, primaryKey);
            } else {
                mv = mvCreateOrAddToMV(mv, item);
            }

            map.put(key, mv);

        }catch (Exception ex) {
             Exceptions.handle(ex, "Problem putting item in lookupWithDefault index, item=", item, "key=", key, "mv=", mv,
                     "primaryKey=", primaryKey);
        }
    }

    private MultiValue mvCreateOrAddToMV( MultiValue mv, Object obj ) {
        return MultiValue.add( mv, obj, keyBucketSize );
    }


    protected final void removeManyKeys( ITEM item, List<KEY> keys ) {
        for ( KEY key : keys ) {
            if ( key != null ) {
                removeKey( item, key );
            }
        }
    }

    @Override
    public boolean delete( ITEM item ) {
        KEY key = keyGetter.apply( item );


        return removeKey( item, key );

    }

    private boolean removeKey( ITEM item, KEY key ) {
        key = getKey( key );

        if ( key == null ) {
            return false;
        }

        if (key instanceof  Collection) {
            Collection collection = (Collection) key;
            for (Object objKey : collection) {
                removeKey ( item, (KEY) objKey );
            }
        } else {
            MultiValue mv = map.get( key );

            if ( mv == null ) {
                return false;
            }

            mv = MultiValue.remove( mv, item );

            if ( mv == null ) {
                map.remove( key );
            }
        }
        return true;

    }


    public void setKeyGetter( Function<ITEM, KEY> keyGetter ) {
        Exceptions.requireNonNull( keyGetter, "keyGetter cannot be null" );
        this.keyGetter = keyGetter;
    }

    public void setPrimaryKeyGetter( Function<ITEM, KEY> keyGetter ) {
        Exceptions.requireNonNull( keyGetter, "keyGetter cannot be null" );
        storeKeyInIndexOnly = true;
        this.primaryKeyGetter = keyGetter;
    }

    @Override
    public List<ITEM> all() {

        if ( log.isLoggable( Level.FINE ) ) {
            log.fine( "all called" );
        }

        List results = new ArrayList<>( map.size() );
        for ( MultiValue values : map.values() ) {
            values.addTo( results );
        }
        return results;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Collection<ITEM> toCollection() {
        return ( Collection<ITEM> ) this.map.values();
    }


    @Override
    public ITEM get( KEY key ) {

        key = getKey( key );

        MultiValue mv = map.get( key );
        if ( mv == null ) {
            return null;
        } else {
            return ( ITEM ) mv.getValue();
        }
    }

    protected KEY getKey( KEY key ) {
        if ( keyTransformer != null ) {
            key = this.keyTransformer.apply( key );
        }
        return key;
    }


    public List<ITEM> getAll( KEY key ) {
        key = getKey( key );

        MultiValue mv = map.get( key );
        if ( mv == null ) {
            return null;
        } else {
            return mv.getValues();
        }
    }

    @Override
    public boolean deleteByKey( KEY key ) {
        key = getKey( key );

        this.map.remove( key );
        return true;
    }


    //TODO implement so we can store only primary keys in a index to make indexes smaller if
    //we ever decide to cache actual items
    @Override
    public boolean isPrimaryKeyOnly() {
        return storeKeyInIndexOnly;
    }

    @Override
    public void setInputKeyTransformer( Function<Object, KEY> func ) {
        this.keyTransformer = func;
    }

    @Override
    public void setBucketSize( int size ) {
        this.keyBucketSize = size;
    }

    @Override
    public void init() {
    }

    @Override
    public boolean has( KEY key ) {
        return this.map.containsKey( key );
    }


    @Override
    public void clear() {

        if ( log.isLoggable( Level.FINE ) ) {
            log.fine( "clear called" );
        }
        this.map.clear();
    }

}
