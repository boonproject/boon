package org.boon.sort;

import org.boon.Exceptions;
import org.boon.Str;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.Chr;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.boon.core.reflection.BeanUtils.indexOf;

/**
 * Created by Richard on 3/8/14.
 */
public final class UniversalComparator implements Comparator<Object> {


    final String sortBy;
    final Map<String, FieldAccess> fields;
    final SortType sortType;
    final List<Comparator> comparators;
    private final boolean byPath;


    public UniversalComparator(String sortBy, Map<String, FieldAccess> fields,
                               SortType sortType, List<Comparator> comparators
                               ) {
        this.sortBy = sortBy;
        this.fields = fields;
        this.sortType = sortType;
        this.comparators = comparators;

        this.byPath = Str.in(Chr.array('.', '[', ']', '/'), sortBy);


    }

    @Override
    final public int compare( Object o1, Object o2 ) {

        Object value1;
        Object value2;

        /** Compare by this. */
        if (byPath || o1 instanceof Map) {
                        /* Grab the values of the sort field. */
            if ( sortType == SortType.ASCENDING ) {
                value1 = indexOf(o1, sortBy);
                value2 = indexOf(o2, sortBy);
            } else {
                value1 = indexOf(o2, sortBy);
                value2 = indexOf(o1, sortBy);
            }
        }

        else if ( sortBy.equals( "this" ) && o1 instanceof Comparable ) {
            if ( sortType == SortType.ASCENDING ) {
                value1 = o1;
                value2 = o2;
            } else {
                value1 = o2;
                value2 = o1;
            }
        }

        else {
            /* Compare by sort field. */
            FieldAccess field = fields.get( sortBy );
            if ( field == null ) {
                Exceptions.die(Str.lines(
                        "The fields was null for sortBy " + sortBy,
                        String.format("fields = %s", fields),
                        String.format("Outer object type = %s", o1.getClass().getName()),
                        String.format("Outer object is %s", o1)
                ));
            }
            /* Grab the values of the sort field. */
            if ( sortType == SortType.ASCENDING ) {
                value1 = field.getValue( o1 );
                value2 = field.getValue( o2 );
            } else {
                value1 = field.getValue( o2 );
                value2 = field.getValue( o1 );
            }
        }


        int compare = Sorting.compare(value1, value2);
        if ( compare == 0 ) {
            for ( Comparator comparator : comparators ) {
                compare = comparator.compare( o1, o2 );
                if ( compare != 0 ) {
                    break;
                }
            }
        }
        return compare;
    }


    public static Comparator universalComparator( final String sortBy, final Map<String, FieldAccess> fields,
                                                  final SortType sortType, final List<Comparator> comparators ) {
        return new UniversalComparator(sortBy, fields, sortType, comparators) ;
    }
}

