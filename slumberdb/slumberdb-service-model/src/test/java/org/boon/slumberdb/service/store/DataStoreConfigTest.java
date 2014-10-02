package org.boon.slumberdb.service.store;

import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.Str;
import org.junit.Test;

import static org.boon.Boon.puts;
import static org.boon.Boon.toJson;

/**
 * Created by Richard on 7/2/14.
 */
public class DataStoreConfigTest {

    @Test
    public void test() {
        DataStoreConfig defaults = DataStoreConfig.config();

        DataStoreConfig config = DataStoreConfig.config();


        config.checkStatusEveryIntervalMS(defaults.checkStatusEveryIntervalMS())
                .dbReadFlushQueueIntervalMS(defaults.dbReadFlushQueueIntervalMS())
                .dbMaxReadBatch(defaults.dbMaxReadBatch())
                .dbMinReadBatch(defaults.dbMinReadBatch())
                .dbMaxWriteBatch(defaults.dbMaxWriteBatch())
                .dbPassword("slumber1234")
                .dbUser("sean")
                .dbTable("user_tbl")
                .dbReaderCount(defaults.dbReaderCount())
                .dbWriterCount(defaults.dbWriterCount())
                .dbUrl("jdbc:mysql://localhost:3306/slumberdb")
                .dbReadFlushQueueIntervalMS(defaults.dbReadFlushQueueIntervalMS())
                .dbWriteFlushQueueIntervalMS(defaults.dbWriteFlushQueueIntervalMS())
                .levelDBCacheSize(defaults.levelDBCacheSize())
                .levelDBFileName(defaults.levelDBFileName())
                .levelDBMaxOpenFiles(defaults.levelDBMaxOpenFiles())
                .threadErrorResumeTimeMS(defaults.threadErrorResumeTimeMS())
                .outputDirectory(defaults.outputDirectory());

        puts(toJson(config));

        puts("DataStoreConfig", Str.underBarCase("DataStoreConfig"));

    }
}
