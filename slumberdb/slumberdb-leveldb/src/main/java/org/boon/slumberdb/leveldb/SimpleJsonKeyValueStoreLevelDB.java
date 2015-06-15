package org.boon.slumberdb.leveldb;


import org.boon.slumberdb.impl.SimpleJsonKeyValueStore;
import org.iq80.leveldb.Options;

public class SimpleJsonKeyValueStoreLevelDB<V> extends SimpleJsonKeyValueStore<V> {


    public SimpleJsonKeyValueStoreLevelDB(String fileName, Class<V> cls) {

        super(new LevelDBKeyValueStore(fileName), cls);
    }


    public SimpleJsonKeyValueStoreLevelDB(String fileName, Options options,  boolean log, Class<V> cls) {

        super(new LevelDBKeyValueStore(fileName, options, log), cls);
    }

}
