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

package org.boon.sort;


import org.boon.Lists;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.*;

import static org.boon.core.Conversions.toArray;
import static org.boon.sort.UniversalComparator.universalComparator;

/**
 * Used for sorting with sorts, i.e., a collection of sorts.
 */
public class Sort {

    /** name holds the property we are sorting on. */
    private final String name;

    /** Sort type dictates ascending order or descending order. */
    private final SortType type;

    /** Sort type dictates ascending order or descending order. */
    private final boolean nullsFirst;

    /**
     * A sort is a composite object that can contain other sorts.
     */
    private List<Sort> sorts = new ArrayList<>();

    /**
     * Cache the toString and hashCode for speed.
     */
    private  String toString = null;
    private  int hashCode = -1;



    public Sort() {
        this.name = "this";
        this.type = SortType.ASCENDING;
        this.nullsFirst = false;
        this.hashCode = doHashCode();
    }

    public Sort( String name, SortType type, boolean nullsFirst ) {
        this.name = name;
        this.type = type;
        this.nullsFirst = nullsFirst;
    }


    public Sort( String name, SortType type ) {
        this.name = name;
        this.type = type;
        this.nullsFirst = false;
    }


    /**
     * A sort ends up creating a list of comparator objects.
     * There is a main comparator and then a list of composite comparators.
     */
    private List<Comparator> comparators;

    /** The main comparator. */
    private Comparator comparator;


    /** Helper method to create a Sort that is a composite of other sorts.
     * @param sorts list of child sorts
     * @return
     */
    public static Sort sorts( Sort... sorts ) {
        if ( sorts == null || sorts.length == 0 ) {
            return null;
        }

        Sort main = sorts[ 0 ];
        for ( int index = 1; index < sorts.length; index++ ) {
            main.then( sorts[ index ] );
        }
        return main;
    }

    /** Creates an ascending sort. */
    public static Sort asc( String name ) {
        return new Sort( name, SortType.ASCENDING );
    }


    /** Creates an ascending sort. */
    public static Sort sortBy( String name ) {
        return new Sort( name, SortType.ASCENDING );
    }


    /** Creates an ascending sort. */
    public static Sort sortByNullsFirst( String name ) {
        return new Sort( name, SortType.ASCENDING, true );
    }


    /** Creates a descending sort. */
    public static Sort desc( String name ) {
        return new Sort( name, SortType.DESCENDING );
    }


    /** Creates a descending sort. */
    public static Sort sortByDesc( String name ) {
        return new Sort( name, SortType.DESCENDING );
    }


    /** Creates a descending sort. */
    public static Sort sortByDescending( String name ) {
        return new Sort( name, SortType.DESCENDING );
    }

    /** Creates an descending sort. */
    public static Sort sortByDescendingNullsFirst( String name ) {
        return new Sort( name, SortType.ASCENDING, true );
    }


    /** Creates an descending sort. */
    public static Sort descNullsFirst( String name ) {
        return new Sort( name, SortType.ASCENDING, true );
    }


    public SortType getType() {
        return type;
    }

    public String getName() {
        return name;
    }


    public Sort then( Sort sort ) {
        this.sorts.add( sort );
        return this;
    }

    public Sort then( String name ) {
        this.sorts.add( new Sort( name, SortType.ASCENDING ) );
        return this;
    }

    public Sort thenAsc( String name ) {
        this.sorts.add( new Sort( name, SortType.ASCENDING ) );
        return this;
    }

    public Sort thenDesc( String name ) {
        this.sorts.add( new Sort( name, SortType.DESCENDING ) );
        return this;
    }

    /**
     * Sort if you already know the reflection fields.
     * @param list sort from list
     * @param fields sort from fields.
     */
    public void sort( List list, Map<String, FieldAccess> fields ) {
        Collections.sort( list, this.comparator( fields ) );
    }


    /**
     * Sort and you look up the reflection fields.
     * @param list
     */
    public void sort( List list ) {
        if ( list == null || list.size() == 0 ) {
            return;
        }

        Object item = list.iterator().next();

        Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject( item );
        Collections.sort( list, this.comparator( fields ) );
    }


    /**
     * Sort and you look up the reflection fields.
     * @param collection the collection to sort
     */
    public <T> Collection<T> sort( Class<T> componentClass, Collection<T> collection ) {

        if (collection instanceof List) {
            sort((List) collection);
            return collection;
        }

        if ( collection == null || collection.size() == 0 ) {
            return Collections.EMPTY_LIST;
        }


        Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject( componentClass );
        T[] array = toArray(componentClass, collection);
        Arrays.sort( array, this.comparator( fields ) );

        if (collection instanceof Set){
            return new LinkedHashSet<>( Lists.list(array));
        } else {
            return Lists.list(array);
        }
    }



    /**
     * Sort and you look up the reflection fields.
     * @param iterable the collection to sort
     */
    public <T> Iterable<T> sort( Class<T> componentClass, Iterable<T> iterable ) {

        if (iterable instanceof List) {
            sort((List) iterable);
            return iterable;
        }

        if (iterable instanceof Collection) {
            return  sort(componentClass, (Collection) iterable);
        }

        if ( iterable == null  ) {
            return Collections.EMPTY_LIST;
        }

        List<T> list = Lists.list(iterable);
        sort(list);
        return list;

    }

    /**
     * Sort and you look up the reflection fields.
     * @param array
     */
    public <T> void sort( T[] array ) {
        if ( array == null || array.length == 0 ) {
            return;
        }

        Object item = array[0];

        Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject( item );
        Arrays.sort( array, this.comparator( fields ) );
    }


    /** This is what really does the magic. This is the comparator creator. */
    public Comparator comparator( Map<String, FieldAccess> fields ) {
        if ( comparator == null ) {
            comparator = universalComparator(this.getName(), fields,
                    this.getType(), this.childComparators(fields));
        }
        return comparator;
    }

    /**
     * This creates a list of children comparators based on the child list.
     * @param fields
     * @return
     */
    private List<Comparator> childComparators( Map<String, FieldAccess> fields ) {
        if ( this.comparators == null ) {
            this.comparators = new ArrayList<>( this.sorts.size() + 1 );

            for ( Sort sort : sorts ) {
                Comparator comparator = universalComparator(
                        sort.getName(),
                        fields,
                        sort.getType(),
                        sort.childComparators(fields)
                );
                this.comparators.add( comparator );
            }
        }
        return this.comparators;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sort sort = (Sort) o;

        if (hashCode != sort.hashCode) return false;
        if (nullsFirst != sort.nullsFirst) return false;
        if (comparator != null ? !comparator.equals(sort.comparator) : sort.comparator != null) return false;
        if (comparators != null ? !comparators.equals(sort.comparators) : sort.comparators != null) return false;
        if (name != null ? !name.equals(sort.name) : sort.name != null) return false;
        if (sorts != null ? !sorts.equals(sort.sorts) : sort.sorts != null) return false;
        if (toString != null ? !toString.equals(sort.toString) : sort.toString != null) return false;
        if (type != sort.type) return false;

        return true;
    }



    @Override
    public int hashCode() {

        if (hashCode == -1) {
            hashCode =  doHashCode();
        }
        return hashCode;
    }

    public int doHashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (nullsFirst ? 1 : 0);
        result = 31 * result + (sorts != null ? sorts.hashCode() : 0);
        result = 31 * result + (toString != null ? toString.hashCode() : 0);
        result = 31 * result + hashCode;
        result = 31 * result + (comparators != null ? comparators.hashCode() : 0);
        result = 31 * result + (comparator != null ? comparator.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "Sort{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", nullsFirst=" + nullsFirst +
                ", sorts=" + sorts +
                ", toString='" + toString + '\'' +
                ", hashCode=" + hashCode +
                ", comparators=" + comparators +
                ", comparator=" + comparator +
                '}';
    }
}
