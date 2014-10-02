package org.boon.slumberdb.integration;

import java.io.IOException;

/**
 * Created by Richard on 9/3/14.
 */
public class EvilDS1 {


    static {
        System.setProperty("org.boon.slumberdb.DataStoreConfig", "/opt/org/slumberdb/datastore1.json");

    }


    public static void main(String... args) throws IOException {

        Utils.runDataStoreServer(10_500);
        System.in.read();
    }

}
