package org.boon.sort;

import org.boon.Exceptions;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.value.ValueList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.boon.sort.Sorting.thisUniversalComparator;

/**
 * Created by Richard on 3/8/14.
 */
public class SortingInternal {


    /**
     * This is the work horse. It does all of the sorting work for the simple cases.
     * Nulls are last by default.
     * @param list the list you want to sort.
     * @param sortBy what you want to sort the list by.
     * @param fields the reflection fields
     * @param ascending true for ascending
     */
    public static void sort( List list, String sortBy, Map<String, FieldAccess> fields, boolean ascending) {

        sort(list, sortBy, fields, ascending, false);

    }

    /**
     * This is the work horse. It does all of the sorting work for the simple cases.
     * @param list the list you want to sort.
     * @param sortBy what you want to sort the list by.
     * @param fields the reflection fields
     * @param ascending true for ascending
     */
    public static void sort( List list, String sortBy, Map<String, FieldAccess> fields, boolean ascending,
                             boolean nullsFirst) {

        try {


            /* If this list is null or empty, we have nothing to do so return. */
            if ( list == null || list.size() == 0 ) {
                return;
            }

            /* Grab the first item in the list and see what it is. */
            Object o = list.get( 0 );

            /* if the sort by string is is this, and the object is comparable then use the objects
            themselves for the sort.
             */
            if ( sortBy.equals( "this" )  ) {

                Collections.sort(list, thisUniversalComparator(ascending, nullsFirst));
                return;
            }

            /* If you did sort by this, then sort by the field. */

            final FieldAccess field = fields.get( sortBy );

            if ( field != null ) {

                Collections.sort( list, Sorting.universalComparator(field, ascending, nullsFirst) );

            }

        } catch (Exception ex) {
            Exceptions.handle(ex, "list", list, "\nsortBy", sortBy, "fields", fields, "ascending", ascending,
            "nullFirst", nullsFirst);
        }
    }

}
