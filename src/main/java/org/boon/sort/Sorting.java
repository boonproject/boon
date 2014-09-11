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

import org.boon.*;
import org.boon.core.Typ;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.Conversions;
import org.boon.core.reflection.Fields;
import org.boon.core.reflection.fields.FieldAccess;

import java.text.Collator;
import java.util.*;

import static org.boon.core.Conversions.toArray;

public class Sorting {

    /**
     * Gets the logger.
     */
    private static final Logger log = Boon.configurableLogger(Sorting.class.getName());


    /** Takes a list an an array or sorts
     *
     * @param list list to sorts
     * @param sorts what you want to sore the list by
     */
    public static void sort(List list, Sort... sorts) {
        Sort.sorts(sorts).sort(list);
    }



    /**
     * Sort a list.
     * @param list the list you want to sort
     * @param sortBy what you want to sort the list by
     * @param ascending do you want ascending order
     * @param nullsFirst do you want nulls first
     */
    public static void sort( List list, String sortBy, boolean ascending, boolean nullsFirst ) {
        if ( list == null || list.size() == 0 ) {
            return;
        }

        if (sortBy.equals("this")) {

            Collections.sort(list, thisUniversalComparator(ascending, nullsFirst));
            return;
        }
        Iterator iterator = list.iterator();
        Object object = iterator.next();

        Map<String, FieldAccess> fields = null;

        if (object != null) {
          fields = BeanUtils.getFieldsFromObject( object );
        } else {
            while(iterator.hasNext()) {

                object = iterator.next();
                if (object!=null) {
                    fields = BeanUtils.getFieldsFromObject( object );
                    break;
                }
            }

        }


        if (fields!=null) {

            final FieldAccess field = fields.get( sortBy );

            if ( field != null ) {

                Collections.sort( list, Sorting.universalComparator(field, ascending, nullsFirst) );

            }
        }
    }


    /**
     * Sort collection.
     * @param collection the collection you want to sort
     * @param sortBy what you want to sort the list by
     * @param ascending do you want ascending order
     * @param nullsFirst do you want nulls first
     */
    public static <V> Collection<V>  sort( Class<V> componentType, Collection<V> collection, String sortBy, boolean ascending, boolean nullsFirst ) {

        if (collection instanceof List) {
            sort ((List) collection, sortBy, ascending, nullsFirst);
            return collection;
        } else {
            V[] array = toArray(componentType, collection);
            sort(array, sortBy, ascending, nullsFirst);
            if (collection instanceof LinkedHashSet) {
                return new LinkedHashSet<>(Lists.list(array));
            } else {
                return Lists.list(array);
            }
        }
    }


    /**
     * Sort map entries.
     * @param map  the map entries you want to sort
     * @param sortBy what you want to sort the list by
     * @param ascending do you want ascending order
     * @param nullsFirst do you want nulls first
     */
    public static <K, V> Collection<Map.Entry<K, V>>  sortEntries( Class<V> componentType, Map<K, V> map,
                                                            String sortBy, boolean ascending, boolean nullsFirst ) {

        return sort ((Class) componentType, (Collection) map.entrySet() , sortBy, ascending, nullsFirst);

    }

    /**
     * Sort map values.
     * @param map  the map entries you want to sort
     * @param sortBy what you want to sort the list by
     * @param ascending do you want ascending order
     * @param nullsFirst do you want nulls first
     */
    public static <K, V> Collection<Map.Entry<K, V>>  sortValues( Class<V> componentType, Map<K, V> map,
                                                                   String sortBy, boolean ascending, boolean nullsFirst ) {

        return sort ((Class) componentType, (Collection) map.values() , sortBy, ascending, nullsFirst);

    }


    /**
     * Sort map keys.
     * @param map  the map entries you want to sort
     * @param sortBy what you want to sort the list by
     * @param ascending do you want ascending order
     * @param nullsFirst do you want nulls first
     */
    public static <K, V> Collection<Map.Entry<K, V>>  sortKeys( Class<V> componentType, Map<K, V> map,
                                                                  String sortBy, boolean ascending, boolean nullsFirst ) {

        return sort ((Class) componentType, (Collection) map.keySet() , sortBy, ascending, nullsFirst);

    }





    /**
     * Sort collection.
     * @param iterable the iterable you want to sort
     * @param sortBy what you want to sort the list by
     * @param ascending do you want ascending order
     * @param nullsFirst do you want nulls first
     */
    public static <V> Iterable<V> sort( Class<V> componentType, Iterable<V> iterable, String sortBy, boolean ascending, boolean nullsFirst ) {

        if (iterable instanceof List) {
            sort ((List) iterable, sortBy, ascending, nullsFirst);
            return iterable;
        } else if (iterable instanceof  Collection) {
            return sort (componentType, (Collection<V>) iterable, sortBy, ascending, nullsFirst);
        } else {
            List<V> list = Lists.list(iterable);
            sort ( list, sortBy, ascending, nullsFirst);
            return list;
        }
    }

    /**
     * Sort an array.
     * @param array the list you want to sort
     * @param sortBy what you want to sort the list by
     * @param ascending do you want ascending order
     * @param nullsFirst do you want nulls first
     */
    public static <T> void  sort( T[] array, String sortBy, boolean ascending, boolean nullsFirst ) {
        if ( array == null || array.length == 0 ) {
            return;
        }

        if (sortBy.equals("this")) {

            Arrays.sort(array, thisUniversalComparator(ascending, nullsFirst));
            return;
        }

        Object object = array[0];

        Map<String, FieldAccess> fields = null;

        if (object != null) {
            fields = BeanUtils.getFieldsFromObject( object );
        } else {
            for (int index=1; index< array.length; index++) {

                object = array[index];
                if (object!=null) {
                    fields = BeanUtils.getFieldsFromObject( object );
                    break;
                }
            }

        }


        if (fields!=null) {

            final FieldAccess field = fields.get( sortBy );

            if ( field != null ) {

                Arrays.sort( array, Sorting.universalComparator(field, ascending, nullsFirst) );

            }
        }
    }


    /**
     * Sorts a list based on the natural ascending order.
     * This puts null values last.
     * @param list the list you want to sort.
     */
    public static void sort( List list ) {
        sort( list, "this", true, false);
    }

    /**
     * Sorts a array based on the natural ascending order.
     * This puts null values last.
     * @param array the list you want to sort.
     */
    public static <T> void sort( T[] array ) {
        sort( array, "this", true, false);
    }

    /**
     * Sorts a collection based on the natural ascending order.
     * This puts null values last.
     * @param collection the list you want to sort.
     */
    public static <T> Collection<T> sort( Class<T> componentType, Collection<T> collection ) {
        return sort( componentType, collection, "this", true, false);
    }

    /**
     * Sorts a iterable based on the natural ascending order.
     * This puts null values last.
     * @param iterable the list you want to sort.
     */
    public static <T> Iterable<T> sort( Class<T> componentType, Iterable<T> iterable ) {
        return sort( componentType, iterable, "this", true, false);
    }




    /**
     * Sorts a list based on the natural ascending order and puts null values first.
     * @param list the list you want to sort.
     */
    public static void sortNullsFirst( List list ) {

        sort(list, "this", true, true);
    }

    /**
     * Sorts an array based on the natural ascending order and puts null values first.
     * @param array the list you want to sort.
     */
    public static <T> void sortNullsFirst( T[] array ) {

        sort(array, "this", true, true);
    }

    /**
     * Sorts a collection based on the natural ascending order.
     * This puts null values first.
     * @param collection the collection you want to sort.
     */
    public static <T> Collection<T> sortNullsFirst( Class<T> componentType, Collection<T> collection ) {
        return sort( componentType, collection, "this", true, true);
    }


    /**
     * Sorts an iterable based on the natural ascending order.
     * This puts null values first.
     * @param iterable the list you want to sort.
     */
    public static <T> Iterable<T> sortNullsFirst( Class<T> componentType, Iterable<T> iterable ) {
        return sort( componentType, iterable, "this", true, true);
    }





    /**
     * Sorts a list based on the natural order descending order.
     * This puts null values last.
     * @param list the list you want to sort.
     */
    public static void sortDesc( List list ) {
        sort( list, "this", false, false);

    }

    /**
     * Sorts a array based on the natural order descending order.
     * This puts null values last.
     * @param array the list you want to sort.
     */
    public static <T> void sortDesc( T[] array ) {
        sort( array, "this", false, false);

    }

    /**
     * Sorts a iterable based on the natural descending order.
     * This puts null values last.
     * @param iterable the iterable you want to sort.
     */
    public static <T> Iterable<T> sortDesc( Class<T> componentType, Iterable<T> iterable ) {
        return sort( componentType, iterable, "this", false, false);
    }

    /**
     * Sorts a collection based on the natural descending order.
     * This puts null values last.
     * @param collection the collection you want to sort.
     */
    public static <T> Collection<T> sortDesc( Class<T> componentType, Collection<T> collection ) {
        return sort( componentType, collection, "this", false, false);
    }





    /**
     * Sorts a list based on the natural order descending order.
     * This puts null values first.
     * @param list the list you want to sort.
     */
    public static void sortDescNullsFirst( List list ) {
        sort( list, "this", false, true);

    }



    /**
     * Sorts a array based on the natural order descending order.
     * This puts null values first.
     * @param array the list you want to sort.
     */
    public static <T> void sortDescNullsFirst( T[] array ) {
        sort( array, "this", false, true);

    }

    /**
     * Sorts a iterable based on the natural descending order.
     * This puts null values first.
     * @param iterable the iterable you want to sort.
     */
    public static <T> Iterable<T> sortDescNullsFirst( Class<T> componentType, Iterable<T> iterable ) {
        return sort( componentType, iterable, "this", false, true);
    }

    /**
     * Sorts a collection based on the natural descending order.
     * This puts null values first.
     * @param collection the collection you want to sort.
     */
    public static <T> Collection<T> sortDescNullsFirst( Class<T> componentType, Collection<T> collection ) {
        return sort( componentType, collection, "this", false, true);
    }


    /**
     *
     * Sorts lists ascending
     * Nulls last.
     * @param list the list you want to sort
     * @param sortBy what you want to sort the list by
     */
    public static void sort( List list, String sortBy ) {
        sort(list, sortBy, true, false);
    }




    /**
     *
     * Sorts array ascending
     * Nulls last.
     * @param array the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> void sort( T[] array, String sortBy ) {
        sort( array, sortBy, true, false);

    }

    /**
     *
     * Sorts iterable ascending
     * Nulls last.
     * @param iterable the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> Iterable<T> sort( Class<T> componentType, Iterable<T> iterable, String sortBy  ) {
        return sort( componentType, iterable, sortBy, true, false);
    }


    /**
     *
     * Sorts collection ascending
     * Nulls last.
     * @param collection the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> Collection<T> sort( Class<T> componentType, Collection<T> collection, String sortBy ) {
        return sort( componentType, collection, sortBy, true, false);
    }



    /**
     *
     * Sorts lists Ascending Null first
     * Nulls first.
     *
     * @param list the list you want to sort
     * @param sortBy what you want to sort the list by
     */
    public static void sortNullsFirst( List list, String sortBy ) {
        sort( list, sortBy, true, true);
    }



    /**
     *
     * Sorts array ascending
     * Nulls first.
     * @param array the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> void sortNullsFirst( T[] array, String sortBy ) {
        sort( array, sortBy, true, true);

    }

    /**
     *
     * Sorts iterable ascending
     * Nulls first.
     * @param iterable the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> Iterable<T> sortNullsFirst( Class<T> componentType, Iterable<T> iterable, String sortBy  ) {
        return sort( componentType, iterable, sortBy, true, true);
    }


    /**
     *
     * Sorts collection ascending
     * Nulls first.
     * @param collection the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> Collection<T> sortNullsFirst( Class<T> componentType, Collection<T> collection, String sortBy ) {
        return sort( componentType, collection, sortBy, true, true);
    }


    /**
     *
     * Sorts lists Descending
     * @param list the list you want to sort
     * @param sortBy what you want to sort the list by
     */
    public static void sortDesc( List list, String sortBy ) {
        sort(list, sortBy, false, false);
    }




    /**
     *
     * Sorts array Descending
     * Nulls last.
     * @param array the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> void sortDesc( T[] array, String sortBy ) {
        sort( array, sortBy, false, false);

    }

    /**
     *
     * Sorts iterable ascending
     * Nulls last.
     * @param iterable the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> Iterable<T> sortDesc( Class<T> componentType, Iterable<T> iterable, String sortBy  ) {
        return sort( componentType, iterable, sortBy, false, false);
    }


    /**
     *
     * Sorts collection ascending
     * Nulls last.
     * @param collection the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> Collection<T> sortDesc( Class<T> componentType, Collection<T> collection, String sortBy ) {
        return sort( componentType, collection, sortBy, false, false);
    }



    /**
     *
     * Sorts lists Descending
     * @param list the list you want to sort
     * @param sortBy what you want to sort the list by
     */
    public static void sortDescNullsFirst( List list, String sortBy ) {
        sort( list, sortBy, false, true);
    }


    /**
     *
     * Sorts array Descending
     * Nulls first.
     * @param array the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> void sortDescNullsFirst( T[] array, String sortBy ) {
        sort( array, sortBy, false, true);

    }

    /**
     *
     * Sorts iterable ascending
     * Nulls first.
     * @param iterable the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> Iterable<T> sortDescNullsFirst( Class<T> componentType, Iterable<T> iterable, String sortBy  ) {
        return sort( componentType, iterable, sortBy, false, true);
    }


    /**
     *
     * Sorts collection ascending
     * Nulls first.
     * @param collection the list you want to sort
     * @param sortBy what you want to sort the array by
     */
    public static <T> Collection<T> sortDescNullsFirst( Class<T> componentType, Collection<T> collection, String sortBy ) {
        return sort( componentType, collection, sortBy, false, true);
    }



    /**
     * This creates the universal comparator object which is used by the sort work horse.
     *
     * @param field The field we are sorting on.
     * @param ascending if this should be ascending or descending.
     * @return
     */
    public static Comparator universalComparator( final FieldAccess field, final boolean ascending,
                                                  final boolean nullsFirst) {
        return new Comparator() {
            @Override
            public int compare( Object o1, Object o2 ) {
                Object value1 = null;
                Object value2 = null;

                if ( ascending ) {
                    value1 = field.getValue( o1 );
                    value2 = field.getValue( o2 );
                } else {
                    value1 = field.getValue( o2 );
                    value2 = field.getValue( o1 );
                }
                return Sorting.compare(value1, value2, nullsFirst);
            }
        };
    }



    /**
     * This creates the universal comparator object used for "this".
     *
     * @param ascending if this should be ascending or descending.
     * @return
     */
    public static Comparator thisUniversalComparator( final boolean ascending,
                                                  final boolean nullsFirst) {
        return new Comparator() {
            @Override
            public int compare( Object o1, Object o2 ) {
                Object value1;
                Object value2;


                if ( ascending ) {
                    value1 =  ( o1 );
                    value2 =  ( o2 );
                } else {
                    value1 =  ( o2 );
                    value2 =  ( o1 );
                }

                return Sorting.compare(value1, value2, nullsFirst);
            }
        };
    }

    /**
     * This compares two values.
     * @param value1 value1
     * @param value2 value2
     * @return
     */
    public static int compare( Object value1, Object value2 ) {
        return compare(value1, value2, false);
    }

    /**
     * This compares two values.
     * If the objects are strings (CharSequence) they are always compared lexicographically.
     * If the objects are comparable they are always compared using a natural order.
     *
     * @param value1 value1
     * @param value2 value2
     * @param nullsLast put nulls last
     *
     * @return
     */
    public static int compare( Object value1, Object value2, boolean nullsLast ) {

        if ( value1 == null && value2 == null ) {
            return 0;
        } else if ( value1 == null && value2 != null ) {
            return nullsLast ? -1 : 1;
        } else if ( value1 != null && value2 == null ) {
            return nullsLast ? 1 : -1;
        }


        /** Objects are string like so compare using collator. */
        if ( value1 instanceof CharSequence ) {
            String str1 = Conversions.toString( value1 );
            String str2 = Conversions.toString( value2 );
            Collator collator = Collator.getInstance();
            return collator.compare( str1, str2 );

        /** Objects are comparable, yeah! */
        } else if ( Typ.isComparable( value1 ) && value1.getClass() == value2.getClass()) {
            Comparable c1 = Conversions.comparable(value1);
            Comparable c2 = Conversions.comparable(value2);
            return c1.compareTo( c2 );
        } else if (  value1 instanceof Integer && value2 instanceof Integer ) {
            Comparable c1 = Conversions.comparable(value1);
            Comparable c2 = Conversions.comparable(value2);
            return c1.compareTo( c2 );
        }
        else if (  value1 instanceof Double && value2 instanceof Double ) {
            Comparable c1 = Conversions.comparable(value1);
            Comparable c2 = Conversions.comparable(value2);
            return c1.compareTo( c2 );
        }
        else if (  value1 instanceof Long && value2 instanceof Long ) {
            Comparable c1 = Conversions.comparable(value1);
            Comparable c2 = Conversions.comparable(value2);
            return c1.compareTo( c2 );
        }
        else if (  value1 instanceof Number && value2 instanceof Number ) {
            Double c1 = Conversions.toDouble(value1);
            Double c2 = Conversions.toDouble(value2);
            return c1.compareTo( c2 );
        }
         else {
            /** Object are neither String like or comparable.
             * Ours it not to reason why, ours it to do or die.
             * Find the first sortable field and sort by that.
             * */
            String name = Fields.getSortableField( value1 );
            String sv1 = ( String ) BeanUtils.getPropByPath( value1, name );
            String sv2 = ( String ) BeanUtils.getPropByPath( value2, name );
            return Sorting.compare(sv1, sv2);

        }

    }


}
