package org.boon.slumberdb.stores;

/**
 * Created by Richard on 6/27/14.
 */
public enum DataStoreSource {

    ALL,
    MEMORY,

    TRANSACTION_LOG,
    LOCAL_DB,
    REMOTE_DB,
    CLIENT,
    REPLICA,
    END,
    SERVER,
    FILE_SYSTEM,
    LOCAL_DB_GET,
    LOCAL_DB_LOAD,
    REMOTE_DB_GET,
    LOCAL_STORES,
    NONE,;
}
