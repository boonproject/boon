package org.boon.datarepo.impl.indexes;

import org.boon.datarepo.spi.SPIFactory;
import org.boon.datarepo.spi.SearchIndex;
import org.boon.predicates.Function;

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

    public UniqueSearchIndex(Class<?> keyType) {
        super(keyType);
        this.keyType = keyType;

    }

    public UniqueSearchIndex(Class<?> keyType, List<ITEM> items, Function<ITEM, KEY> keyGetter) {
        super(keyType);
        super.keyGetter = keyGetter;
        super.map
                = SPIFactory.getMapCreatorFactory().get().createMap(keyType);

        this.navigableMap = (NavigableMap<KEY, ITEM>) super.map;

        for (ITEM item : items) {
            add(item);
        }


    }

    @Override
    public void setComparator(Comparator collator) {
        this.collator = collator;
    }

    @Override
    public void init() {
        super.map
                = SPIFactory.getMapCreatorFactory().get().createNavigableMap(keyType, collator);

        this.navigableMap = (NavigableMap<KEY, ITEM>) super.map;

    }

    @Override
    public ITEM findFirst() {
        return this.navigableMap.firstEntry().getValue();
    }

    @Override
    public ITEM findLast() {
        return this.navigableMap.lastEntry().getValue();
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
    public List<ITEM> findEquals(KEY key) {
        key = getKey(key);
        return list(navigableMap.get(key));
    }

    @Override
    public List<ITEM> findStartsWith(KEY keyFrag) {
        keyFrag = getKey(keyFrag);

        List<ITEM> results;

        if (keyFrag instanceof String) {
            String start = (String) keyFrag;
            if (start.length() == 0 || start == null) {
                return Collections.EMPTY_LIST;
            }

            char endLetter = start.charAt(start.length() - 1);
            String sub = start.substring(0, start.length() - 1);

            StringBuilder after = new StringBuilder(start.length());

            after.append(sub);
            after.append((char) (endLetter + 1));

            NavigableMap<String, MultiValue<ITEM>> sortMap = (NavigableMap<String, MultiValue<ITEM>>) this.navigableMap;


            SortedMap<String, MultiValue<ITEM>> sortedSubMap = sortMap.subMap(start, after.toString());

            if (sortedSubMap.size() > 0) {
                results = new ArrayList<>();
                for (MultiValue<ITEM> values : sortedSubMap.values()) {
                    values.addTo(results);
                }
                return results;
            }
            return Collections.EMPTY_LIST;
        }
        return Collections.EMPTY_LIST;

    }

    @Override
    public List<ITEM> findEndsWith(KEY keyFrag) {
        throw new UnsupportedOperationException("findEndsWith Not supported");
    }

    @Override
    public List<ITEM> findContains(KEY keyFrag) {
        throw new UnsupportedOperationException("findContains Not supported");
    }

    @Override
    public List<ITEM> findBetween(KEY start, KEY end) {
        start = getKey(start);
        end = getKey(end);

        SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.subMap(start, end);

        return new ArrayList<>(keyMultiValueSortedMap.values());

    }


    @Override
    public List<ITEM> findGreaterThan(KEY key) {
        key = getKey(key);

        SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.tailMap(key, false);
        return new ArrayList<>(keyMultiValueSortedMap.values());
    }

    @Override
    public List<ITEM> findLessThan(KEY key) {
        key = getKey(key);
        SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.headMap(key, false);
        return new ArrayList<>(keyMultiValueSortedMap.values());
    }

    @Override
    public List<ITEM> findGreaterThanEqual(KEY key) {
        key = getKey(key);
        SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.tailMap(key);
        return new ArrayList<>(keyMultiValueSortedMap.values());
    }

    @Override
    public List<ITEM> findLessThanEqual(KEY key) {
        key = getKey(key);
        SortedMap<KEY, ITEM> keyMultiValueSortedMap = this.navigableMap.headMap(key);
        return new ArrayList<>(keyMultiValueSortedMap.values());
    }

    @Override
    public ITEM min() {
        return this.navigableMap.firstEntry().getValue();
    }

    @Override
    public ITEM max() {
        return this.navigableMap.lastEntry().getValue();
    }

    @Override
    public List<ITEM> getAll(KEY key) {
        return this.findEquals(key);
    }

    @Override
    public int size() {
        return this.navigableMap.size();
    }


    @Override
    public int count(KEY key) {
        return this.navigableMap.containsKey(key) ? 1 : 0;
    }


}
