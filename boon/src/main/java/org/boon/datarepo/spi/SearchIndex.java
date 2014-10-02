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

package org.boon.datarepo.spi;

import org.boon.datarepo.LookupIndex;

import java.util.Comparator;
import java.util.List;

/**
 * SearchIndex
 *
 * @param <KEY>  Key we are indexing on.
 * @param <ITEM> The item that we are indexing.
 */
public interface SearchIndex<KEY, ITEM> extends LookupIndex<KEY, ITEM> {

    ITEM findFirst();

    ITEM findLast();

    KEY findFirstKey();

    KEY findLastKey();

    List<ITEM> findEquals( KEY key );

    List<ITEM> findStartsWith( KEY keyFrag );

    List<ITEM> findEndsWith( KEY keyFrag );

    List<ITEM> findContains( KEY keyFrag );

    List<ITEM> findBetween( KEY start, KEY end );

    List<ITEM> findGreaterThan( KEY key );

    List<ITEM> findLessThan( KEY key );

    List<ITEM> findGreaterThanEqual( KEY key );

    List<ITEM> findLessThanEqual( KEY key );

    ITEM min();

    ITEM max();

    int count( KEY key );

    void setComparator( Comparator<KEY> collator );


//      List <ITEM> findEquals (KEY key, int start, int length);
//      List <ITEM> findStartsWith(KEY keyFrag, int start, int length);
//      List <ITEM> findEndsWith(KEY keyFrag, int start, int length);
//      List <ITEM> findContains(KEY keyFrag, int start, int length);
//      List <ITEM> findBetween(KEY startKey, KEY endKey, int start, int length);
//      List <ITEM> findGreaterThan(KEY key, int start, int length);
//      List <ITEM> findLessThan(KEY key, int start, int length);
//      List <ITEM> findGreaterThanEqual(KEY key, int start, int length);
//      List <ITEM> findLessThanEqual(KEY key, int start, int length);

}
