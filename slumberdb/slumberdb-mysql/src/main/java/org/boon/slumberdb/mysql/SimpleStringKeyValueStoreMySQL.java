package org.boon.slumberdb.mysql;

import org.boon.slumberdb.StringKeyValueStore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SimpleStringKeyValueStoreMySQL extends BaseMySQLSupport<String> implements StringKeyValueStore {


    public SimpleStringKeyValueStoreMySQL(String url, String userName, String password, String table, int batchSize) {
        super(password, userName, url, table, "TEXT", batchSize);


    }

    @Override
    protected String getValueColumn(int index, ResultSet resultSet) throws SQLException {
        String value;
        value = resultSet.getString(index);
        return value;
    }

    @Override
    protected void setValueColumnQueryParam(int index, PreparedStatement p, String value) throws SQLException {
        p.setString(index, value);
    }
}
