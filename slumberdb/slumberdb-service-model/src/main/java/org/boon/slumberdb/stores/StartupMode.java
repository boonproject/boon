package org.boon.slumberdb.stores;

/**
 * Created by Richard on 6/27/14.
 */
public enum StartupMode {

    /**
     * Read from MySQL, but do not write to MySQL.
     * If this is set, the server will *not* use LEVEL_DB but will use datatracker JSON logs and in-memory data store.
     * If there is a read and the read operation does not find the item in the in-memory store, it will use MYSQL to look
     * up the key.
     * If you use this, then it expected that you are using an indexer batch system to update MySQL like we did with datatracker.
     * <p/>
     * The item read in from MySQL will then populate the in-memory data store to avoid future data store misses.
     * <p/>
     * MySQL is backup.
     * The in-memory data-store is the master.
     */
    MYSQL_READONLY,

    /**
     * Read and write to MySQL.
     * If this is set, the server will *not* use LEVEL_DB but will use datatracker JSON logs and in-memory data store.
     * Every write operation to the system will go to the JSON log, in-memory store and then to MySQL.
     * It is all sent async as soon as the "set" operation comes in.
     * If there is a read and the read operation does not find the item in the in-memory store, it will use MYSQL to look
     * up the key.
     */
    MYSQL,

    /**
     * Read and write to LEVEL_DB but NOT MYSQL.
     * If this is set, the server will *not* use MYSQL but will use datatracker JSON logs and in-memory data store.
     * Every write operation to the system will go to the JSON log, in-memory store and then to LEVEL-DB.
     * It is all sent async as soon as the "set" operation comes in.
     * If there is a read and the read operation does not find the item in the in-memory store, it will use LEVEL-DB to look
     * up the key.
     * <p/>
     * LEVEL-DB uses a large off JVM heap memory cache and disk. It is wicked fast, and can be read very quickly from disk.
     * <p/>
     * We use LEVEL-DB to insulate the service from a MySQL slowdown or stoppage.
     * The system could run for days and days with a complete MySQL outage.
     * <p/>
     * We also use LEVEL-DB to warm up a data store on a cold start in mere seconds.
     * A 1 Gigabyte LEVEL-DB log can warm up a cold started node in less than 10 seconds (using SSD).
     */
    LEVELDB,


    /**
     * DEFAULT:
     * Read and write to LEVEL_DB and  MYSQL.
     * <p/>
     * Writes are sent async (all at once) JSON LOG->IN-MEMORY-STORE->LEVEL_DB->MYSQL.
     * <p/>
     * Reads are first read from the IN-MEMORY-STORE, if they are not found they are then read from LEVEL-DB,
     * if they are not found they are then read from MYSQL.
     * <p/>
     * A read fault from a higher layer means once the read is successful it written to the higher level.
     * <p/>
     * Thus a read miss on in-memory, and LEVEL-DB but a hit on MySQL will populate LEVEL-DB and the in-memory store.
     * This is a write behind read miss system.
     * <p/>
     * Thus a read miss on in-memory,  but a read hit hit on LEVEL-DB will populate the in-memory store.
     * This is a write behind read miss system.
     * <p/>
     * <p/>
     * Reads never happen from the JSON logs. The JSON logs are for DC to DC replication and for backup servers to follow
     * a NODE in a passive / active scenario.
     * <p/>
     * If this is set, the server will use MYSQL and LEVELDB as well as use the datatracker JSON logs and in-memory data store.
     * Every write operation to the system will go to the JSON log, in-memory store and then to LEVEL-DB and then MySQL.
     * It is all sent async as soon as the "set" operation comes in.
     * If there is a read and the read operation does not find the item in the in-memory store, it will use LEVEL-DB/MYSQL to look
     * up the key.
     * <p/>
     * LEVEL-DB uses a large off JVM heap memory cache and disk. It is wicked fast, and can be read very quickly from disk.
     */
    LEVELDB_AND_MYSQL,

    /**
     * In this scenario we do not use MySQL or LEVEL-DB. This would mostly be used for testing or debugging.
     * The data store would have no backing. You would need some other mechanism to warm it.
     */
    NO_BACKING_DB;

}
