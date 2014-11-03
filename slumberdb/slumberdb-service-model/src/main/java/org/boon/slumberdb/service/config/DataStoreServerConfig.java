package org.boon.slumberdb.service.config;

import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.StartupMode;
import org.boon.Str;
import org.boon.core.Sys;

/**
 * Created by Richard on 7/2/14.
 */
public class DataStoreServerConfig {

    private static final String DEFAULT_FILE_LOCATION = "/opt/org/slumberdb/server.json";

    String logDirectory;
    int adminPort;
    private long statusInterval;
    private int websocketWorkerCount;
    private int dataStoreDrainerCount;
    private String websocketURI; //"/services/data/store"
    private String restURI; //"/services/data/store"
    private Boolean httpCompression = null; //true
    private int maxFrameSize = 0; //true
    private int batchResultSize = 0; //true
    private int port;
    private long broadcastInterval;
    private StartupMode startupMode;


    private boolean debug;

    public static DataStoreServerConfig load() {
        String fileLocation = Sys.sysProp("DataStoreServerConfig", DEFAULT_FILE_LOCATION);
        return Sys.loadFromFileLocation(DataStoreServerConfig.class, fileLocation);
    }

    public static DataStoreServerConfig config() {
        return new DataStoreServerConfig();
    }

    public StartupMode startupMode() {
        return startupMode == null ? StartupMode.LEVELDB_AND_MYSQL : startupMode;
    }

    public DataStoreServerConfig startupMode(StartupMode startupMode) {
        this.startupMode = startupMode;
        return this;
    }

    public boolean debug() {
        return debug;
    }

    public DataStoreServerConfig debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public int port() {
        return port == 0 ? ProtocolConstants.DEFAULT_PORT : port;
    }

    public DataStoreServerConfig port(int port) {
        this.port = port;
        return this;
    }

    public int websocketWorkerCount() {
        return websocketWorkerCount == 0 ? 16 : websocketWorkerCount;
    }

    public DataStoreServerConfig websocketWorkerCount(int websocketWorkerCount) {
        this.websocketWorkerCount = websocketWorkerCount;
        return this;
    }

    public int dataStoreDrainerCount() {
        return dataStoreDrainerCount == 0 ? 4 : dataStoreDrainerCount;
    }

    public DataStoreServerConfig dataStoreDrainerCount(int dataStoreDrainerCount) {
        this.dataStoreDrainerCount = dataStoreDrainerCount;
        return this;

    }

    public String websocketURI() {
        return websocketURI == null ? ProtocolConstants.DEFAULT_WEBSOCKET_URI : websocketURI;
    }

    public DataStoreServerConfig websocketURI(String websocketURI) {
        this.websocketURI = websocketURI;
        return this;

    }

    public String restURI() {

        return restURI == null ? ProtocolConstants.DEFAULT_REST_URI : restURI;
    }

    public DataStoreServerConfig restURI(String restURI) {
        this.restURI = restURI;
        return this;

    }

    public boolean httpCompression() {
        return httpCompression == null ? true : httpCompression;
    }

    public DataStoreServerConfig httpCompression(boolean httpCompression) {
        this.httpCompression = httpCompression;
        return this;

    }

    public int maxFrameSize() {
        return maxFrameSize == 0 ? 200_000_000 : maxFrameSize;
    }

    public DataStoreServerConfig maxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
        return this;
    }

    public int batchResultSize() {


        return batchResultSize == 0 ? 50 : batchResultSize;
    }

    public DataStoreServerConfig batchResultSize(int batchResultSize) {
        this.batchResultSize = batchResultSize;
        return this;
    }

    public DataStoreServerConfig statusInterval(long statusInterval) {
        this.statusInterval = statusInterval;
        return this;
    }

    public DataStoreServerConfig broadcastInterval(long broadcastInterval) {
        this.broadcastInterval = broadcastInterval;
        return this;

    }

    public long statusInterval() {
        return statusInterval == 0 ? 60_000 : statusInterval;
    }

    @Override
    public String toString() {
        return "DataStoreServerConfig{" +
                "websocketWorkerCount=" + websocketWorkerCount() +
                ", dataStoreDrainerCount=" + dataStoreDrainerCount() +
                ", websocketURI='" + websocketURI() + '\'' +
                ", restURI='" + restURI() + '\'' +
                ", httpCompression=" + httpCompression() +
                ", maxFrameSize=" + maxFrameSize() +
                ", batchResultSize=" + batchResultSize() +
                ", port=" + port() +
                ", startupMode=" + startupMode() +
                ", debug=" + debug() +
                '}';
    }

    public long broadcastInterval() {
        return broadcastInterval == 0 ? 5_000 : broadcastInterval;
    }

    public String logDirectory() {
        return (Str.isEmpty(logDirectory)) ? "/var/log/slumberdb/" : logDirectory;
    }

    public int adminPort() {
        if (adminPort == 0) {
            return port + 1010;
        } else {
            return adminPort;
        }
    }
}
