package org.boon.utils;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Lists {

    public static <V> List<V> list(final V... array) {
        if (array==null) {
            return new ArrayList<>();
        }
        List<V> list = new ArrayList<V>(array.length);
        for (V o : array) {
            list.add(o);
        }
        return list;
    }

    public static <V> List<V> safeList(final V... array) {
        if (array==null) {
            return new CopyOnWriteArrayList<>();
        }
        List<V> list = new CopyOnWriteArrayList<V>(array);
        return list;
    }

    /** Universal methods */
    public static int len(List<?> list) {
        return list.size();
    }

    public static <V> boolean in(V value, List<?> list) {
            return list.contains(value);
    }

    public static <V> void add(List<V> list, V value) {
        list.add(value);
    }

    public static <V> V idx(List<V> list, int index) {
        return list.get(index);
    }

    public static <V> void idx(List<V> list, int index, V v) {
        list.set(index, v);
    }

    public static <V> List<V> copy(List<V> list) {
        if (list instanceof RandomAccess) {
            return new ArrayList<>(list);
        }   else {
            return new LinkedList<>(list);
        }
    }

    public static <V> List<V> copy(CopyOnWriteArrayList<V> list) {
            return new CopyOnWriteArrayList<>(list);
    }

    public static <V> List<V> copy(ArrayList<V> list) {
        return new ArrayList<>(list);
    }

    public static <V> List<V> copy(LinkedList<V> list) {
        return new LinkedList<>(list);
    }

}
