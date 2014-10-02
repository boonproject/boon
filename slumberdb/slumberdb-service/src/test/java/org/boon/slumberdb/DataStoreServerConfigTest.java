package org.boon.slumberdb;

import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.boon.slumberdb.stores.StartupMode;
import org.junit.Test;

import static org.boon.Boon.puts;
import static org.boon.Boon.toJson;

/**
 * Created by Richard on 7/2/14.
 */
public class DataStoreServerConfigTest {


    @Test
    public void test() {
        //This is more for documenting the config then actually being a real test

        final DataStoreServerConfig dataStoreServerConfig = DataStoreServerConfig.config()
                .dataStoreDrainerCount(5).debug(true)
                .httpCompression(true)
                .port(7070)
                .restURI("/rest/slumberdb")
                .dataStoreDrainerCount(5).websocketURI("/websocket/slumberdb")
                .startupMode(StartupMode.LEVELDB_AND_MYSQL)
                .websocketWorkerCount(5);

        puts(toJson(dataStoreServerConfig));

    }


    @Test
    public void defaults() {
        //This is more for documenting the config then actually being a real test

        final DataStoreServerConfig defaults = DataStoreServerConfig.config();

        final DataStoreServerConfig dataStoreServerConfig = DataStoreServerConfig.config()
                .dataStoreDrainerCount(defaults.dataStoreDrainerCount()).debug(defaults.debug())
                .httpCompression(defaults.httpCompression())
                .port(defaults.port())
                .restURI(defaults.restURI())
                .dataStoreDrainerCount(defaults.dataStoreDrainerCount()).websocketURI(defaults.websocketURI())
                .startupMode(defaults.startupMode())
                .websocketWorkerCount(defaults.websocketWorkerCount());

        puts(toJson(dataStoreServerConfig));

    }
}
