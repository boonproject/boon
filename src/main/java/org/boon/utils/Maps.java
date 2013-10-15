package org.boon.utils;


import java.util.*;

public class Maps {



    public static int size(Map<?, ?> map) {
        return map.size();
    }

    public static <K, V> Entry<K, V> entry(final K k, final V v) {
        return new EntryImpl<K, V>(k, v);
    }

    public static interface Entry<K, V> {
        K key();
        V value();
    }

    public static class EntryImpl<K, V> implements Entry<K, V> {
        EntryImpl(K k, V v) {
            this.k = k;
            this.v = v;
        }

        K k;
        V v;

        @Override
        public K key() {
            return k;
        }

        @Override
        public V value() {
            return v;
        }
    }




    public static <K, V> Map<K, V> hashMap(K k0, V v0) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        return map;
    }
    public static <K, V> Map<K, V> hashMap(K k0, V v0, K k1, V v1) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(K k0, V v0, K k1, V v1, K k2, V v2) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                      V v3) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                      V v3, K k4, V v4) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                      V v3, K k4, V v4, K k5, V v5) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                      V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                      V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                      V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                      V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8,
                                      K k9, V v9) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(Collection<K> keys, Collection<V> values) {
        Map<K, V> map = new LinkedHashMap<K, V>(10 + keys.size());
        Iterator<V> iterator = values.iterator();
        for (K k : keys) {
            if (iterator.hasNext()) {
                V v = iterator.next();
                map.put(k, v);
            } else {
                map.put(k, null);
            }
        }
        return map;
    }

    public static <K, V> Map<K, V> hashMap(K[] keys, V[] values) {

        Map<K, V> map = new LinkedHashMap<K, V>(10 + keys.length);
        int index = 0;
        for (K k : keys) {
            if (index < keys.length) {
                V v = values[index];
                map.put(k, v);
            } else {
                map.put(k, null);
            }
            index++;
        }
        return map;
    }


    public static <K, V> Map<K, V> hashMap(Entry<K, V>... entries) {
        Map<K, V> map = new LinkedHashMap<K, V>(entries.length);
        for (Entry<K, V> entry : entries) {
            map.put(entry.key(), entry.value());
        }
        return map;
    }








    public static <K, V> SortedMap<K, V> treeMap(K k0, V v0) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        return map;
    }
    public static <K, V> SortedMap<K, V> treeMap(K k0, V v0, K k1, V v1) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        return map;
    }

    public static <K, V> SortedMap<K, V> treeMap(K k0, V v0, K k1, V v1, K k2, V v2) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> SortedMap<K, V> treeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                           V v3) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static <K, V> SortedMap<K, V> treeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                           V v3, K k4, V v4) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static <K, V> SortedMap<K, V> treeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                           V v3, K k4, V v4, K k5, V v5) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    public static <K, V> SortedMap<K, V> treeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                           V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        return map;
    }

    public static <K, V> SortedMap<K, V> treeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                           V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        return map;
    }

    public static <K, V> SortedMap<K, V> treeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                           V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        return map;
    }

    public static <K, V> SortedMap<K, V> treeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                           V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8,
                                           K k9, V v9) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        return map;
    }

    public static <K, V> SortedMap<K, V> treeMap(Collection<K> keys, Collection<V> values) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        Iterator<V> iterator = values.iterator();
        for (K k : keys) {
            if (iterator.hasNext()) {
                V v = iterator.next();
                map.put(k, v);
            } else {
                map.put(k, null);
            }
        }
        return map;
    }

    public static <K, V> SortedMap<K, V> treeMap(K[] keys, V[] values) {

        SortedMap<K, V> map = new TreeMap<K, V>();
        int index = 0;
        for (K k : keys) {
            if (index < keys.length) {
                V v = values[index];
                map.put(k, v);
            } else {
                map.put(k, null);
            }
            index++;
        }
        return map;
    }


    public static <K, V> SortedMap<K, V> treeMap(Entry<K, V>... entries) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        for (Entry<K, V> entry : entries) {
            map.put(entry.key(), entry.value());
        }
        return map;
    }

}
