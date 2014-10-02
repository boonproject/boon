package org.boon.slumberdb.impl;

import com.esotericsoftware.kryo.Kryo;
import org.boon.slumberdb.KeyValueStore;
import org.boon.slumberdb.base.BaseSimpleSerializationKeyValueStore;
import org.boon.slumberdb.serialization.ByteArrayToStringConverter;
import org.boon.slumberdb.serialization.KryoByteArrayToObjectConverter;
import org.boon.slumberdb.serialization.KryoObjectToByteArrayConverter;
import org.boon.slumberdb.serialization.StringToByteArrayConverter;

import java.io.Serializable;


/**
 * This marries a key value store with Kryo for valueObjectConverter and valueSerializer support.
 * It is a decorator. The real storage is done by the KeyValueStore <byte[], byte[]> store.
 * You specify the object type.
 * <p/>
 * This class is not thread safe, but the KeyValueStore <byte[], byte[]> likely is.
 * You need a SimpleKryoKeyValueStore per thread.
 * This is needed to optimize buffer reuse of Kryo.
 * <p/>
 * You can combine this Kryo store with any KeyValueStore <byte[], byte[]> store.
 * <p/>
 * It expects the key to be a simple string and the value to be an object that will be serialized using Kryo.
 *
 * @param <V> type of value we are storing.
 * @see org.boon.slumberdb.KeyValueStore
 */
public class SimpleKryoKeyValueStore<V extends Serializable> extends BaseSimpleSerializationKeyValueStore<String, V> implements SerializedJavaKeyValueStore<String, V> {

    /**
     * Kryo valueObjectConverter/valueSerializer
     */
    private final Kryo kryo = new Kryo();

    /**
     * @param store store
     * @param type  type
     */
    public SimpleKryoKeyValueStore(final KeyValueStore<byte[], byte[]> store,
                                   final Class<V> type) {
        super(store);
        this.valueObjectConverter = new KryoByteArrayToObjectConverter<>(kryo, type);
        this.valueToByteArrayConverter = new KryoObjectToByteArrayConverter<>(kryo, type);
        this.keyObjectConverter = new ByteArrayToStringConverter();
        this.keyToByteArrayConverter = new StringToByteArrayConverter();

    }


}
