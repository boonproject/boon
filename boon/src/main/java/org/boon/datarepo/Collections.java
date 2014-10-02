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

import org.boon.core.Typ;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.Fields;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.criteria.internal.Criteria;
import org.boon.datarepo.impl.decorators.FilterWithSimpleCache;
import org.boon.datarepo.spi.SPIFactory;
import org.boon.datarepo.spi.SearchIndex;
import org.boon.datarepo.spi.SearchableCollectionComposer;
import org.boon.core.Function;

import java.util.*;

import static org.boon.Exceptions.requireNonNull;


/**
 * Wraps regular collections in data repo searchable collections.
 */
public class Collections {


    /**
     * $q turns a listStream into a querying listStream.
     *
     * @param list    the listStream you want to convert
     * @param classes classes you want to be able to criteria.
     * @param <T>     The type this criteria listStream will return
     * @return generic listStream decorated with criteria features.
     */
    public static <T> List<T> $q( final List<T> list, Class<?>... classes ) {
        return listQuery( list, true, true, classes );
    }

    /**
     * $c turns a listStream back into a regular listStream.
     * This is the reverse of $q.
     *
     * @param list the listStream
     * @param <T>  the type of the listStream.
     * @return the new decorated listStream.
     * @see Collections#plainList(java.util.List)
     */
    public static <T> List<T> $c( final List<T> list ) {
        return plainList( list );
    }

    /**
     * $c turns a listStream back into a regular listStream.
     * This is the reverse of $q.
     *
     * @param list the listStream
     * @param <T>  the type of the listStream.
     * @return the new decorated listStream.
     * @see Collections#$c(java.util.List)
     */
    private static <T> List<T> plainList( List<T> list ) {
        if ( list instanceof QList ) {
            return ( ( QList ) list ).list;
        } else {
            return list;
        }
    }


    /**
     * listQuery turns a listStream into a querying listStream.
     *
     * @param list the listStream you want to convert
     * @param <T>  The type this criteria listStream will return
     * @return generic listStream decorated with criteria features.
     * @see Collections#$q(java.util.List, Class[])
     */
    public static <T> List<T> listQuery( final List<T> list ) {
        return listQuery( list, true, true );
    }

    /**
     * listQuery turns a listStream into a querying listStream.
     *
     * @param list      the listStream you want to convert
     * @param classes   classes you want to be able to criteria.
     * @param <T>       The type this criteria listStream will return
     * @param useField  use the field instead of the property
     * @param useUnSafe use unsafe
     * @param classes   listStream of classes that we can criteria against, these can be component classes
     * @return generic listStream decorated with criteria features.
     */
    public static <T> List<T> listQuery( final List<T> list, boolean useField, boolean useUnSafe, Class<?>... classes ) {
        if ( list == null || list.size() == 0 ) {
            return list;
        }

        SearchableCollectionComposer query = null;

        if ( classes == null || classes.length == 0 ) {
            Class<?> clazz = list.get( 0 ).getClass();

            query = getSearchableCollectionComposer( list, useField, useUnSafe, clazz );

        } else {
            query = getSearchableCollectionComposer( list, useField, useUnSafe, classes );

        }

        return new QList<T>( list, ( SearchableCollection ) query );
    }

    /**
     * Decorates a set with additional criteria capabilities.
     *
     * @param set set to decorate
     * @param <T> generic type
     * @return new decorated listStream
     */
    public static <T> Set<T> $q( final Set<T> set ) {
        return setQuery( set, true, true );
    }

    /**
     * Un-decorates a set with additional criteria capabilities.
     *
     * @param set set to un-decorate
     * @param <T> generic type
     * @return new decorated listStream
     */
    public static <T> Set<T> $c( final Set<T> set ) {
        return plainSet( set );
    }


    /**
     * Un-decorates a set with additional criteria capabilities.
     *
     * @param set set to un-decorate
     * @param <T> generic type
     * @return new decorated listStream
     */
    private static <T> Set<T> plainSet( Set<T> set ) {
        if ( set instanceof QSet ) {
            return ( ( QSet ) set ).set;
        } else {
            return set;
        }
    }

    /**
     * Decorates a set with additional criteria capabilities.
     *
     * @param set set to un-decorate
     * @param <T> generic type
     * @return new decorated listStream
     */
    public static <T> Set<T> setQuery( final Set<T> set ) {
        return setQuery( set, true, true );
    }

    /**
     * Decorates a set with all sorts of chocolaty richness
     *
     * @param set
     * @param useField
     * @param useUnSafe
     * @param <T>
     * @return
     */
    public static <T> Set<T> setQuery( final Set<T> set, boolean useField, boolean useUnSafe ) {
        if ( set == null || set.size() == 0 ) {
            return set;
        }

        Class<?> clazz = set.iterator().next().getClass();

        SearchableCollectionComposer query = getSearchableCollectionComposer( set, useField, useUnSafe, clazz );

        return new QSet<T>( set, ( SearchableCollection ) query );
    }

    /**
     * This is the internal method that does it all. :)
     *
     * @param set
     * @param useField
     * @param useUnSafe
     * @param classes
     * @param <T>
     * @return
     */
    private static <T> SearchableCollectionComposer getSearchableCollectionComposer( Collection set, boolean useField, boolean useUnSafe, Class<?>... classes ) {
        SearchableCollectionComposer query = SPIFactory.getSearchableCollectionFactory().get();


        Map<String, FieldAccess> fields = new LinkedHashMap<>();

        for ( Class<?> cls : classes ) {

            Map<String, FieldAccess> fieldsSubType
                    = BeanUtils.getFieldsFromObject( cls );

            for ( String sKey : fieldsSubType.keySet() ) {
                if ( !fields.containsKey( sKey ) ) {
                    fields.put( sKey, fieldsSubType.get( sKey ) );
                }
            }


        }

        String primaryKey = findPrimaryKey( fields );
        FieldAccess field = fields.get( primaryKey );
        Function keyGetter = createKeyGetter( field );

        query.setFields( fields );
        query.setPrimaryKeyGetter( keyGetter );
        query.setPrimaryKeyName( primaryKey );
        Filter filter = SPIFactory.getFilterFactory().get();
        query.setFilter( filter );


        LookupIndex index = SPIFactory.getUniqueLookupIndexFactory().apply( fields.get( primaryKey ).type() );
        index.setKeyGetter( keyGetter );
        ( ( SearchableCollection ) query ).addLookupIndex( primaryKey, index );


        for ( FieldAccess f : fields.values() ) {
            if ( f.name().equals( primaryKey ) ) {
                continue;
            }
            if ( Typ.isBasicType( f.type() ) ) {
                configIndexes( ( SearchableCollection ) query, f.name(), fields );
            }
        }

        query.init();

        query.setFilter( new FilterWithSimpleCache( filter ) );

        ( ( SearchableCollection ) query ).addAll( set );
        return query;
    }


    /**
     * Allow you to criteria a criteria-able listStream.
     *
     * @param list        the listStream you want to criteria
     * @param expressions array of expressions
     * @param <T>         the type of the listStream
     * @return the criteria results or an empty listStream if the listStream was not a criteria-able listStream.
     */
    public static <T> List<T> query( final List<T> list, Criteria... expressions ) {
        if ( list instanceof QList ) {
            QList qlist = ( QList ) list;
            return qlist.searchCollection().query( expressions );
        } else {
            throw new DataRepoException( "Not a criteria-able listStream." );
        }
    }

    /**
     * Allow you to criteria a criteria-able listStream.
     *
     * @param list        the listStream you want to criteria
     * @param expressions array of expressions
     * @param <T>         the type of the listStream
     * @return the criteria results or an empty listStream if the listStream was not a criteria-able listStream.
     */
    public static <T> List<T> sortedQuery( final List<T> list, String sortBy, Criteria... expressions ) {
        if ( list instanceof QList ) {
            QList qlist = ( QList ) list;
            return qlist.searchCollection().sortedQuery( sortBy, expressions );
        } else {
            throw new DataRepoException( "Not a criteria-able listStream." );
        }
    }


    /**
     * Allow you to criteria a criteria-able listStream.
     *
     * @param set         the set you want to criteria
     * @param expressions array of expressions
     * @param <T>         the type of the listStream
     * @return the criteria results or an empty listStream if the listStream was not a criteria-able listStream.
     */
    public static <T> List<T> query( final Set<T> set, Criteria... expressions ) {
        if ( set instanceof QSet ) {
            QSet qset = ( QSet ) set;
            return qset.searchCollection().query( expressions );
        }
        return null;
    }

    /**
     * Allow you to criteria a criteria-able listStream.
     *
     * @param set         the set you want to criteria
     * @param expressions array of expressions
     * @param <T>         the type of the listStream
     * @return the criteria results or an empty listStream if the listStream was not a criteria-able listStream.
     */
    public static <T> List<T> sortedQuery( final Set<T> set, String sortBy, Criteria... expressions ) {
        if ( set instanceof QSet ) {
            QSet qset = ( QSet ) set;
            return qset.searchCollection().sortedQuery( sortBy, expressions );
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
    private static String findPrimaryKey( Map<String, FieldAccess> fields ) {
        return "id";
    }


    /**
     * Create key getter.
     *
     * @param field
     * @return
     */
    private static Function createKeyGetter( final FieldAccess field ) {

        requireNonNull( field, "field cannot be null" );
        return new Function() {
            @Override
            public Object apply( Object o ) {

                if ( Fields.hasField( o.getClass(), field.name() ) ) {
                    return field.getValue( o );
                } else {
                    return null;
                }
            }
        };
    }


    /**
     * Helper class that holds an inner set and a searchable collection.
     * TODO we need a navigable version of this.
     *
     * @param <T>
     */
    static class QSet<T> extends AbstractSet<T> implements CollectionDecorator {
        final Set<T> set;
        final SearchableCollection searchCollection;

        QSet( Set<T> set, SearchableCollection searchCollection ) {
            this.set = set;
            this.searchCollection = searchCollection;
        }

        @Override
        public boolean add( T item ) {
            searchCollection.add( item );
            return set.add( item );
        }

        @Override
        public boolean remove( Object item ) {
            searchCollection.delete( ( T ) item );
            return set.remove( item );
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

        QList( List<T> list, SearchableCollection query ) {
            this.list = list;
            this.query = query;
        }

        @Override
        public boolean add( T item ) {
            query.add( item );
            return list.add( item );
        }

        @Override
        public boolean remove( Object item ) {
            query.delete( ( T ) item );
            return list.remove( item );
        }


        @Override
        public T get( int index ) {
            return list.get( index );
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
     *
     * @param query  the search criteria
     * @param prop   the prop
     * @param fields the reflected fields
     */
    private static void configIndexes( SearchableCollection query, String prop,
                                       Map<String, FieldAccess> fields ) {

        SearchIndex searchIndex = SPIFactory.getSearchIndexFactory().apply( fields.get( prop ).type() );
        searchIndex.init();
        Function kg = createKeyGetter( fields.get( prop ) );
        searchIndex.setKeyGetter( kg );
        query.addSearchIndex( prop, searchIndex );

        LookupIndex index = SPIFactory.getLookupIndexFactory().apply( fields.get( prop ).type() );
        index.setKeyGetter( kg );
        query.addLookupIndex( prop, index );

    }


}