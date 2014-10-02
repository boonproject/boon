package org.boon.slumberdb.mysql;

import org.boon.slumberdb.base.BaseStringStringKeyValueStore;
import org.boon.slumberdb.serialization.JsonDeserializer;
import org.boon.slumberdb.serialization.JsonSerializer;

/**
 * Created by Richard on 4/4/14.
 */
public class SimpleJsonKeyValueStoreMySQL<V> extends BaseStringStringKeyValueStore<String, V> {

    public SimpleJsonKeyValueStoreMySQL(String url, String userName, String password, String table, Class<V> cls, int batchSize) {
        super(new SimpleStringKeyValueStoreMySQL(url, userName, password, table, batchSize));


        this.valueObjectConverter = new JsonDeserializer<>(cls);
        this.valueSerializer = new JsonSerializer<>(cls);
    }

}