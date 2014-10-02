package org.boon.slumberdb.service.config;


import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.Exceptions;
import org.boon.IO;
import org.boon.Str;
import org.boon.core.Sys;
import org.boon.json.JsonParserFactory;
import org.boon.json.annotations.JsonIgnore;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.boon.Boon.puts;
import static org.boon.Lists.list;

/**
 * Created by Richard on 7/1/14.
 */
public class DataStoreClientConfig {

    private final static String FILE_LOCATION = Sys.sysProp("org.boon.slumberdb.DataStoreClientConfig", "/opt/org/slumberdb/client.json");

    private List<Bucket> buckets;

    private String websocketURI;

    private String restURI;

    private String clientId;
    @JsonIgnore
    private AtomicLong count = new AtomicLong();


    private int maxFrameSize = 0; //true

    private int batchResultSize = 15; //true

    public DataStoreClientConfig(Bucket... buckets) {
        this(list(buckets));
    }


    public DataStoreClientConfig(List<Bucket> buckets) {
        this.buckets = buckets;

        sequence();

    }

    public static DataStoreClientConfig load() {

        puts("Config for data store client", FILE_LOCATION);

        if (IO.exists(FILE_LOCATION)) {
            try {
                return new JsonParserFactory().create().parseFile(DataStoreClientConfig.class, FILE_LOCATION);
            } catch (Exception ex) {
                Exceptions.handle(ex, "Unable to read config file", FILE_LOCATION, "for data store client config");
                return null;
            }
        } else {

            puts("WARNING", FILE_LOCATION, "does not exist for data store client config!!!");
            return new DataStoreClientConfig();
        }
    }

    public static DataStoreClientConfig config() {
        return new DataStoreClientConfig();
    }

    public static DataStoreClientConfig config(Bucket... buckets) {
        return new DataStoreClientConfig(buckets);
    }

    public List<Bucket> buckets() {
        return buckets;
    }

    public DataStoreClientConfig buckets(List<Bucket> buckets) {
        this.buckets = buckets;


        sequence();

        return this;
    }

    public DataStoreClientConfig buckets(Bucket... buckets) {
        this.buckets = list(buckets);


        sequence();

        return this;
    }

    private void sequence() {
        int index = 0;
        for (Bucket bucket : buckets) {
            bucket.index(index);
            index++;
        }
    }

    @Override
    public boolean equals(Object o) {
        sequence();
        if (this == o) return true;
        if (!(o instanceof DataStoreClientConfig)) return false;

        DataStoreClientConfig that = (DataStoreClientConfig) o;

        if (buckets != null ? !buckets.equals(that.buckets) : that.buckets != null) return false;

        return true;
    }

    @Override
    public int hashCode() {

        sequence();
        return buckets != null ? buckets.hashCode() : 0;
    }

    @Override
    public String toString() {

        sequence();
        return "DataStoreClientConfig{" +
                "buckets=" + buckets +
                '}';
    }

    public Bucket pickBucket(String key) {

        int hash = key.hashCode() % buckets.size();

        hash = hash >= 0 ? hash : hash * -1;

        Bucket bucket = buckets.get(hash);

        return bucket;

    }

    public String websocketURI() {
        return websocketURI == null ? ProtocolConstants.DEFAULT_WEBSOCKET_URI : websocketURI;
    }

    public DataStoreClientConfig websocketURI(String websocketURI) {
        this.websocketURI = websocketURI;
        return this;
    }

    public String restURI() {
        return restURI == null ? ProtocolConstants.DEFAULT_WEBSOCKET_URI : restURI;
    }

    public DataStoreClientConfig restURI(String restURI) {
        this.restURI = restURI;
        return this;
    }

    public int maxFrameSize() {
        return maxFrameSize == 0 ? 20_000_000 : maxFrameSize;
    }

    public DataStoreClientConfig maxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
        return this;
    }


    public String clientId() {

        if (count == null) {
            count = new AtomicLong();
        }
        if (clientId == null) {
            clientId = UUID.randomUUID().toString();
        }
        return Str.add(clientId, ".", "" + count.incrementAndGet());
    }

    public DataStoreClientConfig clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }
}
