package org.boon.sort;

import org.boon.*;
import org.boon.core.Typ;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.Conversions;
import org.boon.core.reflection.Fields;
import org.boon.core.reflection.fields.FieldAccess;

import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Sorting {

    /**
     * Gets the logger.
     */
    private static final Logger log = Boon.configurableLogger(Sorting.class.getName());


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
            SortingInternal.sort(list, sortBy, null, ascending, nullsFirst);
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


        if (fields!=null) SortingInternal.sort(list, sortBy, fields, ascending, nullsFirst);
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
     * Sorts a list based on the natural ascending order and puts null values first.
     * @param list the list you want to sort.
     */
    public static void sortNullsFirst( List list ) {

        sort(list, "this", true, false);
    }


    /** Takes a list an an array or sorts
     *
     * @param list list to sorts
     * @param sorts what you want to sore the list by
     */
    public static void sort(List list, Sort... sorts) {
        Sort.sorts(sorts).sort(list);
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
     * Sorts a list based on the natural order descending order.
     * This puts null values last.
     * @param list the list you want to sort.
     */
    public static void sortDescNullsFirst( List list ) {
        sort( list, "this", false, true);

    }

    /**
     *
     * Sorts lists Descending
     * Nulls last.
     *      * @param list the list you want to sort
     * @param sortBy what you want to sort the list by
     */
    public static void sort( List list, String sortBy ) {
        sort(list, sortBy, true, false);
    }


    /**
     *
     * Sorts lists Descending
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
     * Sorts lists Descending
     * @param list the list you want to sort
     * @param sortBy what you want to sort the list by
     */
    public static void sortDesc( List list, String sortBy ) {
        sort(list, sortBy, false, false);
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
        } else if ( Typ.isComparable( value1 ) ) {
            Comparable c1 = Conversions.comparable(value1);
            Comparable c2 = Conversions.comparable(value2);
            return c1.compareTo( c2 );
        } else {
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
