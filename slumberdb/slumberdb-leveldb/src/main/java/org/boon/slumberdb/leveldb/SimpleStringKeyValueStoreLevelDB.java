package org.boon.slumberdb.leveldb;


import org.boon.slumberdb.impl.SimpleStringKeyValueStore;
import org.iq80.leveldb.Options;


public class SimpleStringKeyValueStoreLevelDB extends SimpleStringKeyValueStore {

    public SimpleStringKeyValueStoreLevelDB(String fileName, Options options) {
        super(new LevelDBKeyValueStore(fileName, options, false));

    }

    public SimpleStringKeyValueStoreLevelDB(String fileName, Options options, boolean log) {
        super(new LevelDBKeyValueStore(fileName, options, log));
    }

    public SimpleStringKeyValueStoreLevelDB(String fileName) {
        super(new LevelDBKeyValueStore(fileName));
    }


}

