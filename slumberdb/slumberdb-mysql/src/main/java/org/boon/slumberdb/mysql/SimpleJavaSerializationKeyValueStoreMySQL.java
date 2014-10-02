package org.boon.slumberdb.mysql;

import org.boon.slumberdb.impl.SerializedJavaKeyValueStore;
import org.boon.slumberdb.base.BaseStringBinaryKeyValueStore;
import org.boon.slumberdb.serialization.JavaDeserializerBytes;
import org.boon.slumberdb.serialization.JavaSerializerBytes;

import java.io.Serializable;

/**
 * Created by Richard on 4/4/14.
 */
public class SimpleJavaSerializationKeyValueStoreMySQL<V extends Serializable> extends BaseStringBinaryKeyValueStore<String, V> implements SerializedJavaKeyValueStore<String, V> {


    public SimpleJavaSerializationKeyValueStoreMySQL(String url, String userName, String password, String table, Class<V> type, int batchSize) {
        super(new SimpleStringBinaryKeyValueStoreMySQL(url, userName, password, table,  batchSize));
        this.valueObjectConverter = new JavaDeserializerBytes<>();
        this.valueSerializer = new JavaSerializerBytes<>();
    }


}
