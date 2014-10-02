package org.boon.slumberdb.integration;

import org.boon.core.Sys;

import java.io.IOException;

public class DataStoreMainDS3 {

    static {
        Sys.putSysProp("org.boon.slumberdb.DataStoreConfig", "/opt/org/slumberdb/datastore3.json");

    }


    public static void main(String... args) throws IOException {

        Utils.runDataStoreServer(10_300);
        System.in.read();
    }
}
