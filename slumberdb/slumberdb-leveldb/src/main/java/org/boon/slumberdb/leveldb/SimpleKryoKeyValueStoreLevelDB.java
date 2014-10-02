package org.boon.slumberdb.leveldb;

import org.boon.slumberdb.impl.SimpleKryoKeyValueStore;
import org.iq80.leveldb.Options;

import java.io.Serializable;

/**
 * Created by Richard on 4/5/14.
 */
public class SimpleKryoKeyValueStoreLevelDB<T extends Serializable> extends SimpleKryoKeyValueStore<T> {


    public SimpleKryoKeyValueStoreLevelDB(String fileName, Options options, boolean log, Class<T> type) {
        super(new LevelDBKeyValueStore(fileName, options, log), type);
    }


    public SimpleKryoKeyValueStoreLevelDB(String fileName, Class<T> type) {
        super(new LevelDBKeyValueStore(fileName), type);
    }
}
