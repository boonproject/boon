package org.boon.slumberdb;


import org.boon.slumberdb.entries.Entry;

import java.io.Closeable;

/**
 * Iterate over key / value store.
 * This might be back by a resource like a database cursor or a result set.
 *
 * @param <K> KEY
 * @param <V> VALUE
 */
public interface KeyValueIterable<K, V> extends Iterable<Entry<K, V>>, Closeable {

    /**
     * Resource is likely to be closeable.
     */
    public void close();
}
