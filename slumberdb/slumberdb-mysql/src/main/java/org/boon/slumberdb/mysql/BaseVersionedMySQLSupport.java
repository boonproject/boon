package org.boon.slumberdb.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.boon.Exceptions;
import org.boon.Lists;
import org.boon.Logger;
import org.boon.collections.LazyMap;
import org.boon.core.Conversions;
import org.boon.primitive.CharBuf;
import org.boon.slumberdb.*;
import org.boon.slumberdb.config.GlobalConfig;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.entries.VersionKey;
import org.boon.slumberdb.entries.VersionedEntry;
import org.boon.slumberdb.spi.VersionedStorageProvider;

import java.sql.*;
import java.util.*;

import static org.boon.Boon.configurableLogger;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 9/23/14.
 */
public  class BaseVersionedMySQLSupport implements VersionedStorageProvider {


    protected final String sqlColumnType = "LONGBLOB";
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
    protected PreparedStatement loadAllVersionDataByKeys;

    protected Logger logger = configurableLogger(BaseVersionedMySQLSupport.class);
    protected String loadAllSQL;
    protected int batchSize = 100;
    protected String selectKeysSQL;
    protected int loadKeyCount = 100;
    protected PreparedStatement loadAllByKeysPreparedStatement;

    protected String loadAllByKeysSQL;
    protected String loadAllVersionDataByKeysSQL;

    private long totalConnectionOpen;
    private long totalClosedConnections;
    private long totalErrors;
    private boolean closed;

    private int KEY_POS = 1;
    private int VALUE_POS = 2;
    private int VERSION_POS = 3;
    private int UPDATE_POS = 4;
    private int CREATE_POS = 5;


    public BaseVersionedMySQLSupport(String password, String userName, String url, String table,
                                     int writeBatchSize, int readBatch) {
        this.password = password;
        this.userName = userName;
        this.url = url;
        this.table = table;
        this.batchSize = writeBatchSize;
        this.loadKeyCount = readBatch;


        createSQL(table);
        initDB();
    }


    protected void initDB() {

        connect();
        createTableIfNeeded();
        createPreparedStatements();
    }


    /**
     * Creates a new table set up just the way we need.
     * @param table to create
     */
    protected void createTableSQL(String table) {
        this.createStatementSQL = "\n" +
                "CREATE TABLE " + "`" + table + "` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `create_timestamp` bigint(20) NOT NULL,\n" +
                "  `version` bigint(20) NOT NULL,\n" +
                "  `update_timestamp` bigint(20) NOT NULL,\n" +
                "  `kv_key` varchar(80) NOT NULL,\n" +
                "  `kv_value` " + sqlColumnType + ",\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY  `" + table + "_kv_key_idx` (`kv_key`)\n" +
                ");\n";
    }




    @Override
    public void removeAll(Iterable<String> keys) {
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

    @Override
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


    @Override
    public KeyValueIterable<String, VersionedEntry<String, byte[]>> search(final String startKey) {

        initIfNeeded();


        if (debug) logger.info("SEARCH", startKey);


        try {
            search.setString(1, startKey);
            final ResultSet resultSet = search.executeQuery();

            return new KeyValueIterable<String, VersionedEntry<String, byte[]>>() {

                @Override
                public void close() {
                    closeResultSet(resultSet);
                }

                @Override
                public Iterator<Entry<String, VersionedEntry<String, byte[]>>> iterator() {

                    return new Iterator<Entry<String, VersionedEntry<String, byte[]>>>() {
                        @Override
                        public boolean hasNext() {
                            return resultSetNext(resultSet);
                        }

                        @Override
                        public Entry<String, VersionedEntry<String, byte[]>> next() {
                            try {

                                String key = resultSet.getString(1);
                                byte[] value = getValueColumn(VALUE_POS, resultSet);
                                long version = resultSet.getLong(VERSION_POS);
                                long update = resultSet.getLong(UPDATE_POS);
                                long create = resultSet.getLong(CREATE_POS);

                                VersionedEntry<String, byte[]> ve = new VersionedEntry<>(key, value);
                                ve.setCreateTimestamp(create);
                                ve.setUpdateTimestamp(update);
                                ve.setVersion(version);


                                return new Entry<>(key, ve);
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



    @Override
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


    @Override
    public VersionedEntry<String, byte[]> load(String key) {

        initIfNeeded();


        VersionedEntry<String, byte[]> returnValue = null;

        if (debug) logger.info("LOAD KEY", key);

        try {

            select.setString(1, key);
            final ResultSet resultSet = select.executeQuery();


            if (resultSet.next()) {

                byte[] value = getValueColumn(VALUE_POS, resultSet);
                long version = resultSet.getLong(VERSION_POS);
                long update = resultSet.getLong(UPDATE_POS);
                long create = resultSet.getLong(CREATE_POS);

                returnValue = new VersionedEntry<>(key, value);
                returnValue.setCreateTimestamp(create);
                returnValue.setUpdateTimestamp(update);
                returnValue.setVersion(version);
            }

        } catch (SQLException ex) {
            handle("Unable to load " + key, ex);
        }
        return returnValue;
    }


    protected void keyBatch(LazyMap results, List<String> keyLoadList, boolean getValue) {

        while (keyLoadList.size() < this.loadKeyCount) {
            keyLoadList.add(null);
        }
        try {
            int indexToLoad = 1;
            for (String keyToLoad : keyLoadList) {
                loadAllByKeysPreparedStatement.setString(indexToLoad, keyToLoad);
                indexToLoad++;
            }

            final ResultSet resultSet = loadAllByKeysPreparedStatement.executeQuery();

            while (resultSet.next()) {

                String key = resultSet.getString(KEY_POS);
                byte[] value = getValue ? getValueColumn(VALUE_POS, resultSet) : null;



                long version = resultSet.getLong(VERSION_POS);
                long update = resultSet.getLong(UPDATE_POS);
                long create = resultSet.getLong(CREATE_POS);

                if (getValue) {
                    VersionedEntry<String, byte[]> returnValue = new VersionedEntry<>(key, value);
                    returnValue.setCreateTimestamp(create);
                    returnValue.setUpdateTimestamp(update);
                    returnValue.setVersion(version);


                    results.put(key, returnValue);
                } else {
                    VersionKey versionKey = new VersionKey(key, version, update, create);

                    results.put(key, versionKey);
                }
            }
            resultSet.close();

        } catch (SQLException ex) {
            handle("Unable to load " + keyLoadList, ex);
        }

    }


    @Override
    public void put(String key, VersionedEntry<String, byte[]> entry) {

        initIfNeeded();


        if (debug) logger.info("PUT KEY", key, entry);

        try {
            insert.setString(KEY_POS, key);
            setValueColumnQueryParam(VALUE_POS, insert, entry.value());
            insert.setLong(CREATE_POS, entry.createdOn());
            insert.setLong(UPDATE_POS, entry.updatedOn());

            insert.executeUpdate();

        } catch (SQLException e) {
            handle(sputs("Unable to insert key", key, "value", entry), e);
        }


    }



    private void initIfNeeded() {
        if (closed) {
            logger.warn("closed detected, reopening connection");
            initDB();
        }
    }


    @Override
    public void putAll(Map<String, VersionedEntry<String, byte[]>> values) {


        initIfNeeded();


        if (debug) logger.info("PUT ALL ", values);



        int count = 0;
        try {

            Set<Map.Entry<String, VersionedEntry<String, byte[]>>> entries = values.entrySet();

            for (Map.Entry<String, VersionedEntry<String, byte[]>> entry : entries) {
                String key = entry.getKey();


                insert.setString(KEY_POS, key);
                setValueColumnQueryParam(VALUE_POS, insert, entry.getValue().value());
                insert.setLong(CREATE_POS, entry.getValue().createdOn());
                insert.setLong(UPDATE_POS, entry.getValue().updatedOn());

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


                for (Map.Entry<String, VersionedEntry<String, byte[]>> entry : values.entrySet()) {
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


    @Override
    public Map<String, VersionedEntry<String, byte[]>> loadAllByKeys(Collection<String> keys) {

        if (debug) logger.info("LOAD ALL BY KEYS ", keys);

        initIfNeeded();


        LazyMap results = new LazyMap(keys.size());
        List<String> keyLoadList = new ArrayList<>(this.loadKeyCount);


        for (String key : keys) {
            keyLoadList.add(key);

            if (keyLoadList.size() == loadKeyCount) {
                keyBatch(results, keyLoadList, true);
                keyLoadList.clear();
            }

        }

        keyBatch(results, keyLoadList, true);
        return (Map<String, VersionedEntry<String, byte[]>>) (Object) results;
    }

    @Override
    public List<VersionKey> loadAllVersionInfoByKeys(Collection<String> keys) {


        if (debug) logger.info("LOAD ALL BY KEYS ", keys);

        initIfNeeded();


        LazyMap results = new LazyMap(keys.size());
        List<String> keyLoadList = new ArrayList<>(this.loadKeyCount);


        for (String key : keys) {
            keyLoadList.add(key);

            if (keyLoadList.size() == loadKeyCount) {
                keyBatch(results, keyLoadList, true);
                keyLoadList.clear();
            }

        }

        keyBatch(results, keyLoadList, true);

        return Conversions.toList(results.values());

    }

    /*

     */

    @Override
    public KeyValueIterable<String, VersionedEntry<String, byte[]>> loadAll() {

        if (debug) logger.info("LOAD ALL  ");

        initIfNeeded();


        try {
            final ResultSet resultSet = loadAll.executeQuery();

            return new KeyValueIterable<String, VersionedEntry<String, byte[]>>() {

                @Override
                public void close() {
                    closeResultSet(resultSet);
                }

                @Override
                public Iterator<Entry<String, VersionedEntry<String, byte[]>>> iterator() {

                    return new Iterator<Entry<String, VersionedEntry<String, byte[]>>>() {
                        @Override
                        public boolean hasNext() {
                            return resultSetNext(resultSet);
                        }

                        @Override
                        public Entry<String, VersionedEntry<String, byte[]>> next() {
                            try {



                                String key = resultSet.getString(KEY_POS);
                                byte[] value = getValueColumn(VALUE_POS, resultSet);
                                long version = resultSet.getLong(VERSION_POS);
                                long update = resultSet.getLong(UPDATE_POS);
                                long create = resultSet.getLong(CREATE_POS);

                                VersionedEntry<String, byte[]> returnValue = new VersionedEntry<>(key, value);
                                returnValue.setCreateTimestamp(create);
                                returnValue.setUpdateTimestamp(update);
                                returnValue.setVersion(version);



                                return new Entry<>(key, returnValue);
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


    @Override
    public boolean isOpen() {
        return !closed;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public VersionKey loadVersion(String key) {
        final List<VersionKey> versionKeys = loadAllVersionInfoByKeys(Lists.list(key));

        if (versionKeys.size()==1) {
            return versionKeys.get(0);
        } else {
            return VersionKey.notFound(key);
        }
    }





    /**
     * Creates a table if needed.
     */
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


    /**
     * Handles an exception
     * @param message status message
     * @param sqlException sql exception
     */
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




    /**
     * Connects to the DB and tracks if successful so upstream stuff can try to reconnect.
     */
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



    /**
     * Handles an Exception.
     * @param ex
     */
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

            loadAllByKeysPreparedStatement = connection.prepareStatement(this.loadAllByKeysSQL);

        } catch (SQLException e) {
            handle("Unable to create prepared statements", e);
        }
    }


    @Override
    public long totalConnectionOpen() {
        return totalConnectionOpen;
    }

    @Override
    public long totalClosedConnections() {
        return totalClosedConnections;
    }

    @Override
    public long totalErrors() {
        return totalErrors;
    }


    protected byte[] getValueColumn(int index, ResultSet resultSet) throws SQLException {
        return resultSet.getBytes(index);
    }

    protected void setValueColumnQueryParam(int index, PreparedStatement p, byte[] value) throws SQLException {
        p.setBytes(index, value);
    }



    /**
     * Create SQL statements
     * @param table
     */
    protected void createSQL(String table) {
        this.insertStatementSQL =  "replace into `" + table + "` (kv_key, kv_value, version, update_timestamp, create_timestamp)" +
                " values (?,?);";
        this.selectStatementSQL = "select kv_key, kv_value, version, update_timestamp, create_timestamp from `" + table +
                "` where kv_key = ?;";
        this.searchStatementSQL = "select kv_key, kv_value, version, update_timestamp, create_timestamp from `" + table
                + "` where kv_key >= ?;";
        this.loadAllSQL = "select kv_key, kv_value, version, update_timestamp, create_timestamp  from `" + table + "`;";
        this.selectKeysSQL = "select kv_key from `" + table + "`;";



        createLoadAllKeysSQL(table);


        this.deleteStatementSQL = "delete  from `" + table + "` where kv_key = ?;";

        this.tableExistsSQL = "select * from `" + table + "` where 1!=1;";

        createTableSQL(table);
        createLoadAllVersionDataSQL(table);

        if (debug)
            logger.info("The following SQL statements will be used", "insert", this.insertStatementSQL, "select", this.selectStatementSQL,
                    "search", this.searchStatementSQL, "LOAD", this.loadAllSQL, "SELECT_KEYS", this.selectKeysSQL,
                    "DELETE", this.deleteStatementSQL, "TABLE EXISTS", this.tableExistsSQL, "CREATE_TABLE", this.createStatementSQL);

    }


    /**
     * Create load all keys SQL.
     * @param table
     */
    protected void createLoadAllKeysSQL(String table) {


        CharBuf buf = CharBuf.create(100);
        buf.add("select kv_key, kv_value, version, update_timestamp, create_timestamp from `");
        buf.add(table);
        buf.add("` where kv_key in (");
        buf.multiply("?,", this.loadKeyCount);
        buf.removeLastChar();
        buf.add(");");

        this.loadAllByKeysSQL = buf.toString();
    }


    /**
     * Create load all keys SQL.
     * @param table table
     */
    protected void createLoadAllVersionDataSQL(String table) {


        CharBuf buf = CharBuf.create(100);
        buf.add("select kv_key, 1, version, update_timestamp, create_timestamp from `");
        buf.add(table);
        buf.add("` where kv_key in (");
        buf.multiply("?,", this.loadKeyCount);
        buf.removeLastChar();
        buf.add(");");

        this.loadAllVersionDataByKeysSQL = buf.toString();
    }



    @Override
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



}

