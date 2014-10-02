package org.boon.slumberdb.mysql;


import org.boon.slumberdb.KeyValueStore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Richard on 4/4/14.
 */
public class SimpleStringBinaryKeyValueStoreMySQL extends BaseMySQLSupport<byte[]> implements KeyValueStore<String, byte[]> {


    public SimpleStringBinaryKeyValueStoreMySQL(String url, String userName, String password, String table, int batchSize) {
        super(password, userName, url, table, "BLOB", batchSize);


    }

    @Override
    protected byte[] getValueColumn(int index, ResultSet resultSet) throws SQLException {
        return resultSet.getBytes(index);
    }

    @Override
    protected void setValueColumnQueryParam(int index, PreparedStatement p, byte[] value) throws SQLException {
        p.setBytes(index, value);
    }

}

