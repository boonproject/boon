package org.boon;

import org.boon.core.reflection.Conversions;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.Reflection;

import java.text.Collator;
import java.util.*;
import java.util.logging.Logger;

public class Ordering {

    private static final Logger log = Logger.getLogger(Ordering.class.getName());




    public static Comparable comparable(Object comparable) {
        return (Comparable) comparable;
    }


    public static void sortAsc(List list) {
        sortAsc(list, "this");
    }

    public static void sortDesc(List list) {
        sortDesc(list, "this");
    }

    public static void sortAsc(List list, String sortBy) {
        if (list == null || list.size() == 0) {
            return;
        }
        Map<String, FieldAccess> fields = Reflection.getPropertyFieldAccessMap(list.iterator().next().getClass());

        sortAsc(list, sortBy, fields);
    }

    public static void sortDesc(List list, String sortBy) {
        if (list == null || list.size() == 0) {
            return;
        }
        Map<String, FieldAccess> fields = Reflection.getPropertyFieldAccessMap(list.iterator().next().getClass());

        sortDesc(list, sortBy, fields);
    }

    public static void sortAsc(List list, String sortBy, Map<String, FieldAccess> fields) {
        sort(list, sortBy, fields, true);
    }

    public static void sortDesc(List list, String sortBy, Map<String, FieldAccess> fields) {
        sort(list, sortBy, fields, false);
    }

    public static void sort(List list, String sortBy, Map<String, FieldAccess> fields, boolean ascending) {
        if (list == null || list.size() == 0) {
            return;
        }
        Object o = list.get(0);
        if (sortBy.equals("this") && o instanceof Comparable) {
            Collections.sort(list);
            if (!ascending) {
                Collections.reverse(list);
            }
            return;
        }

        final FieldAccess field = fields.get(sortBy);

        if (field != null) {

            Collections.sort(list, Ordering.universalComparator(field, ascending));

        }
    }


    public static Comparator universalComparator(final FieldAccess field, final boolean ascending) {
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Object value1 = null;
                Object value2 = null;

                if (ascending) {
                    value1 = field.getValue(o1);
                    value2 = field.getValue(o2);
                } else {
                    value1 = field.getValue(o2);
                    value2 = field.getValue(o1);
                }
                return Ordering.compare(value1, value2);
            }
        };
    }


    public static Comparator universalComparator(final String sortBy, final Map<String, FieldAccess> fields,
                                                 final boolean ascending, final List<Comparator> comparators) {
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                Object value1 = null;
                Object value2 = null;

                /** Compare by this. */
                if (sortBy.equals("this") && o1 instanceof Comparable) {
                    if (ascending) {
                        value1 = o1;
                        value2 = o2;
                    } else {
                        value1 = o2;
                        value2 = o1;
                    }
                } else {
                    /* Compare by sort field. */
                    FieldAccess field = fields.get(sortBy);
                    if (field == null) {
                        Exceptions.die(Str.lines(
                                "The fields was null for sortBy " + sortBy,
                                String.format("fields = %s", fields),
                                String.format("Outer object type = %s", o1.getClass().getName()),
                                String.format("Outer object is %s", o1)
                        ));
                    }
                    /* Grab the values of the sort field. */
                    if (ascending) {
                        value1 = field.getValue(o1);
                        value2 = field.getValue(o2);
                    } else {
                        value1 = field.getValue(o2);
                        value2 = field.getValue(o1);
                    }
                }


                int compare = Ordering.compare(value1, value2);
                if (compare == 0) {
                    for (Comparator comparator : comparators) {
                        compare = comparator.compare(o1, o2);
                        if (compare != 0) {
                            break;
                        }
                    }
                }
                return compare;
            }
        };
    }


    public static int compare(Object value1, Object value2) {

        if (value1 == null && value2 == null) {
            return 0;
        } else if (value1 == null && value2 != null) {
            return -1;
        } else if (value1 != null && value2 == null) {
            return 1;
        }


        if (value1 instanceof CharSequence) {
            String str1 = Conversions.toString(value1);
            String str2 = Conversions.toString(value2);
            Collator collator = Collator.getInstance();
            return collator.compare(str1, str2);
        } else if (Conversions.isComparable(value1)) {
            Comparable c1 = comparable(value1);
            Comparable c2 = comparable(value2);
            return c1.compareTo(c2);
        } else {
            String name = Reflection.getSortableField(value1);
            String sv1 = (String) Reflection.getPropByPath(value1, name);
            String sv2 = (String) Reflection.getPropByPath(value2, name);
            return Ordering.compare(sv1, sv2);

        }

    }


}
