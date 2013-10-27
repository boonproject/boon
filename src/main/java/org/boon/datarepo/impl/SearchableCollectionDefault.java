package org.boon.datarepo.impl;

import org.boon.datarepo.Filter;
import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.ResultSet;
import org.boon.datarepo.SearchableCollection;
import org.boon.datarepo.impl.indexes.UniqueLookupIndex;
import org.boon.criteria.Criteria;
import org.boon.criteria.Selector;
import org.boon.criteria.Sort;
import org.boon.criteria.Visitor;
import org.boon.datarepo.spi.FilterComposer;
import org.boon.datarepo.spi.SearchIndex;
import org.boon.datarepo.spi.SearchableCollectionComposer;
import org.boon.predicates.Function;


import java.util.*;
import java.util.logging.Logger;


import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.Reflection;
import static org.boon.Lists.list;
import static org.boon.core.reflection.Reflection.toMap;

import static org.boon.core.reflection.Reflection.isArray;

public class SearchableCollectionDefault<KEY, ITEM> implements SearchableCollection<KEY, ITEM>, SearchableCollectionComposer {


    private Logger log = Logger.getLogger(RepoDefault.class.getName());

    protected Map<String, LookupIndex> lookupIndexMap = new LinkedHashMap<>();
    protected Map<String, SearchIndex> searchIndexMap = new LinkedHashMap<>();
    protected List<LookupIndex> indexes = new ArrayList<LookupIndex>();
    protected Filter filter;

    protected Map<String, FieldAccess> fields = new LinkedHashMap<>();
    protected UniqueLookupIndex<KEY, ITEM> primaryIndex;

    protected Function<ITEM, KEY> primaryKeyGetter;
    protected String primaryKeyName;


    protected boolean removeDuplication = true;


    @Override
    public boolean delete(ITEM item) {
        for (LookupIndex index : indexes) {
            index.delete((ITEM) item);
        }
        return true;
    }

    public boolean add(ITEM item) {
        for (LookupIndex index : indexes) {
            index.add(item);
        }
        return true;
    }


    public ITEM get(KEY key) {
        LookupIndex lookupIndex = primaryIndex;
        return (ITEM) lookupIndex.get(key);
    }

    public KEY getKey(ITEM item) {
        return (KEY) this.primaryKeyGetter.apply(item);
    }


    public void setRemoveDuplication(boolean removeDuplication) {
        this.removeDuplication = removeDuplication;
    }

    @Override
    public int count(KEY key, String property, int value) {

        SearchIndex index = this.searchIndexMap.get(property);

        if (index == null) {
            throw new IllegalStateException(String.format(

            "No searchIndex was found so you can't do a count for \n " +
                    "key %s \t property %s \t set %s", key, property, value));
        }

        return index.count(key);

    }

    @Override
    public int count(KEY key, String property, short value) {
        SearchIndex index = this.searchIndexMap.get(property);

        if (index == null) {
            throw new IllegalStateException(String.format("No searchIndex was found so you can't do a count for \n " +
                    "key %s \t property %s \t set %s", key, property, value));
        }

        return index.count(key);
    }

    @Override
    public int count(KEY key, String property, byte value) {
        SearchIndex index = this.searchIndexMap.get(property);

        if (index == null) {
            throw new IllegalStateException(String.format("No searchIndex was found so you can't do a count for \n " +
                    "key %s \t property %s \t set %s", key, property, value));
        }

        return index.count(key);
    }

    @Override
    public int count(KEY key, String property, long value) {
        SearchIndex index = this.searchIndexMap.get(property);

        if (index == null) {
            throw new IllegalStateException(
                    String.format("No searchIndex was found so you can't do a count for \n " +
                    "key %s \t property %s \t set %s", key, property, value));
        }

        return index.count(key);
    }

    @Override
    public int count(KEY key, String property, char value) {
        SearchIndex index = this.searchIndexMap.get(property);

        if (index == null) {
            throw new IllegalStateException(
                    String.format("No searchIndex was found so you can't do a count for \n " +
                    "key %s \t property %s \t set %s", key, property, value));
        }

        return index.count(key);
    }

    @Override
    public int count(KEY key, String property, float value) {
        SearchIndex index = this.searchIndexMap.get(property);

        if (index == null) {
            throw new IllegalStateException(
                    String.format("No searchIndex was found so you can't do a count for \n " +
                    "key %s \t property %s \t set %s", key, property, value));
        }

        return index.count(key);
    }

    @Override
    public int count(KEY key, String property, double value) {
        SearchIndex index = this.searchIndexMap.get(property);

        if (index == null) {
            throw new IllegalStateException(
                    String.format("No searchIndex was found so you can't do a count for \n " +
                    "key %s \t property %s \t set %s", key, property, value));
        }

        return index.count(key);
    }

    @Override
    public int count(KEY key, String property, Object value) {
        SearchIndex index = this.searchIndexMap.get(property);

        if (index == null) {
            throw new IllegalStateException(
                    String.format("No searchIndex was found so you can't do a count for \n " +
                    "key %s \t property %s \t set %s", key, property, value));
        }

        return index.count(key);
    }

    @Override
    public <T> T max(KEY key, String property, Class<T> type) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.max();
            if (item != null) {
                return (T) this.fields.get(property).getValue(item);
            }
        }
        return null;
    }

    @Override
    public String maxString(KEY key, String property) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.max();
            if (item != null) {
                return (String) this.fields.get(property).getObject(item);
            }
        }
        return null;
    }

    @Override
    public Number maxNumber(KEY key, String property) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.max();
            if (item != null) {
                return (Number) this.fields.get(property).getValue(item);
            }
        }
        return Double.NaN;
    }

    @Override
    public int maxInt(KEY key, String property) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.max();
            if (item != null) {
                return this.fields.get(property).getInt(item);
            }
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public long maxLong(KEY key, String property) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.max();
            if (item != null) {
                FieldAccess field = this.fields.get(property);

                if (field.getType() == long.class) {
                    return field.getLong(item);
                }
            }
        }
        return Long.MIN_VALUE;
    }

    @Override
    public double maxDouble(KEY key, String property) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.max();
            if (item != null) {
                return this.fields.get(property).getDouble(item);
            }
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public <T> T min(KEY key, String property, Class<T> type) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.min();
            if (item != null) {
                return (T) this.fields.get(property).getValue(item);
            }
        }
        return null;
    }

    @Override
    public String minString(KEY key, String property) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.min();
            if (item != null) {
                return (String) this.fields.get(property).getObject(item);
            }
        }
        return "";
    }

    @Override
    public Number minNumber(KEY key, String property) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.min();
            if (item != null) {
                return (Number) this.fields.get(property).getValue(item);
            }
        }
        return Double.NaN;
    }

    @Override
    public int minInt(KEY key, String property) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.min();
            if (item != null) {
                return this.fields.get(property).getInt(item);
            }
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public long minLong(KEY key, String property) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.min();
            if (item != null) {
                FieldAccess field = this.fields.get(property);

                if (field.getType() == long.class) {
                    return field.getLong(item);
                }
            }
        }
        return Long.MAX_VALUE;
    }

    @Override
    public double minDouble(KEY key, String property) {
        SearchIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            ITEM item = (ITEM) index.min();
            if (item != null) {
                return this.fields.get(property).getDouble(item);
            }
        }
        return Double.MAX_VALUE;
    }

    @Override
    public ResultSet<ITEM> results(Criteria... expressions) {
        return this.filter.filter(expressions);
    }

    @Override
    public List<ITEM> query(Criteria... expressions) {
        if (expressions == null || expressions.length == 0) {
            return this.all();
        } else {
            if (this.removeDuplication) {
                return (List<ITEM>) this.filter.filter(expressions).removeDuplication().asList();
            } else {
                return (List<ITEM>) this.filter.filter(expressions).asList();
            }
        }
    }

    @Override
    public List<ITEM> sortedQuery(final String sortBy, Criteria... expressions) {
        Sort asc = Sort.asc(sortBy);
        return sortedQuery(asc, expressions);
    }

    @Override
    public List<ITEM> sortedQuery(Sort sortBy, Criteria... expressions) {
        List<ITEM> results = this.query(expressions);
        sortBy.sort(results);
        return results;
    }

    @Override
    public List<Map<String, Object>> queryAsMaps(Criteria... expressions) {
        List<ITEM> items = this.query(expressions);
        List<Map<String, Object>> results = new ArrayList<>(items.size());
        for (ITEM item : items) {
            results.add(toMap(item));
        }
        return results;
    }

    @Override
    public List<Map<String, Object>> sortedQuery(final String sortBy, List<Selector> selectors, Criteria... expressions) {

        Sort asc = Sort.asc(sortBy);
        return sortedQuery(asc, selectors, expressions);
    }

    @Override
    public List<Map<String, Object>> sortedQuery(Sort sortBy, List<Selector> selectors, Criteria... expressions) {
        final List<Map<String, Object>> results = query(selectors, expressions);
        sortBy.sort(results);
        return results;
    }

    private void visit(KEY key, ITEM item, Visitor<KEY, ITEM> visitor, Object o, List<String> path, int levels) {
        if (o == null) {
            return;
        }
        levels++;
        if (levels > 20) {
            return;
        }
        visitor.visit(key, item, o, path);


        if (o.getClass().isPrimitive()) {
            return;
        }


        if (o.getClass().getName().startsWith("java")) {
            return;
        }


        if (isArray(o) || o instanceof Collection) {

            int index = 0;
            Iterator iterator = Reflection.iterator(o);
            while (iterator.hasNext()) {
                path.add(String.format("[%s]", index));
                Object objectItem = iterator.next();
                visit(key, item, visitor, objectItem, path, levels);
                path.remove(path.size() - 1);
                index++;

            }

        }

        Map<String, FieldAccess> accessorFields = Reflection.getAllAccessorFields(o.getClass());
        for (FieldAccess field : accessorFields.values()) {
            if (field.isStatic()) {
                continue;
            }
            path.add(field.getName());
            visit(key, item, visitor, field.getValue(o), path, levels);
            path.remove(path.size() - 1);

        }


    }

    @Override
    public void query(Visitor<KEY, ITEM> visitor, Criteria... expressions) {
        List<ITEM> items = this.query(expressions);
        for (ITEM item : items) {
            KEY key = (KEY) this.primaryKeyGetter.apply(item);
            int levels = 0;
            visit(key, item, visitor, item, list("root"), levels);
        }

    }

    @Override
    public void sortedQuery(Visitor<KEY, ITEM> visitor, String sortBy, Criteria... expressions) {
        Sort asc = Sort.asc(sortBy);
        sortedQuery(visitor, asc, expressions);
    }

    @Override
    public void sortedQuery(Visitor<KEY, ITEM> visitor, Sort sortBy, Criteria... expressions) {
        List<ITEM> items = this.sortedQuery(sortBy, expressions);
        for (ITEM item : items) {
            KEY key = (KEY) this.primaryKeyGetter.apply(item);
            int levels = 0;
            visit(key, item, visitor, item, list("root"), levels);
        }
    }

    @Override
    public List<Map<String, Object>> query(List<Selector> selectors, Criteria... expressions) {

        List<ITEM> results = this.query(expressions);

        return Selector.performSelection(selectors, results, fields);
    }


    public void invalidateIndex(String property, ITEM item) {


        LookupIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            index.delete(item);
        }

        index = this.lookupIndexMap.get(property);
        if (index != null) {
            index.delete(item);
        }
        filter.invalidate();

    }

    public void validateIndex(String property, ITEM item) {
        LookupIndex index = this.searchIndexMap.get(property);
        if (index != null) {
            index.add(item);
        }

        index = this.lookupIndexMap.get(property);
        if (index != null) {
            index.add(item);
        }

    }


    @Override
    public void clear() {
        for (LookupIndex index : indexes) {
            index.clear();
        }
    }


    public void setFilter(Filter filter) {
        this.filter = filter;
    }


    public void addSearchIndex(String name, SearchIndex si) {
        log.config(String.format("search index added name %s", name));
        searchIndexMap.put(name, si);
        indexes.add(si);
    }

    public void addLookupIndex(String name, LookupIndex si) {
        log.config(String.format("lookup index added name %s", name));

        lookupIndexMap.put(name, si);
        indexes.add(si);
    }

    public List<ITEM> all() {
        return primaryIndex.all();
    }


    public void setPrimaryKeyName(String primaryKey) {
        this.primaryKeyName = primaryKey;
    }

    public Collection<ITEM> toCollection() {
        return this.primaryIndex.toCollection();
    }

    public boolean isEmpty() {
        return this.primaryIndex.toCollection().isEmpty();
    }


    public Iterator<ITEM> iterator() {
        return primaryIndex.toCollection().iterator();
    }


    public Object[] toArray() {
        return primaryIndex.toCollection().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return primaryIndex.toCollection().toArray(a);
    }


    public void setPrimaryKeyGetter(Function getter) {
        log.config(String.format("primary key getter set %s", getter));

        this.primaryKeyGetter = getter;
    }

    @Override
    public void init() {
        this.primaryIndex = (UniqueLookupIndex<KEY, ITEM>) this.lookupIndexMap.get(this.primaryKeyName);
        if (filter instanceof FilterComposer) {
            FilterComposer fc = (FilterComposer) filter;
            fc.setFields(fields);
            fc.setLookupIndexMap(this.lookupIndexMap);
            fc.setSearchIndexMap(this.searchIndexMap);
            fc.setSearchableCollection(this);
            fc.init();
        }
    }

    @Override
    public void setFields(Map<String, FieldAccess> fields) {
        this.fields = fields;
    }


    public int size() {
        return primaryIndex.size();
    }

    public boolean addAll(Collection<? extends ITEM> items) {
        for (ITEM item : items) {
            this.add(item);
        }
        return true;
    }

    public boolean remove(Object o) {
        KEY key = null;
        ITEM item = null;
        try {
            key = (KEY) o;
            removeByKey(key);
        } catch (ClassCastException ex) {
            item = (ITEM) o;
            delete(item);
        }
        return true;
    }

    public void removeByKey(KEY key) {
        ITEM item = this.get(key);
        this.delete(item);
    }


    public boolean containsAll(Collection<?> c) {

        for (Object object : c) {
            KEY key = null;
            ITEM item = null;
            try {
                key = (KEY) object;
                item = get(key);
            } catch (ClassCastException ex) {
                ITEM itemArg = (ITEM) object;
                key = this.getKey(itemArg);
                item = get(key);
            }
            if (item == null) {
                return true;
            }
        }
        return false;
    }


    public boolean removeAll(Collection<?> items) {
        for (Object o : items) {
            remove(o);
        }
        return true;
    }


    public boolean retainAll(Collection<?> c) {
        for (Object object : c) {
            KEY key = null;
            ITEM item = null;
            try {
                key = (KEY) object;
                item = get(key);
            } catch (ClassCastException ex) {
                item = (ITEM) object;
            }
            if (item == null) {
                return true;
            }
        }
        return false;
    }


    public boolean contains(Object o) {
        KEY key = null;
        ITEM item = null;
        try {
            key = (KEY) o;
            item = get(key);
        } catch (ClassCastException ex) {
            ITEM itemArg = (ITEM) o;
            key = getKey(itemArg);
            item = get(key);
        }
        if (item == null) {
            return true;
        } else {
            return false;
        }

    }


}
