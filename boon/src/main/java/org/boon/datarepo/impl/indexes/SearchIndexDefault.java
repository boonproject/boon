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

import org.boon.core.Typ;
import org.boon.datarepo.spi.SPIFactory;
import org.boon.datarepo.spi.SearchIndex;
import org.boon.core.Function;
import org.boon.primitive.CharBuf;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;

/**
 * Default Search Index which uses a TreeMap
 *
 * @param <KEY>  Key we are indexing on.
 * @param <ITEM> The items we are indexing.
 */
public class SearchIndexDefault<KEY, ITEM> extends LookupIndexDefault<KEY, ITEM> implements SearchIndex<KEY, ITEM> {
    private NavigableMap<KEY, MultiValue> navigableMap;

    private Comparator collator;

    private Class<?> keyType;



    public SearchIndexDefault( Class<?> keyType ) {
        super( keyType );
        this.keyType = keyType;

    }



    public SearchIndexDefault( Class<?> keyType, List<ITEM> items, Function<ITEM, KEY> keyGetter ) {
        super( null );
        super.keyGetter = keyGetter;
        super.map = SPIFactory.getMapCreatorFactory().get().createNavigableMap( keyType );
        this.navigableMap = ( NavigableMap<KEY, MultiValue> ) super.map;

        for ( ITEM item : items ) {
            add( item );
        }


    }

    public SearchIndexDefault( Class<?> keyType, List<ITEM> items, Function<ITEM, KEY> keyGetter, Collator collator ) {
        super( null );
        super.keyGetter = keyGetter;
        super.map = SPIFactory.getMapCreatorFactory().get().createNavigableMap( keyType, collator );
        this.navigableMap = ( NavigableMap<KEY, MultiValue> ) super.map;

        for ( ITEM item : items ) {
            add( item );
        }


    }

    @Override
    public void setComparator( Comparator collator ) {
        this.collator = collator;
    }

    @Override
    public void init() {

        if (this.collator!=null) {
            super.map = SPIFactory.getMapCreatorFactory().get().createNavigableMap(this.keyType, this.collator);

        } else {
            if (keyType == Typ.number) {
                super.map = SPIFactory.getMapCreatorFactory().get().createNavigableMap(keyType, new Comparator<Number>() {
                    @Override
                    public int compare(Number o1, Number o2) {

                        if (o1 instanceof Long) {
                            long long1 = o1.longValue();
                            long long2 = o2.longValue();
                            if (long1 > long2) {
                                return 1;
                            } else if (long1 < long2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else if (o1 instanceof Double) {
                            double long1 = o1.doubleValue();
                            double long2 = o2.doubleValue();
                            if (long1 > long2) {
                                return 1;
                            } else if (long1 < long2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else if (o1 instanceof BigDecimal) {
                            double long1 = o1.doubleValue();
                            double long2 = o2.doubleValue();
                            if (long1 > long2) {
                                return 1;
                            } else if (long1 < long2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else {
                            double long1 = o1.doubleValue();
                            double long2 = o2.doubleValue();
                            if (long1 > long2) {
                                return 1;
                            }else if (long1 < long2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }

                    }
                });
            } else {
                super.map = SPIFactory.getMapCreatorFactory().get().createNavigableMap(this.keyType);

            }
        }

        this.navigableMap = ( NavigableMap<KEY, MultiValue> ) super.map;

    }


    @Override
    public ITEM findFirst() {
        return ( ITEM ) this.navigableMap.firstEntry().getValue().getValue();
    }

    @Override
    public ITEM findLast() {
        return ( ITEM ) this.navigableMap.lastEntry().getValue().getValue();
    }

    @Override
    public KEY findFirstKey() {
        return this.navigableMap.firstEntry().getKey();
    }

    @Override
    public KEY findLastKey() {
        return this.navigableMap.lastEntry().getKey();
    }

    @Override
    public List<ITEM> findEquals( KEY key ) {
        key = getKey( key );
        MultiValue<ITEM> items = navigableMap.get( key );
        if ( items == null ) {
            return null;
        }
        return items.getValues();
    }

    @Override
    public List<ITEM> findStartsWith( KEY keyFrag ) {

        keyFrag = getKey( keyFrag );
        List<ITEM> results;

        if ( keyFrag instanceof String ) {
            String start = ( String ) keyFrag;
            if ( start.length() == 0 || start == null ) {
                return Collections.EMPTY_LIST;
            }

            char endLetter = start.charAt( start.length() - 1 );
            String sub = start.substring( 0, start.length() - 1 );

            CharBuf after = CharBuf.create( start.length() );

            after.add( sub );
            after.add( ( char ) ( endLetter + 1 ) );

            NavigableMap<String, MultiValue> sortMap = ( NavigableMap<String, MultiValue> ) this.navigableMap;


            SortedMap<String, MultiValue> sortedSubMap = sortMap.subMap( start, after.toString() );

            if ( sortedSubMap.size() > 0 ) {
                results = new ArrayList<>();
                for ( MultiValue values : sortedSubMap.values() ) {
                    values.addTo( results );
                }
                return results;
            }
            return Collections.EMPTY_LIST;
        }
        return Collections.EMPTY_LIST;

    }

    @Override
    public List<ITEM> findEndsWith( KEY keyFrag ) {
        keyFrag = getKey( keyFrag );

        List<ITEM> results = new ArrayList<>();

        if ( keyFrag instanceof String ) {

            Collection<MultiValue> values = navigableMap.values();
            for ( MultiValue<ITEM> mv : values ) {
                for ( ITEM value : mv.getValues() ) {
                    String svalue = ( String ) this.keyGetter.apply( value );
                    if ( svalue.endsWith( ( String ) keyFrag ) ) {
                        results.add( value );
                    }
                }
            }
        }
        return results;
    }

    @Override
    public List<ITEM> findContains( KEY keyFrag ) {
        keyFrag = getKey( keyFrag );

        List<ITEM> results = new ArrayList<>();

        if ( keyFrag instanceof String ) {

            Collection<MultiValue> values = navigableMap.values();
            for ( MultiValue<ITEM> mv : values ) {
                for ( ITEM value : mv.getValues() ) {

                    String svalue = ( String ) this.keyGetter.apply( value );
                    if ( svalue.endsWith( ( String ) keyFrag ) ) {
                        results.add( value );
                    }
                }
            }
        }
        return results;
    }


    boolean init;

    void initIfNeeded() {
        if ( !init ) {
            init = true;
            ITEM item = ( ITEM ) ( ( MultiValue ) this.navigableMap.firstEntry() ).getValue();

        }
    }

    @Override
    public List<ITEM> findBetween( KEY start, KEY end ) {
        start = getKey( start );
        end = getKey( end );


        SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.subMap( start, end );

        return getResults( keyMultiValueSortedMap );

    }

    private List<ITEM> getResults( SortedMap<KEY, MultiValue> keyMultiValueSortedMap ) {
        List<ITEM> results = null;
        if ( keyMultiValueSortedMap.size() > 0 ) {
            results = new ArrayList<>();
            for ( MultiValue<ITEM> values : keyMultiValueSortedMap.values() ) {
                values.addTo( results );
            }
            return results;
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ITEM> findGreaterThan( KEY key ) {
        key = getKey( key );


        SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.tailMap( key, false );
        return getResults( keyMultiValueSortedMap );
    }

    @Override
    public List<ITEM> findLessThan( KEY key ) {
        key = getKey( key );

        SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.headMap( key, false );
        return getResults( keyMultiValueSortedMap );
    }

    @Override
    public List<ITEM> findGreaterThanEqual( KEY key ) {
        key = getKey( key );

        SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.tailMap( key );
        return getResults( keyMultiValueSortedMap );
    }

    @Override
    public List<ITEM> findLessThanEqual( KEY key ) {
        key = getKey( key );

        SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.headMap( key );
        return getResults( keyMultiValueSortedMap );
    }


    @Override
    public ITEM min() {
        return ( ITEM ) this.navigableMap.firstEntry().getValue().getValue();
    }

    @Override
    public ITEM max() {
        return ( ITEM ) this.navigableMap.lastEntry().getValue().getValue();
    }

    @Override
    public int count( KEY key ) {
        return this.navigableMap.get( key ).size();
    }


    @Override
    public int size() {
        return this.navigableMap.size();
    }

}
