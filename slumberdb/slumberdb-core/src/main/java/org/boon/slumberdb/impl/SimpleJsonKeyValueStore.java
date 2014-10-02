package org.boon.slumberdb.impl;

import org.boon.slumberdb.JsonKeyValueStore;
import org.boon.slumberdb.KeyValueStore;
import org.boon.slumberdb.base.BaseSimpleSerializationKeyValueStore;
import org.boon.slumberdb.serialization.ByteArrayToStringConverter;
import org.boon.slumberdb.serialization.JsonDeserializerBytes;
import org.boon.slumberdb.serialization.JsonSerializerBytes;
import org.boon.slumberdb.serialization.StringToByteArrayConverter;


/**
 * This marries a store to the Boon JSON parser and the Boon JSON valueObjectConverter.
 * It is a decorator. The real storage is done by the StringKeyValueStore store.
 * You specify the object type.
 * <p/>
 * This class is not thread safe, but the StringKeyValueStore likely is.
 * You need a SimpleJsonKeyValueStore per thread.
 * This is needed to optimize buffer reuse of parser and valueObjectConverter.
 * <p/>
 * You can combine This JSON store with any KeyValueStore store.
 * <p/>
 * It expects the key to be a simple string and the value to be an object that will be serialized to JSON.
 *
 * @param <V> type of value we are storing.
 * @see org.boon.slumberdb.KeyValueStore
 */
public class SimpleJsonKeyValueStore<V> extends BaseSimpleSerializationKeyValueStore<String, V> implements JsonKeyValueStore<String, V> {


    /**
     * Constructor to create a key / value store.
     *
     * @param store store that does the actual store.
     * @param cls   the class of the object that you are storing.
     */
    public SimpleJsonKeyValueStore(KeyValueStore store, Class<V> cls) {
        super(store);
        this.keyObjectConverter = new ByteArrayToStringConverter();
        this.keyToByteArrayConverter = new StringToByteArrayConverter();
        this.valueObjectConverter = new JsonDeserializerBytes<>(cls);
        this.valueToByteArrayConverter = new JsonSerializerBytes<>(cls);

    }


}
