package org.boon.slumberdb.impl;

import org.boon.Boon;
import org.boon.Str;
import org.boon.concurrent.Timer;
import org.boon.slumberdb.entries.UpdateStatus;
import org.boon.slumberdb.entries.VersionedEntry;
import org.boon.slumberdb.entries.VersionedKeyValuePut;
import org.boon.slumberdb.spi.InMemoryVersionedStorageProvider;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Str.equalsOrDie;

/**
 * Created by Richard on 9/23/14.
 */
public class BaseVersionedStorageInMemoryTest {


    @Test
    public void test() throws Exception {

        InMemoryVersionedStorageProvider baseStore = new InMemoryVersionedStorageProvider();

        VersionedEntry<String, byte[]> versionedEntry = new VersionedEntry<>("Rick", null);

        versionedEntry.setValue("WAS HERE".getBytes(StandardCharsets.UTF_8));
        versionedEntry.setCreateTimestamp(99);
        versionedEntry.setVersion(1);
        versionedEntry.setUpdateTimestamp(66);

        baseStore.put("Rick", versionedEntry);

        final VersionedEntry<String, byte[]> rick = baseStore.load("Rick");

        equalsOrDie("Rick", rick.getKey());


        equalsOrDie("WAS HERE", new String(rick.getValue(), StandardCharsets.UTF_8));


        Boon.equalsOrDie(99L, rick.createdOn());

        Boon.equalsOrDie(66L, rick.updatedOn());

        Boon.equalsOrDie(1L, rick.version());




    }

    boolean ok;


    @Test
    public void testVersion() {

        InMemoryVersionedStorageProvider spi = new InMemoryVersionedStorageProvider();

        BinaryVersionedStore store = new BinaryVersionedStore(spi);

        VersionedEntry<String, byte[]> versionedEntry = new VersionedEntry<>("Rick", null);

        versionedEntry.setValue("WAS HERE".getBytes(StandardCharsets.UTF_8));
        versionedEntry.setCreateTimestamp(99);
        versionedEntry.setVersion(1);
        versionedEntry.setUpdateTimestamp(66);


        store.put("Rick", versionedEntry);

        VersionedEntry<String, byte[]> rick = store.load("Rick");

        equalsOrDie("Rick", rick.getKey());


        equalsOrDie("WAS HERE", new String(rick.getValue(), StandardCharsets.UTF_8));


        Boon.equalsOrDie(99L, rick.createdOn());

        Boon.equalsOrDie(66L, rick.updatedOn());

        Boon.equalsOrDie(1L, rick.version());






        UpdateStatus updateStatus = store.put("Rick", 4, versionedEntry.setVersion(3).value());

        ok = updateStatus == UpdateStatus.SUCCESS || die(updateStatus);


        updateStatus = store.put("Rick", 5,
                versionedEntry.setVersion(5).setUpdateTimestamp(System.currentTimeMillis()).value());

        ok = updateStatus == UpdateStatus.SUCCESS || die(updateStatus);


        long time = System.currentTimeMillis();

        updateStatus = store.put("Rick", -1,
                versionedEntry.value());

        long now = Timer.timer().now();

        ok = !updateStatus.isSuccessful()  || die(updateStatus);


        ok = updateStatus.versionKey()!=null  || die(updateStatus);


        ok = updateStatus.versionKey().version() == 5  || die(updateStatus);


        ok = updateStatus.versionKey().updatedOn() == now  || die(updateStatus);




        ok = updateStatus.versionKey().createdOn() == 99  || die(updateStatus);

        ok = updateStatus.versionKey().size() == 8  || die(updateStatus.versionKey().size());


        rick = store.load("Rick");


        Boon.equalsOrDie(99L, rick.createdOn());

        Boon.equalsOrDie(now, rick.updatedOn());

        Boon.equalsOrDie(5L, rick.version());


        updateStatus = store.put("Rick", 6, time+2000,
                versionedEntry.setVersion(6)
                        .setUpdateTimestamp(time + 2000)
                        .value("I love Diana"
                                .getBytes(StandardCharsets.UTF_8))
                        .value());

        ok = updateStatus == UpdateStatus.SUCCESS || die(updateStatus);


        rick = store.load("Rick");

        Boon.equalsOrDie(99L, rick.createdOn());

        Boon.equalsOrDie(time+2000, rick.updatedOn());

        Boon.equalsOrDie(6L, rick.version());

        Str.equalsOrDie("I love Diana", new String(rick.getValue(), StandardCharsets.UTF_8));


        //VersionedKeyValuePut<byte[]> put = new VersionedKeyValuePut<>();

        puts(rick);

    }
}
