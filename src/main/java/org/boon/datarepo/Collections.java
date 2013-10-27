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

import org.boon.core.reflection.Conversions;
import org.boon.datarepo.impl.decorators.FilterWithSimpleCache;
import org.boon.criteria.Criteria;
import org.boon.datarepo.spi.SPIFactory;
import org.boon.datarepo.spi.SearchIndex;
import org.boon.datarepo.spi.SearchableCollectionComposer;
import org.boon.predicates.Function;



import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.Reflection;

import java.util.*;


/**
 * Wraps regular collections in data repo searchable collections.
 *
 */
public class Collections {


    /**
     * $q turns a list into a querying list.
     * @param list  the list you want to convert
     * @param classes classes you want to be able to criteria.
     * @param <T> The type this criteria list will return
     * @return generic list decorated with criteria features.
     */
    public static <T> List<T> $q(final List<T> list, Class<?>... classes) {
        return listQuery(list, true, true, classes);
    }

    /**
     * $c turns a list back into a regular list.
     * This is the reverse of $q.
     *
     * @see Collections#plainList(java.util.List)
     * @param list the list
     * @param <T> the type of the list.
     * @return the new decorated list.
     */
    public static <T> List<T> $c(final List<T> list) {
        return plainList(list);
    }

    /**
     * $c turns a list back into a regular list.
     * This is the reverse of $q.
     *
     * @see Collections#$c(java.util.List)
     * @param list the list
     * @param <T> the type of the list.
     * @return the new decorated list.
     */
    private static <T> List<T> plainList(List<T> list) {
        if (list instanceof QList) {
            return ((QList) list).list;
        } else {
            return list;
        }
    }


    /**
     * listQuery turns a list into a querying list.
     * @see Collections#$q(java.util.List, Class[])
     * @param list  the list you want to convert
     * @param <T> The type this criteria list will return
     * @return generic list decorated with criteria features.
     */
    public static <T> List<T> listQuery(final List<T> list) {
        return listQuery(list, true, true);
    }

    /**
     * listQuery turns a list into a querying list.
     * @param list  the list you want to convert
     * @param classes classes you want to be able to criteria.
     * @param <T> The type this criteria list will return
     * @param useField use the field instead of the property
     * @param useUnSafe use unsafe
     * @param classes  list of classes that we can criteria against, these can be component classes
     * @return generic list decorated with criteria features.
     */
    public static <T> List<T> listQuery(final List<T> list, boolean useField, boolean useUnSafe, Class<?>... classes) {
        if (list == null || list.size() == 0) {
            return list;
        }

        SearchableCollectionComposer query = null;

        if (classes == null || classes.length == 0) {
            Class<?> clazz = list.get(0).getClass();

            query = getSearchableCollectionComposer(list, useField, useUnSafe, clazz);

        } else {
            query = getSearchableCollectionComposer(list, useField, useUnSafe, classes);

        }

        return new QList<T>(list, (SearchableCollection) query);
    }

    /**
     * Decorates a set with additional criteria capabilities.
     *
     * @param set   set to decorate
     * @param <T>   generic type
     * @return      new decorated list
     */
    public static <T> Set<T> $q(final Set<T> set) {
        return setQuery(set, true, true);
    }

    /**
     * Un-decorates a set with additional criteria capabilities.
     *
     * @param set   set to un-decorate
     * @param <T>   generic type
     * @return      new decorated list
     */
    public static <T> Set<T> $c(final Set<T> set) {
        return plainSet(set);
    }


    /**
     * Un-decorates a set with additional criteria capabilities.
     *
     * @param set   set to un-decorate
     * @param <T>   generic type
     * @return      new decorated list
     */
    private static <T> Set<T> plainSet(Set<T> set) {
        if (set instanceof QSet) {
            return ((QSet) set).set;
        } else {
            return set;
        }
    }

    /**
     * Decorates a set with additional criteria capabilities.
     *
     * @param set   set to un-decorate
     * @param <T>   generic type
     * @return      new decorated list
     */
    public static <T> Set<T> setQuery(final Set<T> set) {
        return setQuery(set, true, true);
    }

    /**
     * Decorates a set with all sorts of chocolaty richness
     * @param set
     * @param useField
     * @param useUnSafe
     * @param <T>
     * @return
     */
    public static <T> Set<T> setQuery(final Set<T> set, boolean useField, boolean useUnSafe) {
        if (set == null || set.size() == 0) {
            return set;
        }

        Class<?> clazz = set.iterator().next().getClass();

        SearchableCollectionComposer query = getSearchableCollectionComposer(set, useField, useUnSafe, clazz);

        return new QSet<T>(set, (SearchableCollection) query);
    }

    /**
     * This is the internal method that does it all. :)
     * @param set
     * @param useField
     * @param useUnSafe
     * @param classes
     * @param <T>
     * @return
     */
    private static <T> SearchableCollectionComposer getSearchableCollectionComposer(Collection set, boolean useField, boolean useUnSafe, Class<?>... classes) {
        SearchableCollectionComposer query = SPIFactory.getSearchableCollectionFactory().get();


        Map<String, FieldAccess> fields = new HashMap<>();

        for (Class<?> cls : classes) {

            Map<String, FieldAccess> fieldsSubType
                    = Reflection.getPropertyFieldAccessMap(cls, useField, useUnSafe);

            for (String sKey : fieldsSubType.keySet()) {
                if (!fields.containsKey(sKey)) {
                    fields.put(sKey, fieldsSubType.get(sKey));
                }
            }
        }

        String primaryKey = findPrimaryKey(fields);
        FieldAccess field = fields.get(primaryKey);
        Function keyGetter = createKeyGetter(field);

        query.setFields(fields);
        query.setPrimaryKeyGetter(keyGetter);
        query.setPrimaryKeyName(primaryKey);
        Filter filter = SPIFactory.getFilterFactory().get();
        query.setFilter(filter);


        LookupIndex index = SPIFactory.getUniqueLookupIndexFactory().apply(fields.get(primaryKey).getType());
        index.setKeyGetter(keyGetter);
        ((SearchableCollection) query).addLookupIndex(primaryKey, index);


        for (FieldAccess f : fields.values()) {
            if (f.getName().equals(primaryKey)) {
                continue;
            }
            if (Conversions.isBasicType(f.getType())) {
                configIndexes((SearchableCollection) query, f.getName(), fields);
            }
        }

        query.init();

        query.setFilter(new FilterWithSimpleCache(filter));

        ((SearchableCollection) query).addAll(set);
        return query;
    }


    /**
     * Allow you to criteria a criteria-able list.
     * @param list  the list you want to criteria
     * @param expressions array of expressions
     * @param <T> the type of the list
     * @return the criteria results or an empty list if the list was not a criteria-able list.
     */
    public static <T> List<T> query(final List<T> list, Criteria... expressions) {
        if (list instanceof QList) {
            QList qlist = (QList) list;
            return qlist.searchCollection().query(expressions);
        }  else {
            throw new DataRepoException("Not a criteria-able list.");
        }
    }

    /**
     * Allow you to criteria a criteria-able list.
     * @param list  the list you want to criteria
     * @param expressions array of expressions
     * @param <T> the type of the list
     * @return the criteria results or an empty list if the list was not a criteria-able list.
     */
    public static <T> List<T> sortedQuery(final List<T> list, String sortBy, Criteria... expressions) {
        if (list instanceof QList) {
            QList qlist = (QList) list;
            return qlist.searchCollection().sortedQuery(sortBy, expressions);
        }  else {
            throw new DataRepoException("Not a criteria-able list.");
        }
    }


    /**
     * Allow you to criteria a criteria-able list.
     * @param set  the set you want to criteria
     * @param expressions array of expressions
     * @param <T> the type of the list
     * @return the criteria results or an empty list if the list was not a criteria-able list.
     */
    public static <T> List<T> query(final Set<T> set, Criteria... expressions) {
        if (set instanceof QSet) {
            QSet qset = (QSet) set;
            return qset.searchCollection().query(expressions);
        }
        return null;
    }

    /**
     * Allow you to criteria a criteria-able list.
     * @param set  the set you want to criteria
     * @param expressions array of expressions
     * @param <T> the type of the list
     * @return the criteria results or an empty list if the list was not a criteria-able list.
     */
    public static <T> List<T> sortedQuery(final Set<T> set, String sortBy, Criteria... expressions) {
        if (set instanceof QSet) {
            QSet qset = (QSet) set;
            return qset.searchCollection().sortedQuery(sortBy, expressions);
        }
        return null;
    }

    /**
     * placeholder for a generic way to discover a primary key.
     * Right now the primarykey must be called id.
     *
     * @param fields fields we are going to search for the primary key
     * @return
     */
    private static String findPrimaryKey(Map<String, FieldAccess> fields) {
        return "id";
    }


    /**
     * Create key getter.
     * @param field
     * @return
     */
    private static Function createKeyGetter(final FieldAccess field) {

        Objects.requireNonNull( field, "field cannot be null" );
        return new Function() {
            @Override
            public Object apply(Object o) {

                if (Reflection.hasField(o.getClass(), field.getName())) {
                    return field.getValue(o);
                } else {
                    return null;
                }
            }
        };
    }


    /**
     * Helper class that holds an inner set and a searchable collection.
     * TODO we need a navigable version of this.
     * @param <T>
     */
    static class QSet<T> extends AbstractSet<T> implements CollectionDecorator {
        final Set<T> set;
        final SearchableCollection searchCollection;

        QSet(Set<T> set, SearchableCollection searchCollection) {
            this.set = set;
            this.searchCollection = searchCollection;
        }

        @Override
        public boolean add(T item) {
            searchCollection.add(item);
            return set.add(item);
        }

        @Override
        public boolean remove(Object item) {
            searchCollection.delete((T) item);
            return set.remove(item);
        }


        @Override
        public Iterator<T> iterator() {
            return set.iterator();
        }

        @Override
        public int size() {
            return set.size();
        }

        @Override
        public SearchableCollection searchCollection() {
            return searchCollection;
        }

        @Override
        public Collection collection() {
            return set;
        }
    }

    /**
     * @param <T>
     */
    static class QList<T> extends AbstractList<T> implements CollectionDecorator {
        List<T> list;
        SearchableCollection query;

        QList(List<T> list, SearchableCollection query) {
            this.list = list;
            this.query = query;
        }

        @Override
        public boolean add(T item) {
            query.add(item);
            return list.add(item);
        }

        @Override
        public boolean remove(Object item) {
            query.delete((T) item);
            return list.remove(item);
        }


        @Override
        public T get(int index) {
            return list.get(index);
        }


        @Override
        public int size() {
            return list.size();
        }


        @Override
        public SearchableCollection searchCollection() {
            return query;
        }

        @Override
        public Collection collection() {
            return this.list;
        }
    }


    /**
     * Configures the indexes.
     * @param query the search criteria
     * @param prop  the prop
     * @param fields  the reflected fields
     */
    private static void configIndexes(SearchableCollection query, String prop,
                                      Map<String, FieldAccess> fields) {

        SearchIndex searchIndex = SPIFactory.getSearchIndexFactory().apply(fields.get(prop).getType());
        searchIndex.init();
        Function kg = createKeyGetter(fields.get(prop));
        searchIndex.setKeyGetter(kg);
        query.addSearchIndex(prop, searchIndex);

        LookupIndex index = SPIFactory.getLookupIndexFactory().apply(fields.get(prop).getType());
        index.setKeyGetter(kg);
        query.addLookupIndex(prop, index);

    }


}