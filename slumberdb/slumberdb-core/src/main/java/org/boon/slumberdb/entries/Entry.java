package org.boon.slumberdb.entries;



import java.io.Serializable;
import java.util.Map;

/**
 * Represents an entry in the database.
 *
 * @param <K> key
 * @param <V> value
 */
public class Entry<K, V> extends org.boon.Pair<K, V> implements Serializable {



    public Entry() {

    }

    public Entry(Map.Entry<K, V> entry) {
        super(entry.getKey(), entry.getValue());
    }


    public Entry(K k, V v) {
        super(k, v);
    }
}
