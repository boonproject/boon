package org.boon.slumberdb.leveldb;


import org.boon.Str;
import org.boon.slumberdb.leveldb.LevelDBKeyValueStore;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class LevelDBKeyValueStoreTest {

    LevelDBKeyValueStore store;

    @Before
    public void setup() {

        File file = new File("target/test-data");
        file = file.getAbsoluteFile();
        file.mkdirs();
        file = new File(file, "bytes.dat");
        store = new LevelDBKeyValueStore(file.toString(), null, true);

    }

    @Test
    public void test() {
        store.put("hello".getBytes(StandardCharsets.UTF_8),
                "world".getBytes(StandardCharsets.UTF_8)
        );

        byte[] world = store.load("hello".getBytes(StandardCharsets.UTF_8));
        Str.equalsOrDie("world", new String(world, StandardCharsets.UTF_8));
    }


}
