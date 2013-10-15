package org.boon.utils;


import java.io.Serializable;
import java.util.*;

public class Maps {

    /** Universal methods. */
    public static int len(Map<?, ?> map) {
        return map.size();
    }

    public static <K, V> boolean in(K key, Map<K, V> map) {
        return map.containsKey(key);
    }

    public static <K, V> void add(Map<K, V> map, Entry<K, V> entry) {
         map.put(entry.key(), entry.value());
    }

    public static <K, V> V idx(Map<K, V> map, K k) {
        return map.get(k);
    }

    public static <K, V> void set(Map<K, V> map, K k, V v) {
        map.put(k, v);
    }

    public static <K, V> SortedMap<K, V> copy(SortedMap<K, V> map) {
        return new TreeMap<>(map);
    }

    public static <K, V> Map<K, V> copy(Map<K, V> map) {
        return new LinkedHashMap<>(map);
    }

    //TODO implement clone which is like copy but goes deeper

    /** End universal methods. */

    public static <K, V> boolean valueIn(V value, Map<K, V> map) {
        return map.containsValue(value);
    }


    public static <K, V> Entry<K, V> entry(final K k, final V v) {
        return new EntryImpl<K, V>(k, v);
    }

    public static <K, V> Entry<K, V> entry(Entry<K, V> entry) {
        return new EntryImpl<K, V>(entry);
    }

    public static interface Entry<K, V> extends Comparable<Entry>,
            Serializable, Cloneable {
        K key();

        V value();

        boolean equals(Entry o) ;
    }

    public static class EntryImpl<K, V> implements Entry<K, V> {

        private K k;
        private V v;

        public EntryImpl() {

        }

        public EntryImpl(EntryImpl<K, V> impl) {
            Objects.requireNonNull(impl);
            Objects.requireNonNull(impl.k);
            Objects.requireNonNull(impl.v);

            this.k = impl.k;
            this.v = impl.v;
        }

        public EntryImpl(Entry<K, V> entry) {
            Objects.requireNonNull(entry);
            Objects.requireNonNull(entry.key());
            Objects.requireNonNull(entry.value());

            this.k = entry.key();
            this.v = entry.value();
        }

        EntryImpl(K k, V v) {
            Objects.requireNonNull(k);
            Objects.requireNonNull(v);

            this.k = k;
            this.v = v;
        }

        @Override
        public K key() {
            return k;
        }

        @Override
        public V value() {
            return v;
        }



        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EntryImpl entry = (EntryImpl) o;
            return this.equals(entry);
        }

        @Override
        public boolean equals(Entry entry) {

            if (k != null ? !k.equals(entry.key()) : entry.key() != null) return false;
            if (v != null ? !v.equals(entry.value()) : entry.value() != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = k != null ? k.hashCode() : 0;
            result = 31 * result + (v != null ? v.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(Entry entry) {
            Objects.nonNull(entry);
            return this.key().toString().compareTo(entry.key().toString());
        }

        @Override
        public String toString() {
            return "{" +
                    "\"k\":" + k +
                    ", \"v\":" + v +
                    '}';
        }
    }


    public static <K, V> Map<K, V> map(K k0, V v0) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        return map;
    }

    public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        return map;
    }

    public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                       V v3) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                       V v3, K k4, V v4) {
        Map<K, V> map = new LinkedHashMap<K, V>(10);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
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

    public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
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

    public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
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

    public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
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

    public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
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

    public static <K, V> Map<K, V> map(Collection<K> keys, Collection<V> values) {
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

    public static <K, V> Map<K, V> map(Iterable<K> keys, Iterable<V> values) {
        Map<K, V> map = new LinkedHashMap<K, V>();
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

    public static <K, V> Map<K, V> map(K[] keys, V[] values) {

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


    public static <K, V> Map<K, V> map(Entry<K, V>... entries) {
        Map<K, V> map = new LinkedHashMap<K, V>(entries.length);
        for (Entry<K, V> entry : entries) {
            map.put(entry.key(), entry.value());
        }
        return map;
    }


    public static <K, V> SortedMap<K, V> sortedMap(K k0, V v0) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(K k0, V v0, K k1, V v1) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                                   V v3) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                                   V v3, K k4, V v4) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
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

    public static <K, V> SortedMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
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

    public static <K, V> SortedMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
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

    public static <K, V> SortedMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
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

    public static <K, V> SortedMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3,
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

    public static <K, V> SortedMap<K, V> sortedMap(Collection<K> keys, Collection<V> values) {
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


    public static <K, V> SortedMap<K, V> sortedMap(Iterable<K> keys, Iterable<V> values) {
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


    public static <K, V> SortedMap<K, V> sortedMap(K[] keys, V[] values) {

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


    public static <K, V> SortedMap<K, V> sortedMap(Entry<K, V>... entries) {
        SortedMap<K, V> map = new TreeMap<K, V>();
        for (Entry<K, V> entry : entries) {
            map.put(entry.key(), entry.value());
        }
        return map;
    }


    //

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K k0, V v0) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
        map.put(k0, v0);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K k0, V v0, K k1, V v1) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
        map.put(k0, v0);
        map.put(k1, v1);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K k0, V v0, K k1, V v1, K k2, V v2) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                                   V v3) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                                   V v3, K k4, V v4) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                                   V v3, K k4, V v4, K k5, V v5) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                                   V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
        map.put(k0, v0);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        return map;
    }

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                                   V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
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

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                                   V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
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

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3,
                                                   V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8,
                                                   K k9, V v9) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
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

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, Collection<K> keys, Collection<V> values) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
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

    public static <K, V> SortedMap<K, V> sortedMap(Comparator comparator, K[] keys, V[] values) {

        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
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


    public static <K, V> SortedMap<K, V> sortedMapOfEntries(Comparator comparator, Entry<K, V>... entries) {
        SortedMap<K, V> map = new TreeMap<K, V>(comparator);
        for (Entry<K, V> entry : entries) {
            map.put(entry.key(), entry.value());
        }
        return map;
    }

}
