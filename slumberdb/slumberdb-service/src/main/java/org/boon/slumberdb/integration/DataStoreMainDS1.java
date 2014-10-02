package org.boon.slumberdb.integration;


import org.boon.core.Sys;

import java.io.IOException;

public class DataStoreMainDS1 {

    static {
        Sys.putSysProp("org.boon.slumberdb.DataStoreConfig", "/opt/org/slumberdb/datastore1.json");

    }


    public static void main(String... args) throws IOException {

        Utils.runDataStoreServer(10_100);
        System.in.read();
    }

}
