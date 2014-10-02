package org.boon.slumberdb.mysql;

import com.esotericsoftware.kryo.Kryo;
import org.boon.slumberdb.impl.SerializedJavaKeyValueStore;
import org.boon.slumberdb.base.BaseStringBinaryKeyValueStore;
import org.boon.slumberdb.serialization.KryoByteArrayToObjectConverter;
import org.boon.slumberdb.serialization.KryoObjectToByteArrayConverter;

import java.io.Serializable;

/**
 * Created by Richard on 4/5/14.
 */
public class SimpleKryoKeyValueStoreMySQL<V extends Serializable> extends BaseStringBinaryKeyValueStore<String, V> implements SerializedJavaKeyValueStore<String, V> {

    /**
     * Kryo valueObjectConverter/valueSerializer
     */
    private final Kryo kryo = new Kryo();


    public SimpleKryoKeyValueStoreMySQL(String url, String userName, String password, String table, Class<V> type, int batchSize) {
        super(new SimpleStringBinaryKeyValueStoreMySQL(url, userName, password, table, batchSize));
        this.valueObjectConverter = new KryoByteArrayToObjectConverter<>(kryo, type);
        this.valueSerializer = new KryoObjectToByteArrayConverter<>(kryo, type);
    }


}
