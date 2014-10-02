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

package org.boon.datarepo;

import org.boon.criteria.internal.Criteria;
import org.boon.criteria.Selector;
import org.boon.sort.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ResultSet<T> extends Iterable<T> {

    ResultSet expectOne();


    <EXPECT> ResultSet<EXPECT> expectOne( Class<EXPECT> clz );


    ResultSet expectMany();

    ResultSet expectNone();

    ResultSet expectOneOrMany();

    ResultSet removeDuplication();

    ResultSet sort( Sort sort );

    Collection<T> filter( Criteria criteria );

    ResultSet<List<Map<String, Object>>> select( Selector... selectors );

    int[] selectInts( Selector selector );

    float[] selectFloats( Selector selector );

    short[] selectShorts( Selector selector );

    double[] selectDoubles( Selector selector );

    byte[] selectBytes( Selector selector );

    char[] selectChars( Selector selector );

    Object[] selectObjects( Selector selector );

    <OBJ> OBJ[] selectObjects( Class<OBJ> cls, Selector selector );

    <OBJ> ResultSet<OBJ> selectObjectsAsResultSet( Class<OBJ> cls, Selector selector );


    Collection<T> asCollection();

    String asJSONString();

    List<Map<String, Object>> asListOfMaps();

    List<T> asList();


    <G> List<G> asList(Class<G> cls);

    Set<T> asSet();

    List<PlanStep> queryPlan();

    T firstItem();

    Map<String, Object> firstMap();

    String firstJSON();

    int firstInt( Selector selector );

    float firstFloat( Selector selector );

    short firstShort( Selector selector );

    double firstDouble( Selector selector );

    byte firstByte( Selector selector );

    char firstChar( Selector selector );

    Object firstObject( Selector selector );

    <OBJ> OBJ firstObject( Class<OBJ> cls, Selector selector );


    List<T> paginate( int start, int size );

    List<Map<String, Object>> paginateMaps( int start, int size );

    String paginateJSON( int start, int size );

    //Size can vary if you allow duplication.
    //The size can change after removeDuplication.
    int size();


}
