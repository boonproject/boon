package org.boon.criteria;


import java.util.*;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.Reflection;
import org.boon.Ordering;


public class Sort {

    private String name = "this";
    private SortType type;
    private List<Sort> sorts = new ArrayList<>();
    private String toString;
    private int hashCode;


    private List<Comparator> comparators;
    private Comparator comparator;

    public static Sort sorts(Sort... sorts) {
        if (sorts == null || sorts.length == 0) {
            return null;
        }

        Sort main = sorts[0];
        for (int index = 1; index < sorts.length; index++) {
            main.then(sorts[index]);
        }
        return main;
    }

    public static Sort asc(String name) {
        return new Sort(name, SortType.ASCENDING);
    }


    public static Sort desc(String name) {
        return new Sort(name, SortType.DESCENDING);
    }

    public Sort() {
    }

    public Sort(String name, SortType type) {
        this.name = name;
        this.type = type;
        this.hashCode = doHashCode();
        this.toString = doToString();
    }

    public SortType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    private String doToString() {
        return "Sort{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';

    }

    public Sort then(Sort sort) {
        this.sorts.add(sort);
        return this;
    }

    public Sort then(String name) {
        this.sorts.add(new Sort(name, SortType.ASCENDING));
        return this;
    }

    public Sort thenAsc(String name) {
        this.sorts.add(new Sort(name, SortType.ASCENDING));
        return this;
    }

    public Sort thenDesc(String name) {
        this.sorts.add(new Sort(name, SortType.DESCENDING));
        return this;
    }

    @Override
    public String toString() {
        return toString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sort)) return false;

        Sort sort = (Sort) o;

        if (!name.equals(sort.name)) return false;
        if (type != sort.type) return false;

        return true;
    }

    private int doHashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;

    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public void sort(List list, Map<String, FieldAccess> fields) {
        Collections.sort(list, this.comparator(fields));
    }


    public void sort(List list) {
        if (list == null || list.size() == 0) {
            return;
        }

        Object item = list.iterator().next();

        Map<String, FieldAccess> fields = Reflection.getFieldsFromObject(item);
        Collections.sort(list, this.comparator(fields));
    }

    public Comparator comparator(Map<String, FieldAccess> fields) {
        if (comparator == null) {
            comparator = Ordering.universalComparator(this.getName(), fields,
                    this.getType() == SortType.ASCENDING, this.childComparators(fields));
        }
        return comparator;
    }

    private List<Comparator> childComparators(Map<String, FieldAccess> fields) {
        if (this.comparators == null) {
            this.comparators = new ArrayList<Comparator>(this.sorts.size() + 1);

            for (Sort sort : sorts) {
                Comparator comparator = Ordering.universalComparator(
                        sort.getName(),
                        fields,
                        sort.type == SortType.ASCENDING,
                        sort.childComparators(fields)
                );
                this.comparators.add(comparator);
            }
        }
        return this.comparators;
    }

}
