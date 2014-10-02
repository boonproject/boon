package org.boon.slumberdb.integration;

import org.boon.core.Sys;

import java.io.IOException;

public class DataStoreMainDS2 {

    static {
        Sys.putSysProp("org.boon.slumberdb.DataStoreConfig", "/opt/org/slumberdb/datastore2.json");

    }


    public static void main(String... args) throws IOException {

        Utils.runDataStoreServer(10_200);
        System.in.read();
    }
}
