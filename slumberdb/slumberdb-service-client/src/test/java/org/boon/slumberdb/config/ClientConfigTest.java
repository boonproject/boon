package org.boon.slumberdb.config;

import org.boon.slumberdb.service.config.Bucket;
import org.boon.slumberdb.service.config.DataStoreClientConfig;
import org.junit.Test;

import static org.boon.slumberdb.service.config.Bucket.bucket;
import static org.boon.Boon.puts;
import static org.boon.Boon.toJson;
import static org.boon.Str.rpad;
import static org.boon.Str.underBarCase;

/**
 * Created by Richard on 7/1/14.
 */
public class ClientConfigTest {


    @Test
    public void suggestedConfig() {

        DataStoreClientConfig config;

        int portPrimary = 8080;
        int portBackup = 9090;

        String server1 = "10.4.123.1";
        String server2 = "10.4.123.2";
        String server3 = "10.4.123.3";


        String backupServer1 = server3;
        String backupServer2 = server1;
        String backupServer3 = server2;

        config = DataStoreClientConfig.config().buckets(

                bucket(server1, portPrimary, backupServer1, portBackup),
                bucket(server2, portPrimary, backupServer2, portBackup),
                bucket(server3, portPrimary, backupServer3, portBackup),
                bucket(server1, portPrimary, backupServer1, portBackup),
                bucket(server2, portPrimary, backupServer2, portBackup),
                bucket(server3, portPrimary, backupServer3, portBackup),
                bucket(server1, portPrimary, backupServer1, portBackup),
                bucket(server2, portPrimary, backupServer2, portBackup),
                bucket(server3, portPrimary, backupServer3, portBackup)

        );


        puts(toJson(config));

        puts(rpad("PaulTabor79", 40, ' '), "in on server", config.pickBucket("PaulTabor79"));
        puts(rpad("JasonDaniel14", 40, ' '), "in on server", config.pickBucket("JasonDaniel14"));
        puts(rpad("EdMoonCountryMusicFan", 40, ' '), "in on server", config.pickBucket("EdMoonCountryMusicFan"));
        puts(rpad("SeanWalbergRoyalMounty", 40, ' '), "in on server", config.pickBucket("SeanWalbergRoyalMounty"));


        puts(rpad("PaulTabor79", 40, ' '), "in on backup", config.pickBucket("PaulTabor79"));
        puts(rpad("JasonDaniel14", 40, ' '), "in on backup", config.pickBucket("JasonDaniel14"));
        puts(rpad("EdMoonCountryMusicFan", 40, ' '), "in on backup", config.pickBucket("EdMoonCountryMusicFan"));
        puts(rpad("SeanWalbergRoyalMounty", 40, ' '), "in on backup", config.pickBucket("SeanWalbergRoyalMounty"));
    }

    @Test
    public void test() {

        DataStoreClientConfig config;

        String server1 = "10.4.123.1";

        String server2 = "10.4.123.2";

        String server3 = "10.4.123.3";


        config = DataStoreClientConfig.config().buckets(
                bucket(server1), bucket(server2), bucket(server3),

                bucket(server1), bucket(server2), bucket(server3),

                bucket(server1), bucket(server2), bucket(server3)

        );

        puts(config);

        puts(toJson(config));


        String backupServer1 = "10.4.346.1";

        String backupServer2 = "10.4.456.2";

        String backupServer3 = "10.4.891.3";

        config = DataStoreClientConfig.config().buckets(
                bucket(server1, backupServer3), bucket(server2, backupServer2), bucket(server3, backupServer1),

                bucket(server1, backupServer1), bucket(server2, backupServer3), bucket(server3, backupServer2),

                bucket(server1, backupServer1), bucket(server2, backupServer2), bucket(server3, backupServer3),

                bucket(server1, backupServer3), bucket(server2, backupServer2), bucket(server3, backupServer1),

                bucket(server1, backupServer1), bucket(server2, backupServer3), bucket(server3, backupServer2),

                bucket(server1, backupServer1), bucket(server2, backupServer2), bucket(server3, backupServer3),

                bucket(server1, backupServer3), bucket(server2, backupServer2), bucket(server3, backupServer1),

                bucket(server1, backupServer1), bucket(server2, backupServer3), bucket(server3, backupServer2),

                bucket(server1, backupServer1), bucket(server2, backupServer2), bucket(server3, backupServer3),

                bucket(server1, backupServer3), bucket(server2, backupServer2), bucket(server3, backupServer1),

                bucket(server1, backupServer1), bucket(server2, backupServer3), bucket(server3, backupServer2),

                bucket(server1, backupServer1), bucket(server2, backupServer2), bucket(server3, backupServer3),


                bucket(server1, backupServer1), bucket(server2, backupServer2),

                bucket(server3, 8080, backupServer3, 9090)


        );


        puts(config);

        puts(toJson(config));

    }


    @Test
    public void testLocal() {

        DataStoreClientConfig config = DataStoreClientConfig.config();

        /* CONFIG: Simple one bucket for hits. You have to run the data store client. */
        config.buckets(

                Bucket.bucket("localhost", 10100, "localhost", 10200),

                Bucket.bucket("localhost", 10200, "localhost", 10300),

                Bucket.bucket("localhost", 10300, "localhost", 10100)
        );


        puts(toJson(config));
    }


    @Test
    public void devIntegration() {

        DataStoreClientConfig config = DataStoreClientConfig.config();

        //nowdevhazel01/02/3, all under dmz.la3.org.com


        /* CONFIG: Simple one bucket for hits. You have to run the data store client. */
        config.buckets(

                Bucket.bucket("nowdevhazel01.dmz.la3.org.com", 8080, "nowdevhazel03.dmz.la3.org.com", 9090),

                Bucket.bucket("nowdevhazel02.dmz.la3.org.com", 8080, "nowdevhazel01.dmz.la3.org.com", 9090),

                Bucket.bucket("nowdevhazel03.dmz.la3.org.com", 8080, "nowdevhazel02.dmz.la3.org.com", 9090),

                Bucket.bucket("nowdevhazel01.dmz.la3.org.com", 8080, "nowdevhazel03.dmz.la3.org.com", 9090),

                Bucket.bucket("nowdevhazel02.dmz.la3.org.com", 8080, "nowdevhazel01.dmz.la3.org.com", 9090),

                Bucket.bucket("nowdevhazel03.dmz.la3.org.com", 8080, "nowdevhazel02.dmz.la3.org.com", 9090),

                Bucket.bucket("nowdevhazel01.dmz.la3.org.com", 8080, "nowdevhazel03.dmz.la3.org.com", 9090),

                Bucket.bucket("nowdevhazel02.dmz.la3.org.com", 8080, "nowdevhazel01.dmz.la3.org.com", 9090),

                Bucket.bucket("nowdevhazel03.dmz.la3.org.com", 8080, "nowdevhazel02.dmz.la3.org.com", 9090)
        );


        puts(toJson(config));

        puts(
                underBarCase("org.boon.slumberdb.config.DataStoreConfig"));
    }
}
