package org.boon.datarepo.impl;

import org.boon.core.reflection.Conversions;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.criteria.Criteria;
import org.boon.criteria.QueryFactory;
import org.boon.criteria.Selector;
import org.boon.criteria.Sort;
import org.boon.datarepo.DataRepoException;
import org.boon.datarepo.PlanStep;
import org.boon.datarepo.ResultSet;
import org.boon.datarepo.spi.ResultSetInternal;

import java.lang.reflect.Array;
import java.util.*;

import static org.boon.Lists.list;
import static org.boon.core.reflection.Reflection.toMap;

public class ResultSetImpl<T> implements ResultSetInternal<T> {

    private List<T> results;
    private List<List<T>> allResults;
    private int totalSize;

    private List<T> lastList;

    private Map<String, FieldAccess> fields;


    public ResultSetImpl ( Map<String, FieldAccess> fields ) {
        this.fields = fields;
        this.allResults = new ArrayList<> ();
    }


    public ResultSetImpl ( List<T> results, Map<String, FieldAccess> fields ) {
        this.fields = fields;
        this.allResults = new ArrayList<> ();
        this.addResults ( results );
    }

<<<<<<< HEAD
    public ResultSetImpl ( List<T> results ) {
=======
    public ResultSetImpl( List<T> results ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( results.size () > 0 ) {
            this.fields = Reflection.getPropertyFieldAccessMap ( results.get ( 0 ).getClass () );
        } else {
            this.fields = Collections.EMPTY_MAP;
        }
        this.allResults = new ArrayList<> ();
        this.addResults ( results );
    }

<<<<<<< HEAD
    private void prepareResults () {
=======
    private void prepareResults() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( results == null && allResults.size () == 1 ) {
            results = allResults.get ( 0 );
        } else if ( results == null ) {

            results = new ArrayList<> ( totalSize );

            for ( List<T> list : allResults ) {
                for ( T item : list ) {
                    results.add ( item );
                }
            }
        }
        allResults.clear ();
        totalSize = 0;
    }


    public void addResults ( List<T> results ) {
        lastList = results;
        totalSize += results.size ();
        allResults.add ( results );
    }

    @Override
<<<<<<< HEAD
    public ResultSet expectOne () {
=======
    public ResultSet expectOne() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();
        if ( results.size () == 0 ) {
            throw new DataRepoException ( "Expected one result, no results" );
        } else if ( results.size () > 1 ) {
            throw new DataRepoException ( "Expected one result, but have many" );
        }
        return this;
    }

    @Override
<<<<<<< HEAD
    public <EXPECT> ResultSet<EXPECT> expectOne ( Class<EXPECT> clz ) {
=======
    public <EXPECT> ResultSet<EXPECT> expectOne( Class<EXPECT> clz ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return ( ResultSet<EXPECT> ) this.expectOne ();
    }

    @Override
<<<<<<< HEAD
    public ResultSet expectMany () {
=======
    public ResultSet expectMany() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        if ( results.size () <= 1 ) {
            throw new DataRepoException ( "Expected many" );
        }
        return this;
    }

    @Override
<<<<<<< HEAD
    public ResultSet expectNone () {
=======
    public ResultSet expectNone() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        if ( results.size () != 0 ) {
            throw new DataRepoException ( "Expected none" );
        }
        return this;
    }

    @Override
<<<<<<< HEAD
    public ResultSet expectOneOrMany () {
=======
    public ResultSet expectOneOrMany() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        if ( results.size () >= 1 ) {
            throw new DataRepoException ( "Expected one or many" );
        }
        return this;
    }

    @Override
<<<<<<< HEAD
    public ResultSet removeDuplication () {
=======
    public ResultSet removeDuplication() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();
        results = new ArrayList ( asSet () );
        return this;
    }

    @Override
<<<<<<< HEAD
    public ResultSet sort ( Sort sort ) {
=======
    public ResultSet sort( Sort sort ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();
        sort.sort ( results );
        return this;
    }

    @Override
<<<<<<< HEAD
    public Collection<T> filter ( Criteria criteria ) {
=======
    public Collection<T> filter( Criteria criteria ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();
        return QueryFactory.filter ( results, criteria );
    }

    @Override
<<<<<<< HEAD
    public void filterAndPrune ( Criteria criteria ) {
=======
    public void filterAndPrune( Criteria criteria ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();
        this.results = QueryFactory.filter ( results, criteria );
    }


    @Override
<<<<<<< HEAD
    public ResultSet<List<Map<String, Object>>> select ( Selector... selectors ) {
=======
    public ResultSet<List<Map<String, Object>>> select( Selector... selectors ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();
        return new ResultSetImpl (
                Selector.performSelection (
                        Arrays.asList ( selectors ), results, fields ),
                fields );

    }

    @Override
<<<<<<< HEAD
    public int[] selectInts ( Selector selector ) {
        prepareResults ();

        int[] values = new int[ results.size () ];
=======
    public int[] selectInts( Selector selector ) {
        prepareResults ();

        int[] values = new int[results.size ()];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toInt ( map.get ( keyName ) );
        }
        return values;
    }

    @Override
<<<<<<< HEAD
    public float[] selectFloats ( Selector selector ) {
        prepareResults ();

        float[] values = new float[ results.size () ];
=======
    public float[] selectFloats( Selector selector ) {
        prepareResults ();

        float[] values = new float[results.size ()];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toFloat ( map.get ( keyName ) );
        }
        return values;
    }

    @Override
<<<<<<< HEAD
    public short[] selectShorts ( Selector selector ) {
        prepareResults ();

        short[] values = new short[ results.size () ];
=======
    public short[] selectShorts( Selector selector ) {
        prepareResults ();

        short[] values = new short[results.size ()];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toShort ( map.get ( keyName ) );
        }
        return values;
    }

    @Override
<<<<<<< HEAD
    public double[] selectDoubles ( Selector selector ) {
        prepareResults ();

        double[] values = new double[ results.size () ];
=======
    public double[] selectDoubles( Selector selector ) {
        prepareResults ();

        double[] values = new double[results.size ()];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toDouble ( map.get ( keyName ) );
        }
        return values;
    }

    @Override
<<<<<<< HEAD
    public byte[] selectBytes ( Selector selector ) {
        prepareResults ();

        byte[] values = new byte[ results.size () ];
=======
    public byte[] selectBytes( Selector selector ) {
        prepareResults ();

        byte[] values = new byte[results.size ()];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toByte ( map.get ( keyName ) );
        }
        return values;
    }

    @Override
<<<<<<< HEAD
    public char[] selectChars ( Selector selector ) {
        prepareResults ();

        char[] values = new char[ results.size () ];
=======
    public char[] selectChars( Selector selector ) {
        prepareResults ();

        char[] values = new char[results.size ()];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toChar ( map.get ( keyName ) );
        }
        return values;
    }

    @Override
<<<<<<< HEAD
    public Object[] selectObjects ( Selector selector ) {
        prepareResults ();

        Object[] values = new Object[ results.size () ];
=======
    public Object[] selectObjects( Selector selector ) {
        prepareResults ();

        Object[] values = new Object[results.size ()];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = map.get ( keyName );
        }
        return values;
    }

    @Override
<<<<<<< HEAD
    public <OBJ> OBJ[] selectObjects ( Class<OBJ> cls, Selector selector ) {
=======
    public <OBJ> OBJ[] selectObjects( Class<OBJ> cls, Selector selector ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        Object values = Array.newInstance ( cls, results.size () );

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < results.size (); index++ ) {
            Map<String, Object> map = maps.get ( index );
            Reflection.idx ( values, index, map.get ( keyName ) );
        }
        return ( OBJ[] ) values;
    }


    @Override
<<<<<<< HEAD
    public <OBJ> ResultSet<OBJ> selectObjectsAsResultSet ( Class<OBJ> cls, Selector selector ) {
=======
    public <OBJ> ResultSet<OBJ> selectObjectsAsResultSet( Class<OBJ> cls, Selector selector ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        Object values = Array.newInstance ( cls, results.size () );

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < results.size (); index++ ) {
            Map<String, Object> map = maps.get ( index );
            Reflection.idx ( values, index, map.get ( keyName ) );
        }
        OBJ[] array = ( OBJ[] ) values;
        List list = new ArrayList ( Arrays.asList ( array ) );
        return new ResultSetImpl ( list );
    }

    @Override
<<<<<<< HEAD
    public Collection<T> asCollection () {
=======
    public Collection<T> asCollection() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        return results;
    }

    @Override
<<<<<<< HEAD
    public String asJSONString () {
=======
    public String asJSONString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        throw new RuntimeException ( "NOT IMPLEMENTED" );
    }

    @Override
<<<<<<< HEAD
    public List<Map<String, Object>> asListOfMaps () {
=======
    public List<Map<String, Object>> asListOfMaps() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();


        List<Map<String, Object>> items = new ArrayList<> ( results.size () );
        for ( T item : results ) {
            items.add ( toMap ( item ) );
        }
        return items;

    }

    @Override
<<<<<<< HEAD
    public List<T> asList () {
=======
    public List<T> asList() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        return results;
    }

    @Override
<<<<<<< HEAD
    public Set<T> asSet () {
=======
    public Set<T> asSet() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        return new HashSet ( results );
    }

    @Override
<<<<<<< HEAD
    public List<PlanStep> queryPlan () {
=======
    public List<PlanStep> queryPlan() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        throw new RuntimeException ( "NOT IMPLEMENTED" );
    }

    @Override
<<<<<<< HEAD
    public T firstItem () {
=======
    public T firstItem() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        return results.get ( 0 );
    }

    @Override
<<<<<<< HEAD
    public Map<String, Object> firstMap () {
=======
    public Map<String, Object> firstMap() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();
        return toMap ( this.firstItem () );
    }

    @Override
<<<<<<< HEAD
    public String firstJSON () {
=======
    public String firstJSON() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        throw new RuntimeException ( "NOT IMPLEMENTED" );
    }

    @Override
<<<<<<< HEAD
    public int firstInt ( Selector selector ) {
=======
    public int firstInt( Selector selector ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        int[] values = new int[ 1 ];

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length && index < maps.size (); index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toInt ( map.get ( keyName ) );
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 0 ];


    }

    @Override
<<<<<<< HEAD
    public float firstFloat ( Selector selector ) {
=======
    public float firstFloat( Selector selector ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        float[] values = new float[ 1 ];

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toFloat ( map.get ( keyName ) );
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
<<<<<<< HEAD
    public short firstShort ( Selector selector ) {
=======
    public short firstShort( Selector selector ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        short[] values = new short[ 1 ];

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toShort ( map.get ( keyName ) );
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
<<<<<<< HEAD
    public double firstDouble ( Selector selector ) {
=======
    public double firstDouble( Selector selector ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        double[] values = new double[ 1 ];

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toDouble ( map.get ( keyName ) );
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
<<<<<<< HEAD
    public byte firstByte ( Selector selector ) {
=======
    public byte firstByte( Selector selector ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        byte[] values = new byte[ 1 ];

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toByte ( map.get ( keyName ) );
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
<<<<<<< HEAD
    public char firstChar ( Selector selector ) {
=======
    public char firstChar( Selector selector ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        char[] values = new char[ 1 ];

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = Conversions.toChar ( map.get ( keyName ) );
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
<<<<<<< HEAD
    public Object firstObject ( Selector selector ) {
=======
    public Object firstObject( Selector selector ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        Object[] values = new Object[ 1 ];

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = map.get ( keyName );
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
<<<<<<< HEAD
    public <OBJ> OBJ firstObject ( Class<OBJ> cls, Selector selector ) {
=======
    public <OBJ> OBJ firstObject( Class<OBJ> cls, Selector selector ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        Object[] values = new Object[ 1 ];

        List<Map<String, Object>> maps = Selector.performSelection ( list ( selector ), results, fields );

        String keyName = selector.getName ();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get ( index );
            values[ index ] = map.get ( keyName );
            if ( index == 1 ) {
                break;
            }
        }
        return ( OBJ ) values[ 1 ];
    }

    @Override
<<<<<<< HEAD
    public List<T> paginate ( int start, int size ) {
=======
    public List<T> paginate( int start, int size ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        return results.subList ( start, start + size );
    }

    @Override
<<<<<<< HEAD
    public List<Map<String, Object>> paginateMaps ( int start, int size ) {
=======
    public List<Map<String, Object>> paginateMaps( int start, int size ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        List<Map<String, Object>> mapResults = new ArrayList<> ();
        List<T> list = this.paginate ( start, size );

        for ( T item : list ) {
            mapResults.add ( toMap ( item ) );
        }

        return mapResults;
    }

    @Override
<<<<<<< HEAD
    public String paginateJSON ( int start, int size ) {
=======
    public String paginateJSON( int start, int size ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();

        throw new RuntimeException ( "NOT IMPLEMENTED" );
    }

    @Override
<<<<<<< HEAD
    public int size () {
=======
    public int size() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( results != null ) {
            return this.results.size ();
        } else {
            return totalSize;
        }
    }

    @Override
<<<<<<< HEAD
    public Iterator<T> iterator () {
=======
    public Iterator<T> iterator() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        prepareResults ();
        return this.results.iterator ();
    }

    @Override
<<<<<<< HEAD
    public void andResults () {
=======
    public void andResults() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( allResults.size () == 0 ) {
            return;
        }

        if ( allResults.size () == 1 ) {
            prepareResults ();
            return;
        }

        boolean foundEmpty = false;

        for ( List<T> list : allResults ) {
            if ( list.size () == 0 ) {
                foundEmpty = true;
                break;
            }
        }

        if ( foundEmpty ) {
            results = Collections.EMPTY_LIST;
            allResults.clear ();
            totalSize = 0;
            return;
        }


        List<T> shortestList = null;
        int min = Integer.MAX_VALUE;
        for ( List<T> list : allResults ) {
            int size = list.size ();
            if ( size < min ) {
                min = size;
                shortestList = list;
            }
        }
        if ( shortestList == null ) {
            return;
        }

        allResults.remove ( shortestList );
        Set set = new HashSet ( shortestList );
        for ( List<T> list : allResults ) {
            set.retainAll ( list );
        }

        results = new ArrayList ( set );
        allResults.clear ();
        totalSize = 0;

    }

    @Override
<<<<<<< HEAD
    public int lastSize () {
=======
    public int lastSize() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( lastList == null ) {
            return 0;
        } else {
            return lastList.size ();
        }

    }


}
