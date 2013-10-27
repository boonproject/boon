/**
 * Copyright 2013 Rick Hightower
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.boon.datarepo;

import org.boon.criteria.Criteria;
import org.boon.criteria.Selector;
import org.boon.criteria.Sort;
import org.boon.criteria.Visitor;
import org.boon.datarepo.spi.SearchIndex;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SearchableCollection<KEY, ITEM> extends Collection<ITEM> {

    ITEM get(KEY key);

    KEY getKey(ITEM item);

    void invalidateIndex(String property, ITEM item);

    void validateIndex(String property, ITEM item);


    int count(KEY key, String property, int value);

    int count(KEY key, String property, short value);

    int count(KEY key, String property, byte value);

    int count(KEY key, String property, long value);

    int count(KEY key, String property, char value);

    int count(KEY key, String property, float value);

    int count(KEY key, String property, double value);

    int count(KEY key, String property, Object value);

    <T> T max(KEY key, String property, Class<T> type);

    String maxString(KEY key, String property);

    Number maxNumber(KEY key, String property);

    int maxInt(KEY key, String property);

    long maxLong(KEY key, String property);

    double maxDouble(KEY key, String property);

    <T> T min(KEY key, String property, Class<T> type);

    String minString(KEY key, String property);

    Number minNumber(KEY key, String property);

    int minInt(KEY key, String property);

    long minLong(KEY key, String property);

    double minDouble(KEY key, String property);


    ResultSet<ITEM> results(Criteria... expressions);

    List<ITEM> query(Criteria... expressions);

    List<ITEM> sortedQuery(String sortBy, Criteria... expressions);

    List<ITEM> sortedQuery(Sort sortBy, Criteria... expressions);

    List<Map<String, Object>> queryAsMaps(Criteria... expressions);

    List<Map<String, Object>> query(List<Selector> selectors, Criteria... expressions);

    List<Map<String, Object>> sortedQuery(String sortBy, List<Selector> selectors, Criteria... expressions);

    List<Map<String, Object>> sortedQuery(Sort sortBy, List<Selector> selectors, Criteria... expressions);

    void query(Visitor<KEY, ITEM> visitor, Criteria... expressions);

    void sortedQuery(Visitor<KEY, ITEM> visitor, String sortBy, Criteria... expressions);

    void sortedQuery(Visitor<KEY, ITEM> visitor, Sort sortBy, Criteria... expressions);


    boolean delete(ITEM item);


    void addSearchIndex(String name, SearchIndex<?, ?> si);

    void addLookupIndex(String name, LookupIndex<?, ?> si);


    List<ITEM> all();

    void removeByKey(KEY key);

}
