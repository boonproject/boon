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

package org.boon.datarepo.impl;

import org.boon.core.reflection.BeanUtils;
import org.boon.core.Conversions;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.criteria.internal.Criteria;
import org.boon.criteria.internal.QueryFactory;
import org.boon.criteria.Selector;
import org.boon.sort.Sort;
import org.boon.datarepo.DataRepoException;
import org.boon.datarepo.PlanStep;
import org.boon.datarepo.ResultSet;
import org.boon.datarepo.spi.ResultSetInternal;

import java.lang.reflect.Array;
import java.util.*;

import static org.boon.Boon.toJson;
import static org.boon.Lists.list;
import static org.boon.core.reflection.MapObjectConversion.toMap;

public class ResultSetImpl<T> implements ResultSetInternal<T> {

    private List<T> results;
    private List<List<T>> allResults;
    private int totalSize;

    private List<T> lastList;

    private Map<String, FieldAccess> fields;


    public ResultSetImpl( Map<String, FieldAccess> fields ) {
        this.fields = fields;
        this.allResults = new ArrayList<>();
    }


    public ResultSetImpl( List<T> results, Map<String, FieldAccess> fields ) {
        this.fields = fields;
        this.allResults = new ArrayList<>();
        this.addResults( results );
    }

    public ResultSetImpl( List<T> results ) {
        if ( results.size() > 0 ) {
            this.fields = BeanUtils.getFieldsFromObject( results.get( 0 ));
        } else {
            this.fields = Collections.EMPTY_MAP;
        }
        this.allResults = new ArrayList<>();
        this.addResults( results );
    }

    private void prepareResults() {
        if ( results == null && allResults.size() == 1 ) {
            results = allResults.get( 0 );
        } else if ( results == null ) {

            results = new ArrayList<>( totalSize );

            for ( List<T> list : allResults ) {
                for ( T item : list ) {
                    results.add( item );
                }
            }
        }
        allResults.clear();
        totalSize = 0;
    }


    public void addResults( List<T> results ) {
        lastList = results;
        totalSize += results.size();
        allResults.add( results );
    }

    @Override
    public ResultSet expectOne() {
        prepareResults();
        if ( results.size() == 0 ) {
            throw new DataRepoException( "Expected one result, no results" );
        } else if ( results.size() > 1 ) {
            throw new DataRepoException( "Expected one result, but have many" );
        }
        return this;
    }

    @Override
    public <EXPECT> ResultSet<EXPECT> expectOne( Class<EXPECT> clz ) {
        return ( ResultSet<EXPECT> ) this.expectOne();
    }

    @Override
    public ResultSet expectMany() {
        prepareResults();

        if ( results.size() <= 1 ) {
            throw new DataRepoException( "Expected many" );
        }
        return this;
    }

    @Override
    public ResultSet expectNone() {
        prepareResults();

        if ( results.size() != 0 ) {
            throw new DataRepoException( "Expected none" );
        }
        return this;
    }

    @Override
    public ResultSet expectOneOrMany() {
        prepareResults();

        if ( results.size() >= 1 ) {
            throw new DataRepoException( "Expected one or many" );
        }
        return this;
    }

    @Override
    public ResultSet removeDuplication() {
        prepareResults();
        results = new ArrayList( asSet() );
        return this;
    }

    @Override
    public ResultSet sort( Sort sort ) {
        prepareResults();
        sort.sort( results );
        return this;
    }

    @Override
    public Collection<T> filter( Criteria criteria ) {
        prepareResults();
        return QueryFactory.filter( results, criteria );
    }

    @Override
    public void filterAndPrune( Criteria criteria ) {
        prepareResults();
        this.results = QueryFactory.filter( results, criteria );
    }


    @Override
    public ResultSet<List<Map<String, Object>>> select( Selector... selectors ) {
        prepareResults();
        return new ResultSetImpl(
                Selector.selectFrom(
                        Arrays.asList(selectors), results, fields),
                fields );

    }

    @Override
    public int[] selectInts( Selector selector ) {
        prepareResults();

        int[] values = new int[ results.size() ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toInt( map.get( keyName ) );
        }
        return values;
    }

    @Override
    public float[] selectFloats( Selector selector ) {
        prepareResults();

        float[] values = new float[ results.size() ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toFloat( map.get( keyName ) );
        }
        return values;
    }

    @Override
    public short[] selectShorts( Selector selector ) {
        prepareResults();

        short[] values = new short[ results.size() ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toShort( map.get( keyName ) );
        }
        return values;
    }

    @Override
    public double[] selectDoubles( Selector selector ) {
        prepareResults();

        double[] values = new double[ results.size() ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toDouble( map.get( keyName ) );
        }
        return values;
    }

    @Override
    public byte[] selectBytes( Selector selector ) {
        prepareResults();

        byte[] values = new byte[ results.size() ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toByte( map.get( keyName ) );
        }
        return values;
    }

    @Override
    public char[] selectChars( Selector selector ) {
        prepareResults();

        char[] values = new char[ results.size() ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toChar( map.get( keyName ) );
        }
        return values;
    }

    @Override
    public Object[] selectObjects( Selector selector ) {
        prepareResults();

        Object[] values = new Object[ results.size() ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = map.get( keyName );
        }
        return values;
    }

    @Override
    public <OBJ> OBJ[] selectObjects( Class<OBJ> cls, Selector selector ) {
        prepareResults();

        Object values = Array.newInstance( cls, results.size() );

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < results.size(); index++ ) {
            Map<String, Object> map = maps.get( index );
            BeanUtils.idx ( values, index, map.get ( keyName ) );
        }
        return ( OBJ[] ) values;
    }


    @Override
    public <OBJ> ResultSet<OBJ> selectObjectsAsResultSet( Class<OBJ> cls, Selector selector ) {
        prepareResults();

        Object values = Array.newInstance( cls, results.size() );

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < results.size(); index++ ) {
            Map<String, Object> map = maps.get( index );
            BeanUtils.idx ( values, index, map.get ( keyName ) );
        }
        OBJ[] array = ( OBJ[] ) values;
        List list = new ArrayList( Arrays.asList( array ) );
        return new ResultSetImpl( list );
    }

    @Override
    public Collection<T> asCollection() {
        prepareResults();

        return results;
    }

    @Override
    public String asJSONString() {
        prepareResults();

        throw new RuntimeException( "NOT IMPLEMENTED" );
    }

    @Override
    public List<Map<String, Object>> asListOfMaps() {
        prepareResults();


        List<Map<String, Object>> items = new ArrayList<>( results.size() );
        for ( T item : results ) {
            items.add( toMap( item ) );
        }
        return items;

    }

    @Override
    public List<T> asList() {
        prepareResults();

        return results;
    }

    @Override
    public <G> List<G> asList(Class<G> cls) {
        return (List<G>)asList();
    }

    @Override
    public Set<T> asSet() {
        prepareResults();

        return new HashSet( results );
    }

    @Override
    public List<PlanStep> queryPlan() {
        throw new RuntimeException( "NOT IMPLEMENTED" );
    }

    @Override
    public T firstItem() {
        prepareResults();

        return results.get(0);
    }

    @Override
    public Map<String, Object> firstMap() {
        prepareResults();
        return toMap(this.firstItem());
    }

    @Override
    public String firstJSON() {
        return toJson( firstItem () );
    }

    @Override
    public int firstInt( Selector selector ) {
        prepareResults();

        int[] values = new int[ 1 ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getAlias();
        for ( int index = 0; index < values.length && index < maps.size(); index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toInt(map.get(keyName));
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 0 ];


    }

    @Override
    public float firstFloat( Selector selector ) {
        prepareResults();

        float[] values = new float[ 1 ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toFloat(map.get(keyName));
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
    public short firstShort( Selector selector ) {
        prepareResults();

        short[] values = new short[ 1 ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toShort(map.get(keyName));
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
    public double firstDouble( Selector selector ) {
        prepareResults();

        double[] values = new double[ 1 ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toDouble(map.get(keyName));
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
    public byte firstByte( Selector selector ) {
        prepareResults();

        byte[] values = new byte[ 1 ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toByte(map.get(keyName));
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
    public char firstChar( Selector selector ) {
        prepareResults();

        char[] values = new char[ 1 ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = Conversions.toChar(map.get(keyName));
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
    public Object firstObject( Selector selector ) {
        prepareResults();

        Object[] values = new Object[ 1 ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = map.get( keyName );
            if ( index == 1 ) {
                break;
            }
        }
        return values[ 1 ];
    }

    @Override
    public <OBJ> OBJ firstObject( Class<OBJ> cls, Selector selector ) {
        prepareResults();

        Object[] values = new Object[ 1 ];

        List<Map<String, Object>> maps = Selector.selectFrom(list(selector), results, fields);

        String keyName = selector.getName();
        for ( int index = 0; index < values.length; index++ ) {
            Map<String, Object> map = maps.get( index );
            values[ index ] = map.get( keyName );
            if ( index == 1 ) {
                break;
            }
        }
        return ( OBJ ) values[ 1 ];
    }

    @Override
    public List<T> paginate( int start, int size ) {
        prepareResults();

        return results.subList( start, start + size );
    }

    @Override
    public List<Map<String, Object>> paginateMaps( int start, int size ) {
        prepareResults();

        List<Map<String, Object>> mapResults = new ArrayList<>();
        List<T> list = this.paginate( start, size );

        for ( T item : list ) {
            mapResults.add( toMap( item ) );
        }

        return mapResults;
    }

    @Override
    public String paginateJSON( int start, int size ) {
        prepareResults();

        throw new RuntimeException( "NOT IMPLEMENTED" );
    }

    @Override
    public int size() {
        if ( results != null ) {
            return this.results.size();
        } else {
            return totalSize;
        }
    }

    @Override
    public Iterator<T> iterator() {
        prepareResults();
        return this.results.iterator();
    }

    @Override
    public int lastSize() {
        if ( lastList == null ) {
            return 0;
        } else {
            return lastList.size();
        }

    }

    public void andResults() {

        int size = Integer.MAX_VALUE;
        List<T> finalResult = Collections.emptyList();

        for (List<T> result : allResults) {

            if (result.size()==0) {
                finalResult = Collections.emptyList();
                size=0;
                break;
            }
            if (result.size() < size) {
                size = result.size();
            }
        }


        for (List<T> result : allResults) {

            if (result.size() ==size) {
                finalResult = result;
                break;
            }
        }

        allResults.clear();
        allResults.add(finalResult);
    }


}
