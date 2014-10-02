package org.boon.slumberdb.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.boon.Exceptions;
import org.boon.Logger;
import org.boon.primitive.CharBuf;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.KeyValueIterable;

import java.sql.*;
import java.util.*;

import static org.boon.Boon.configurableLogger;
import static org.boon.Boon.sputs;

/**
 * Created by Richard on 4/9/14.
 */
public abstract class BaseMySQLSupport<T> {


    protected final String sqlColumnType;
    private final boolean debug = GlobalConfig.DEBUG;
    protected String url;
    protected String userName;
    protected String password;
    protected String table;
    protected Connection connection;
    protected String insertStatementSQL;
    protected String selectStatementSQL;
    protected String searchStatementSQL;
    protected String createStatementSQL;
    protected String deleteStatementSQL;
    protected String tableExistsSQL;
    protected PreparedStatement insert;
    protected PreparedStatement delete;
    protected PreparedStatement select;
    protected PreparedStatement search;
    protected PreparedStatement loadAll;
    protected PreparedStatement allKeys;
    protected Logger logger = configurableLogger(BaseMySQLSupport.class);
    protected String loadAllSQL;
    protected int batchSize = 100;
    protected String selectKeysSQL;
    protected int loadKeyCount = 100;
    protected PreparedStatement loadAllKeys;
    protected String loadAllKeysSQL;
    private long totalConnectionOpen;
    private long totalClosedConnections;
    private long totalErrors;
    private boolean closed;
    public BaseMySQLSupport(String password, String userName, String url, String table, String sqlColumnType, int batchSize) {
        this.sqlColumnType = sqlColumnType;
        this.password = password;
        this.userName = userName;
        this.url = url;
        this.table = table;
        this.batchSize = batchSize;
        this.loadKeyCount = batchSize;


        createSQL(table);
        initDB();
    }

    public long totalConnectionOpen() {
        return totalConnectionOpen;
    }

    public long totalClosedConnections() {
        return totalClosedConnections;
    }

    public long totalErrors() {
        return totalErrors;
    }

    protected void initDB() {

        connect();
        createTableIfNeeded();
        createPreparedStatements();
    }

    protected abstract T getValueColumn(int index, ResultSet resultSet) throws SQLException;

    protected abstract void setValueColumnQueryParam(int index, PreparedStatement p, T value) throws SQLException;

    protected void createSQL(String table) {
        this.insertStatementSQL = "replace into `" + table + "` (kv_key, kv_value) values (?,?);";
        this.selectStatementSQL = "select kv_value from `" + table + "` where kv_key = ?;";
        this.searchStatementSQL = "select kv_key, kv_value from `" + table + "` where kv_key >= ?;";
        this.loadAllSQL = "select kv_key, kv_value from `" + table + "`;";
        this.selectKeysSQL = "select kv_key from `" + table + "`;";


        createLoadAllKeysSQL(table);


        this.deleteStatementSQL = "delete  from `" + table + "` where kv_key = ?;";

        this.tableExistsSQL = "select * from `" + table + "` where 1!=1;";

        createTableSQL(table);

        if (debug)
            logger.info("The following SQL statements will be used", "insert", this.insertStatementSQL, "select", this.selectStatementSQL,
                    "search", this.searchStatementSQL, "LOAD", this.loadAllSQL, "SELECT_KEYS", this.selectKeysSQL,
                    "DELETE", this.deleteStatementSQL, "TABLE EXISTS", this.tableExistsSQL, "CREATE_TABLE", this.createStatementSQL);

    }


    protected void createTableSQL(String table) {
        this.createStatementSQL = "\n" +
                "CREATE TABLE " + "`" + table + "` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `kv_key` varchar(80) DEFAULT NULL,\n" +
                "  `kv_value` " + sqlColumnType + ",\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY  `" + table + "_kv_key_idx` (`kv_key`)\n" +
                ");\n";
    }


    protected void createLoadAllKeysSQL(String table) {
        CharBuf buf = CharBuf.create(100);
        buf.add("select kv_key, kv_value from `");
        buf.add(table);
        buf.add("` where kv_key in (");
        buf.multiply("?,", this.loadKeyCount);
        buf.removeLastChar();
        buf.add(");");

        this.loadAllKeysSQL = buf.toString();
    }


    protected void connect() {

        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setURL(url);
            dataSource.setPassword(password);
            dataSource.setUser(userName);
            connection = dataSource.getConnection();
            connection.setAutoCommit(true);
            closed = false;
            totalConnectionOpen++;
        } catch (SQLException sqlException) {
            this.closed = true;
            connection = null;

            handle("Unable to connect", sqlException);

        }


    }


    protected void createTableIfNeeded() {
        if (closed) {
            return;
        }

        try {


            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(tableExistsSQL);
            resultSet.next();


        } catch (SQLException e) {
            closed = true;
            this.close();
            this.connect();

            try {

                Statement statement = connection.createStatement();
                statement.execute(createStatementSQL);

            } catch (SQLException e1) {
                handle("Unable to create prepare table " + createStatementSQL, e);
            }
        }
    }


    protected void handle(String message, SQLException sqlException) {

        totalErrors++;


        if (debug) handleSQLException(sqlException);

        try {
            close();
        } catch (Exception ex) {
            logger.warn(ex, "Problem closing connection after sql exception\n", sqlException);
        }


        Exceptions.handle(message, sqlException);


    }


    public void handleSQLException(SQLException ex) {

        SQLException next = ex.getNextException();

        while (next != null) {
            logger.warn(next, "BasyMySQLSupport Nested SQL Exception", next.getMessage());
            next = ex.getNextException();

        }


    }


    protected void createPreparedStatements() {
        if (closed) {
            return;
        }

        try {

            insert = connection.prepareStatement(insertStatementSQL);

            delete = connection.prepareStatement(deleteStatementSQL);

            select = connection.prepareStatement(selectStatementSQL);

            search = connection.prepareStatement(searchStatementSQL);

            loadAll = connection.prepareStatement(loadAllSQL);

            allKeys = connection.prepareStatement(selectKeysSQL);

            loadAllKeys = connection.prepareStatement(this.loadAllKeysSQL);

        } catch (SQLException e) {
            handle("Unable to create prepared statements", e);
        }
    }


    public void removeAll(Iterable<String> keys) {
        initIfNeeded();
        removeAllUseBatch(keys);
    }

    protected void removeAllUseBatch(Iterable<String> keys) {
        initIfNeeded();

        try {

            for (String key : keys) {
                delete.setString(1, key);
                delete.addBatch();
            }

            delete.executeBatch();

        } catch (SQLException e) {

            handle("Unable to removeAll values", e);
        }

    }

    public void remove(String key) {
        initIfNeeded();

        if (debug) logger.info("REMOVE KEY", key);

        try {
            delete.setString(1, key);
            delete.executeUpdate();
        } catch (SQLException e) {
            delete = null;
            closed = true;
            connection = null;

            handle(sputs("Unable to remove key", key), e);
        }

    }


    public KeyValueIterable<String, T> search(final String startKey) {

        initIfNeeded();


        if (debug) logger.info("SEARCH", startKey);


        try {
            search.setString(1, startKey);
            final ResultSet resultSet = search.executeQuery();

            return new KeyValueIterable<String, T>() {

                @Override
                public void close() {
                    closeResultSet(resultSet);
                }

                @Override
                public Iterator<Entry<String, T>> iterator() {

                    return new Iterator<Entry<String, T>>() {
                        @Override
                        public boolean hasNext() {
                            return resultSetNext(resultSet);
                        }

                        @Override
                        public Entry<String, T> next() {
                            try {

                                String key = resultSet.getString(1);
                                T value = getValueColumn(2, resultSet);
                                return new Entry<>(key, value);
                            } catch (SQLException e) {
                                handle("Unable to extract values for search query for " + startKey, e);
                                return null;
                            }

                        }

                        @Override
                        public void remove() {

                        }
                    };
                }
            };


        } catch (SQLException e) {
            handle(sputs("Unable to search records search key", startKey, "\nquery=", this.searchStatementSQL), e);
            return null;
        }
    }

    protected boolean resultSetNext(ResultSet resultSet) {

        try {
            return resultSet.next();
        } catch (SQLException e) {
            closeResultSet(resultSet);
            handle("Unable to call next() for result set", e);
            return false;
        }
    }


    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.warn("Problem closing", e);
        } finally {
            closed = true;
            connection = null;
            totalClosedConnections++;
        }
    }


    protected void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.error("Unable to close result set", e);
            }
        }
    }

    public Collection<String> loadAllKeys() {

        initIfNeeded();


        if (debug) logger.info("LOAD ALL KEYS");

        LinkedHashSet<String> set = new LinkedHashSet<>();
        ResultSet resultSet = null;

        try {

            resultSet = allKeys.executeQuery();

            while (resultSet.next()) {
                String key = resultSet.getString(1);
                set.add(key);
            }
        } catch (SQLException e) {
            handle("Unable to call next() for result set for loadAllByKeysPreparedStatement query", e);
        } finally {
            closeResultSet(resultSet);
        }


        if (debug) logger.debug("LOAD ALL KEYS BEGETS", set);
        return set;

    }


    public T load(String key) {

        initIfNeeded();


        if (debug) logger.info("LOAD KEY", key);

        T value;
        try {
            select.setString(1, key);
            final ResultSet resultSet = select.executeQuery();


            if (resultSet.next()) {
                value = getValueColumn(1, resultSet);
            } else {
                value = null;
            }

        } catch (SQLException ex) {
            handle("Unable to load " + key, ex);
            return null;
        }
        return value;
    }


    protected void keyBatch(Map<String, T> results, List<String> keyLoadList) {
        String keyResult;
        T valueResult;

        while (keyLoadList.size() < this.loadKeyCount) {
            keyLoadList.add(null);
        }
        try {
            int indexToLoad = 1;
            for (String keyToLoad : keyLoadList) {
                loadAllKeys.setString(indexToLoad, keyToLoad);
                indexToLoad++;
            }

            final ResultSet resultSet = loadAllKeys.executeQuery();

            while (resultSet.next()) {

                keyResult = resultSet.getString(1);
                valueResult = getValueColumn(2, resultSet);
                results.put(keyResult, valueResult);
            }
            resultSet.close();

        } catch (SQLException ex) {
            handle("Unable to load " + keyLoadList, ex);
        }

    }


    public void put(String key, T value) {

        initIfNeeded();


        if (debug) logger.info("PUT KEY", key, value);

        try {
            insert.setString(1, key);
            setValueColumnQueryParam(2, insert, value);
            insert.executeUpdate();

        } catch (SQLException e) {
            handle(sputs("Unable to insert key", key, "value", value), e);
        }


    }


    public void putAllUseBatch(Map<String, T> values) {

        initIfNeeded();


        int count = 0;
        try {

            Set<Map.Entry<String, T>> entries = values.entrySet();

            for (Map.Entry<String, T> entry : entries) {
                String key = entry.getKey();
                T value = entry.getValue();
                insert.setString(1, key);
                setValueColumnQueryParam(2, insert, value);

                insert.addBatch();

                if (count == batchSize) {
                    count = 0;
                    insert.executeBatch();
                } else {
                    count++;
                }
            }

            insert.executeBatch();

        } catch (SQLException e) {

            boolean recover = true;

            if (e instanceof SQLTransactionRollbackException) {


                for (Map.Entry<String, T> entry : values.entrySet()) {
                    try {

                        this.put(entry.getKey(), entry.getValue());

                    } catch (Exception ex) {
                        logger.warn(ex, "BaseMySQLSUpport", "Unable to save", entry.getKey());

                        recover = false;
                    }
                }
            }

            if (!recover) {
                handle("BaseMySQLSUpport Unable to putALl values " + values.size(), e);
            }
        }
    }

    private void initIfNeeded() {
        if (closed) {
            logger.warn("closed detected, reopening connection");
            initDB();
        }
    }


    public void putAllUseTransaction(Map<String, T> values) {

        initIfNeeded();


        try {
            connection.setAutoCommit(false);

            Set<Map.Entry<String, T>> entries = values.entrySet();

            for (Map.Entry<String, T> entry : entries) {
                String key = entry.getKey();
                T value = entry.getValue();
                insert.setString(1, key);
                setValueColumnQueryParam(2, insert, value);
                insert.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.warn("Unable to rollback exception", e1);
            }
            handle("Unable to putALl values", e);

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.warn("Unable to set auto commit back to true", e);

            }

        }

    }

    public void putAll(Map<String, T> values) {


        initIfNeeded();


        if (debug) logger.info("PUT ALL ", values);


        putAllUseBatch(values);
    }


    public Map<String, T> loadAllByKeys(Collection<String> keys) {

        if (debug) logger.info("LOAD ALL BY KEYS ", keys);

        initIfNeeded();


        Map<String, T> results = new LinkedHashMap<>(keys.size());
        List<String> keyLoadList = new ArrayList<>(this.loadKeyCount);


        for (String key : keys) {
            keyLoadList.add(key);

            if (keyLoadList.size() == loadKeyCount) {
                keyBatch(results, keyLoadList);
                keyLoadList.clear();
            }

        }

        keyBatch(results, keyLoadList);
        return results;
    }


    public KeyValueIterable<String, T> loadAll() {

        if (debug) logger.info("LOAD ALL  ");

        initIfNeeded();


        try {
            final ResultSet resultSet = loadAll.executeQuery();

            return new KeyValueIterable<String, T>() {

                @Override
                public void close() {
                    closeResultSet(resultSet);
                }

                @Override
                public Iterator<Entry<String, T>> iterator() {

                    return new Iterator<Entry<String, T>>() {
                        @Override
                        public boolean hasNext() {
                            return resultSetNext(resultSet);
                        }

                        @Override
                        public Entry<String, T> next() {
                            try {

                                String key = resultSet.getString(1);

                                T value = getValueColumn(2, resultSet);

                                return new Entry<>(key, value);
                            } catch (SQLException e) {
                                handle("Unable to extract values for loadAllByKeys query", e);
                                return null;
                            }

                        }

                        @Override
                        public void remove() {

                        }
                    };
                }
            };


        } catch (SQLException e) {
            handle("Unable to load all records", e);
            return null;
        }
    }

    public boolean isOpen() {
        return !closed;
    }

    public boolean isClosed() {
        return closed;
    }
}
