package org.boon.slumberdb.service.config;

import org.boon.Lists;
import org.boon.core.Sys;

import java.util.List;

/**
 * Created by Richard on 7/2/14.
 * Modified By Scott Oct '14
 */
public class DataStoreConfig {

    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String dbTable;
    private int dbMaxReadBatch;//= 1000;
    private int dbMinReadBatch;// = 100;
    private int dbMaxWriteBatch;// = 1000;
    private int sqlBatchWrite;// = 1000;

    private int levelDBBatchReaderCount = 0;//5


    private int checkStatusEveryIntervalMS;// = 5_000;
    private int threadErrorResumeTimeMS;//=100;

    private List<String> mySQLKeyBlackList = Lists.list();


    private int pollTimeout;//100

    private int processQueueMaxBatchSize;//1_000

    private String outputDirectory; ///opt/org/userdatatracker

    private String levelDBFileName; //"userdata.dat"

    private int levelDBCacheSize;// = 1_073_741_824;

    private int levelDBWriteBufferSize;// = 268_435_456;

    private int levelDBMaxOpenFiles;// = 1024;

    private int dbReaderCount;

    private int dbWriterCount;


    private int dbReadFlushQueueIntervalMS;
    private int dbWriteFlushQueueIntervalMS;

    private static final String DEFAULT_FILE_LOCATION = "/opt/org/slumberdb/slumberdb.json";

    public static DataStoreConfig load() {
        String fileLocation = Sys.sysPropMultipleKeys(
                "org.boon.slumberdb.DataStoreConfig", // POSSIBLE KEY, KEPT FOR BACKWARD COMPATIBLE
                "DataStoreConfig"                     // POSSIBLE KEY
        );

        return Sys.loadFromFileLocation(DataStoreConfig.class, fileLocation, DEFAULT_FILE_LOCATION);
    }

    public static DataStoreConfig config() {
        return new DataStoreConfig();
    }

    public int dbReadFlushQueueIntervalMS() {
        return dbReadFlushQueueIntervalMS == 0 ? 250 : dbReadFlushQueueIntervalMS;
    }

    public DataStoreConfig dbReadFlushQueueIntervalMS(int dbFlushQueueIntervalMS) {
        this.dbReadFlushQueueIntervalMS = dbFlushQueueIntervalMS;
        return this;
    }

    public int dbWriteFlushQueueIntervalMS() {
        return dbWriteFlushQueueIntervalMS == 0 ? 5_000 : dbWriteFlushQueueIntervalMS;
    }

    public DataStoreConfig dbWriteFlushQueueIntervalMS(int dbWriteFlushQueueIntervalMS) {
        this.dbWriteFlushQueueIntervalMS = dbWriteFlushQueueIntervalMS;
        return this;
    }

    public int dbReaderCount() {
        return dbReaderCount == 0 ? 5 : dbReaderCount;
    }

    public DataStoreConfig dbReaderCount(int dbReaderCount) {
        this.dbReaderCount = dbReaderCount;
        return this;
    }

    public int dbWriterCount() {

        return dbWriterCount == 0 ? 10 : dbWriterCount;
    }

    public DataStoreConfig dbWriterCount(int dbWriterCount) {
        this.dbWriterCount = dbWriterCount;
        return this;
    }

    public int checkStatusEveryIntervalMS() {
        return checkStatusEveryIntervalMS == 0 ? 30_000 : checkStatusEveryIntervalMS;
    }

    public DataStoreConfig checkStatusEveryIntervalMS(int checkStatusEveryIntervalMS) {
        this.checkStatusEveryIntervalMS = checkStatusEveryIntervalMS;
        return this;
    }

    public int levelDBCacheSize() {
        return levelDBCacheSize == 0 ? 1_073_741_824 : levelDBCacheSize;
    }

    public DataStoreConfig levelDBCacheSize(int levelDBCacheSize) {
        this.levelDBCacheSize = levelDBCacheSize;
        return this;
    }

    public int levelDBWriteBufferSize() {
        return levelDBWriteBufferSize == 0 ? 268_435_456 : levelDBWriteBufferSize;
    }

    public DataStoreConfig levelDBWriteBufferSize(int levelDBWriteBufferSize) {
        this.levelDBWriteBufferSize = levelDBWriteBufferSize;
        return this;
    }

    public int levelDBMaxOpenFiles() {
        return levelDBMaxOpenFiles == 0 ? 1024 : levelDBMaxOpenFiles;
    }

    public DataStoreConfig levelDBMaxOpenFiles(int levelDBMaxOpenFiles) {
        this.levelDBMaxOpenFiles = levelDBMaxOpenFiles;
        return this;
    }

    public String levelDBFileName() {
        return levelDBFileName == null ? "slumberdb.dat" : levelDBFileName;
    }

    public DataStoreConfig levelDBFileName(String levelDBFileName) {
        this.levelDBFileName = levelDBFileName;
        return this;
    }

    public String outputDirectory() {
        return outputDirectory == null ? "/opt/org/slumberdb/data" : outputDirectory;
    }

    public DataStoreConfig outputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        return this;
    }

    public int threadErrorResumeTimeMS() {
        return threadErrorResumeTimeMS == 0 ? 100 : threadErrorResumeTimeMS;
    }

    public DataStoreConfig threadErrorResumeTimeMS(int threadErrorResumeTimeMS) {
        this.threadErrorResumeTimeMS = threadErrorResumeTimeMS;
        return this;
    }

    public int pollTimeoutMS() {

        return pollTimeout == 0 ? 1 : pollTimeout;
    }

    public DataStoreConfig pollTimeoutMS(int pollTimeout) {
        this.pollTimeout = pollTimeout;
        return this;
    }

    public int processQueueMaxBatchSize() {
        return processQueueMaxBatchSize == 0 ? 1_000 : processQueueMaxBatchSize;
    }

    public DataStoreConfig setProcessQueueMaxBatchSize(int processQueueMaxBatchSize) {
        this.processQueueMaxBatchSize = processQueueMaxBatchSize;
        return this;
    }

    public String dbUrl() {
        return dbUrl;
    }

    public DataStoreConfig dbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
        return this;
    }

    public String dbUser() {
        return dbUser;
    }

    public DataStoreConfig dbUser(String dbUser) {
        this.dbUser = dbUser;
        return this;
    }

    public String dbPassword() {
        return dbPassword;
    }

    public DataStoreConfig dbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
        return this;
    }

    public String dbTable() {
        return dbTable;
    }

    public DataStoreConfig dbTable(String dbTable) {
        this.dbTable = dbTable;
        return this;
    }

    public int dbMaxReadBatch() {
        return dbMaxReadBatch == 0 ? 50 : dbMaxReadBatch;
    }

    public DataStoreConfig dbMaxReadBatch(int dbMaxReadBatch) {
        this.dbMaxReadBatch = dbMaxReadBatch;
        return this;
    }

    public int dbMinReadBatch() {

        return dbMinReadBatch == 0 ? 10 : dbMinReadBatch;
    }

    public DataStoreConfig dbMinReadBatch(int dbMinReadBatch) {
        this.dbMinReadBatch = dbMinReadBatch;
        return this;
    }

    public int dbMaxWriteBatch() {

        return dbMaxWriteBatch == 0 ? 500 : dbMaxWriteBatch;

    }

    public DataStoreConfig dbMaxWriteBatch(int dbMaxWriteBatch) {
        this.dbMaxWriteBatch = dbMaxWriteBatch;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataStoreConfig)) return false;

        DataStoreConfig config = (DataStoreConfig) o;

        if (checkStatusEveryIntervalMS != config.checkStatusEveryIntervalMS) return false;
        if (dbMaxReadBatch != config.dbMaxReadBatch) return false;
        if (dbMaxWriteBatch != config.dbMaxWriteBatch) return false;
        if (dbMinReadBatch != config.dbMinReadBatch) return false;
        if (dbReadFlushQueueIntervalMS != config.dbReadFlushQueueIntervalMS) return false;
        if (dbReaderCount != config.dbReaderCount) return false;
        if (dbWriteFlushQueueIntervalMS != config.dbWriteFlushQueueIntervalMS) return false;
        if (dbWriterCount != config.dbWriterCount) return false;
        if (levelDBCacheSize != config.levelDBCacheSize) return false;
        if (levelDBMaxOpenFiles != config.levelDBMaxOpenFiles) return false;
        if (levelDBWriteBufferSize != config.levelDBWriteBufferSize) return false;
        if (pollTimeout != config.pollTimeout) return false;
        if (processQueueMaxBatchSize != config.processQueueMaxBatchSize) return false;
        if (threadErrorResumeTimeMS != config.threadErrorResumeTimeMS) return false;
        if (dbPassword != null ? !dbPassword.equals(config.dbPassword) : config.dbPassword != null) return false;
        if (dbTable != null ? !dbTable.equals(config.dbTable) : config.dbTable != null) return false;
        if (dbUrl != null ? !dbUrl.equals(config.dbUrl) : config.dbUrl != null) return false;
        if (dbUser != null ? !dbUser.equals(config.dbUser) : config.dbUser != null) return false;
        if (levelDBFileName != null ? !levelDBFileName.equals(config.levelDBFileName) : config.levelDBFileName != null)
            return false;
        if (outputDirectory != null ? !outputDirectory.equals(config.outputDirectory) : config.outputDirectory != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dbUrl != null ? dbUrl.hashCode() : 0;
        result = 31 * result + (dbUser != null ? dbUser.hashCode() : 0);
        result = 31 * result + (dbPassword != null ? dbPassword.hashCode() : 0);
        result = 31 * result + (dbTable != null ? dbTable.hashCode() : 0);
        result = 31 * result + dbMaxReadBatch;
        result = 31 * result + dbMinReadBatch;
        result = 31 * result + dbMaxWriteBatch;
        result = 31 * result + checkStatusEveryIntervalMS;
        result = 31 * result + threadErrorResumeTimeMS;
        result = 31 * result + pollTimeout;
        result = 31 * result + processQueueMaxBatchSize;
        result = 31 * result + (outputDirectory != null ? outputDirectory.hashCode() : 0);
        result = 31 * result + (levelDBFileName != null ? levelDBFileName.hashCode() : 0);
        result = 31 * result + levelDBCacheSize;
        result = 31 * result + levelDBWriteBufferSize;
        result = 31 * result + levelDBMaxOpenFiles;
        result = 31 * result + dbReaderCount;
        result = 31 * result + dbWriterCount;
        result = 31 * result + dbReadFlushQueueIntervalMS;
        result = 31 * result + dbWriteFlushQueueIntervalMS;
        return result;
    }

    @Override
    public String toString() {
        return "DataStoreConfig{" +
                "dbUrl='" + dbUrl() + '\'' +
                ", dbUser='" + dbUser() + '\'' +
                ", dbPassword='" + dbPassword.length() + '\'' +
                ", dbTable='" + dbTable() + '\'' +
                ", dbMaxReadBatch=" + dbMaxReadBatch() +
                ", dbMinReadBatch=" + dbMinReadBatch() +
                ", dbMaxWriteBatch=" + dbMaxWriteBatch() +
                ", checkStatusEveryIntervalMS=" + checkStatusEveryIntervalMS() +
                ", threadErrorResumeTimeMS=" + threadErrorResumeTimeMS() +
                ", pollTimeout=" + pollTimeoutMS() +
                ", processQueueMaxBatchSize=" + processQueueMaxBatchSize() +
                ", outputDirectory='" + outputDirectory() + '\'' +
                ", levelDBFileName='" + levelDBFileName() +
                ", levelDBCacheSize=" + levelDBCacheSize() +
                ", levelDBWriteBufferSize=" + levelDBWriteBufferSize() +
                ", levelDBMaxOpenFiles=" + levelDBMaxOpenFiles() +
                ", dbReaderCount=" + dbReaderCount() +
                ", dbWriterCount=" + dbWriterCount() +
                ", dbReadFlushQueueIntervalMS=" + dbReadFlushQueueIntervalMS() +
                ", dbWriteFlushQueueIntervalMS=" + dbWriteFlushQueueIntervalMS() +
                '}';
    }

    public List<String> mySQLKeyBlackList() {
        return mySQLKeyBlackList;
    }

    public DataStoreConfig mySQLKeyBlackList(List<String> mySQLKeyBlackList) {
        this.mySQLKeyBlackList = mySQLKeyBlackList;
        return this;
    }


    public int sqlBatchWrite() {
        return sqlBatchWrite == 0 ? 200 : sqlBatchWrite;
    }

    public void sqlBatchWrite(int sqlBatchWrite) {
        this.sqlBatchWrite = sqlBatchWrite;
    }

    public int levelDBBatchReaderCount() {

        return levelDBBatchReaderCount == 0 ? 5 : levelDBBatchReaderCount;
    }
}
