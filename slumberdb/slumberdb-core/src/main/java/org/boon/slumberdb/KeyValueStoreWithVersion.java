package org.boon.slumberdb;

import org.boon.slumberdb.entries.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by Richard on 9/23/14.
 */
public interface KeyValueStoreWithVersion <K, O, V extends VersionedEntry<K, O>> extends KeyValueStore<K, V>{



    LoadedEntry<K, O> load(String key, long version);

    UpdateStatus put(String key, long version, O value);

    void set(K key, O value);

    O get(K key);

    UpdateStatus put(String key, long version, long updatedTime, O value);

    UpdateStatus put(VersionKey key, O value);

    Collection<UpdateStatus> putAll(List<VersionedKeyValuePut<byte[]>> list);


}
