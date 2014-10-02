package org.boon.slumberdb.impl;

import org.boon.slumberdb.KeyValueStore;
import org.boon.slumberdb.base.BaseSimpleSerializationKeyValueStore;
import org.boon.slumberdb.serialization.ByteArrayToStringConverter;
import org.boon.slumberdb.serialization.JavaDeserializerBytes;
import org.boon.slumberdb.serialization.JavaSerializerBytes;
import org.boon.slumberdb.serialization.StringToByteArrayConverter;

import java.io.Serializable;

/**
 * This is done mostly to benchmark it against Kryo to show how awesome Kryo is.
 */
public class SimpleJavaSerializationStore<V extends Serializable> extends BaseSimpleSerializationKeyValueStore<String, V> implements SerializedJavaKeyValueStore<String, V> {

    /**
     * @param store store
     */
    public SimpleJavaSerializationStore(final KeyValueStore<byte[], byte[]> store
    ) {
        super(store);
        this.valueObjectConverter = new JavaDeserializerBytes();
        this.valueToByteArrayConverter = new JavaSerializerBytes();
        this.keyObjectConverter = new ByteArrayToStringConverter();
        this.keyToByteArrayConverter = new StringToByteArrayConverter();

    }

}
