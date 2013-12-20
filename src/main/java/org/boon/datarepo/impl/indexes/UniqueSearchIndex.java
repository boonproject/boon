package org.boon.datarepo.impl.indexes;

import org.boon.datarepo.spi.SPIFactory;
import org.boon.datarepo.spi.SearchIndex;
import org.boon.predicates.Function;
import org.boon.primitive.CharBuf;

import java.util.*;

import static org.boon.Lists.list;

/**
 * Default Search Index which uses a TreeMap
 *
 * @param <KEY>  Key we are indexing on.
 * @param <ITEM> The items we are indexing.
 */
public class UniqueSearchIndex<KEY, ITEM> extends UniqueLookupIndex<KEY, ITEM> implements SearchIndex<KEY, ITEM> {
    private Class<?> keyType;

    private NavigableMap<KEY, ITEM> navigableMap;
    private Comparator collator;

    public UniqueSearchIndex ( Class<?> keyType ) {
        super ( keyType );
        this.keyType = keyType;

    }

    public UniqueSearchIndex ( Class<?> keyType, List<ITEM> items, Function<ITEM, KEY> keyGetter ) {
        super ( keyType );
        super.keyGetter = keyGetter;
        super.map
                = SPIFactory.getMapCreatorFactory ().get ().createMap ( keyType );

        this.navigableMap = ( NavigableMap<KEY, ITEM> ) super.map;

        for ( ITEM item : items ) {
            add ( item );
        }


    }

    @Override
    public void setComparator ( Comparator collator ) {
        this.collator = collator;
    }

    @Override
<<<<<<< HEAD
    public void init () {
=======
    public void init() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        super.map
                = SPIFactory.getMapCreatorFactory ().get ().createNavigableMap ( keyType, collator );

        this.navigableMap = ( NavigableMap<KEY, ITEM> ) super.map;

    }

    @Override
<<<<<<< HEAD
    public ITEM findFirst () {
=======
    public ITEM findFirst() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.navigableMap.firstEntry ().getValue ();
    }

    @Override
<<<<<<< HEAD
    public ITEM findLast () {
=======
    public ITEM findLast() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.navigableMap.lastEntry ().getValue ();
    }

    @Override
<<<<<<< HEAD
    public KEY findFirstKey () {
=======
    public KEY findFirstKey() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.navigableMap.firstEntry ().getKey ();
    }

    @Override
<<<<<<< HEAD
    public KEY findLastKey () {
=======
    public KEY findLastKey() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.navigableMap.lastEntry ().getKey ();
    }

    @Override
    public List<ITEM> findEquals ( KEY key ) {
        key = getKey ( key );
        return list ( navigableMap.get ( key ) );
    }

    @Override
    public List<ITEM> findStartsWith ( KEY keyFrag ) {
        keyFrag = getKey ( keyFrag );

        List<ITEM> results;

        if ( keyFrag instanceof String ) {
            String start = ( String ) keyFrag;
            if ( start.length () == 0 || start == null ) {
                return Collections.EMPTY_LIST;
            }

            char endLetter = start.charAt ( start.length () - 1 );
            String sub = start.substring ( 0, start.length () - 1 );

            CharBuf after = CharBuf.create ( start.length () );

            after.add ( String.valueOf ( sub ) );
            after.add ( ( char ) ( endLetter + 1 ) );

            NavigableMap<String, MultiValue<ITEM>> sortMap = ( NavigableMap<String, MultiValue<ITEM>> ) this.navigableMap;


            SortedMap<String, MultiValue<ITEM>> sortedSubMap = sortMap.subMap ( start, after.toString () );

            if ( sortedSubMap.size () > 0 ) {
                results = new ArrayList<> ();
                for ( MultiValue<ITEM> values : sortedSubMap.values () ) {
                    values.addTo ( results );
                }
                return results;
            }
            return Collections.EMPTY_LIST;
        }
        return Collections.EMPTY_LIST;

    }

    @Override
    public List<ITEM> findEndsWith ( KEY keyFrag ) {
        throw new UnsupportedOperationException ( "findEndsWith Not supported" );
    }

    @Override
    public List<ITEM> findContains ( KEY keyFrag ) {
        throw new UnsupportedOperationException ( "findContains Not supported" );
    }

    @Override
    public List<ITEM> findBetween ( KEY start, KEY end ) {
        start = getKey ( start );
        end = getKey ( end );

        SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.subMap ( start, end );

        return new ArrayList<> ( keyMultiValueSortedMap.values () );

    }


    @Override
    public List<ITEM> findGreaterThan ( KEY key ) {
        key = getKey ( key );

        SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.tailMap ( key, false );
        return new ArrayList<> ( keyMultiValueSortedMap.values () );
    }

    @Override
    public List<ITEM> findLessThan ( KEY key ) {
        key = getKey ( key );
        SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.headMap ( key, false );
        return new ArrayList<> ( keyMultiValueSortedMap.values () );
    }

    @Override
    public List<ITEM> findGreaterThanEqual ( KEY key ) {
        key = getKey ( key );
        SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.tailMap ( key );
        return new ArrayList<> ( keyMultiValueSortedMap.values () );
    }

    @Override
    public List<ITEM> findLessThanEqual ( KEY key ) {
        key = getKey ( key );
        SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.headMap ( key );
        return new ArrayList<> ( keyMultiValueSortedMap.values () );
    }

    @Override
<<<<<<< HEAD
    public ITEM min () {
=======
    public ITEM min() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.navigableMap.firstEntry ().getValue ();
    }

    @Override
<<<<<<< HEAD
    public ITEM max () {
=======
    public ITEM max() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.navigableMap.lastEntry ().getValue ();
    }

    @Override
    public List<ITEM> getAll ( KEY key ) {
        return this.findEquals ( key );
    }

    @Override
<<<<<<< HEAD
    public int size () {
=======
    public int size() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.navigableMap.size ();
    }


    @Override
    public int count ( KEY key ) {
        return this.navigableMap.containsKey ( key ) ? 1 : 0;
    }


}
