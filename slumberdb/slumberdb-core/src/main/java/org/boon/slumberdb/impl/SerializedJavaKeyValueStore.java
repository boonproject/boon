package org.boon.slumberdb.impl;

import org.boon.slumberdb.KeyValueStore;

/**
 * This is a marker interface of sorts for serialized java object stores.
 * The main implementation will be Kyro.
 */
public interface SerializedJavaKeyValueStore<K, V> extends KeyValueStore<K, V> {
}
