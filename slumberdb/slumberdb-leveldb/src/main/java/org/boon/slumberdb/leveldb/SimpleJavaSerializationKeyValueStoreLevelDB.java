package org.boon.slumberdb.leveldb;

import org.boon.slumberdb.impl.SimpleJavaSerializationStore;
import org.iq80.leveldb.Options;

import java.io.Serializable;

public class SimpleJavaSerializationKeyValueStoreLevelDB<T extends Serializable> extends SimpleJavaSerializationStore<T> {

    public SimpleJavaSerializationKeyValueStoreLevelDB(String fileName, Options options, boolean log) {
        super(new LevelDBKeyValueStore(fileName, options, log));
    }


    public SimpleJavaSerializationKeyValueStoreLevelDB(String fileName) {
        super(new LevelDBKeyValueStore(fileName));
    }
}
