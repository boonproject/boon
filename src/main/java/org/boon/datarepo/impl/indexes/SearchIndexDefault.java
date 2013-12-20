package org.boon.datarepo.impl.indexes;

import org.boon.datarepo.spi.SPIFactory;
import org.boon.datarepo.spi.SearchIndex;
import org.boon.predicates.Function;
import org.boon.primitive.CharBuf;

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


    public SearchIndexDefault ( Class<?> keyType ) {
        super ( keyType );
        this.keyType = keyType;


    }

    public SearchIndexDefault ( Class<?> keyType, List<ITEM> items, Function<ITEM, KEY> keyGetter ) {
        super ( null );
        super.keyGetter = keyGetter;
        super.map = SPIFactory.getMapCreatorFactory ().get ().createNavigableMap ( keyType );
        this.navigableMap = ( NavigableMap<KEY, MultiValue> ) super.map;

        for ( ITEM item : items ) {
            add ( item );
        }


    }

    public SearchIndexDefault ( Class<?> keyType, List<ITEM> items, Function<ITEM, KEY> keyGetter, Collator collator ) {
        super ( null );
        super.keyGetter = keyGetter;
        super.map = SPIFactory.getMapCreatorFactory ().get ().createNavigableMap ( keyType, collator );
        this.navigableMap = ( NavigableMap<KEY, MultiValue> ) super.map;

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
        super.map = SPIFactory.getMapCreatorFactory ().get ().createNavigableMap ( this.keyType, this.collator );
        this.navigableMap = ( NavigableMap<KEY, MultiValue> ) super.map;
    }


    @Override
<<<<<<< HEAD
    public ITEM findFirst () {
=======
    public ITEM findFirst() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return ( ITEM ) this.navigableMap.firstEntry ().getValue ().getValue ();
    }

    @Override
<<<<<<< HEAD
    public ITEM findLast () {
=======
    public ITEM findLast() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return ( ITEM ) this.navigableMap.lastEntry ().getValue ().getValue ();
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
        MultiValue<ITEM> items = navigableMap.get ( key );
        if ( items == null ) {
            return null;
        }
        return items.getValues ();
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

            after.add ( sub );
            after.add ( ( char ) ( endLetter + 1 ) );

            NavigableMap<String, MultiValue> sortMap = ( NavigableMap<String, MultiValue> ) this.navigableMap;


            SortedMap<String, MultiValue> sortedSubMap = sortMap.subMap ( start, after.toString () );

            if ( sortedSubMap.size () > 0 ) {
                results = new ArrayList<> ();
                for ( MultiValue values : sortedSubMap.values () ) {
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
        keyFrag = getKey ( keyFrag );

        List<ITEM> results = new ArrayList<> ();

        if ( keyFrag instanceof String ) {

            Collection<MultiValue> values = navigableMap.values ();
            for ( MultiValue<ITEM> mv : values ) {
                for ( ITEM value : mv.getValues () ) {
                    String svalue = ( String ) this.keyGetter.apply ( value );
                    if ( svalue.endsWith ( ( String ) keyFrag ) ) {
                        results.add ( value );
                    }
                }
            }
        }
        return results;
    }

    @Override
    public List<ITEM> findContains ( KEY keyFrag ) {
        keyFrag = getKey ( keyFrag );

        List<ITEM> results = new ArrayList<> ();

        if ( keyFrag instanceof String ) {

            Collection<MultiValue> values = navigableMap.values ();
            for ( MultiValue<ITEM> mv : values ) {
                for ( ITEM value : mv.getValues () ) {

                    String svalue = ( String ) this.keyGetter.apply ( value );
                    if ( svalue.endsWith ( ( String ) keyFrag ) ) {
                        results.add ( value );
                    }
                }
            }
        }
        return results;
    }


    boolean init;

<<<<<<< HEAD
    void initIfNeeded () {
=======
    void initIfNeeded() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( !init ) {
            init = true;
            ITEM item = ( ITEM ) ( ( MultiValue ) this.navigableMap.firstEntry () ).getValue ();

        }
    }

    @Override
    public List<ITEM> findBetween ( KEY start, KEY end ) {
        start = getKey ( start );
        end = getKey ( end );


        SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.subMap ( start, end );

        return getResults ( keyMultiValueSortedMap );

    }

    private List<ITEM> getResults ( SortedMap<KEY, MultiValue> keyMultiValueSortedMap ) {
        List<ITEM> results = null;
        if ( keyMultiValueSortedMap.size () > 0 ) {
            results = new ArrayList<> ();
            for ( MultiValue<ITEM> values : keyMultiValueSortedMap.values () ) {
                values.addTo ( results );
            }
            return results;
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<ITEM> findGreaterThan ( KEY key ) {
        key = getKey ( key );


        SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.tailMap ( key, false );
        return getResults ( keyMultiValueSortedMap );
    }

    @Override
    public List<ITEM> findLessThan ( KEY key ) {
        key = getKey ( key );

        SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.headMap ( key, false );
        return getResults ( keyMultiValueSortedMap );
    }

    @Override
    public List<ITEM> findGreaterThanEqual ( KEY key ) {
        key = getKey ( key );

        SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.tailMap ( key );
        return getResults ( keyMultiValueSortedMap );
    }

    @Override
    public List<ITEM> findLessThanEqual ( KEY key ) {
        key = getKey ( key );

        SortedMap<KEY, MultiValue> keyMultiValueSortedMap = this.navigableMap.headMap ( key );
        return getResults ( keyMultiValueSortedMap );
    }


    @Override
<<<<<<< HEAD
    public ITEM min () {
=======
    public ITEM min() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return ( ITEM ) this.navigableMap.firstEntry ().getValue ().getValue ();
    }

    @Override
<<<<<<< HEAD
    public ITEM max () {
=======
    public ITEM max() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return ( ITEM ) this.navigableMap.lastEntry ().getValue ().getValue ();
    }

    @Override
<<<<<<< HEAD
    public int count ( KEY key ) {
=======
    public int count( KEY key ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.navigableMap.get ( key ).size ();
    }


    @Override
<<<<<<< HEAD
    public int size () {
=======
    public int size() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.navigableMap.size ();
    }

}
