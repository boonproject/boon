package org.boon.slumberdb.service.config;

import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.json.annotations.JsonIgnore;

/**
 * Created by Richard on 7/1/14.
 */
public class Bucket {

    private String server;
    private String backupServer;
    private int serverPort;
    private int backupServerPort;

    @JsonIgnore
    private transient int index;


    public Bucket(String server, int serverPort, String backupServer, int backupServerPort, int index) {
        this.server = server;
        this.backupServer = backupServer;
        this.serverPort = serverPort;
        this.backupServerPort = backupServerPort;
        this.index = index;
    }

    public static Bucket bucket(String server, int serverPort, String backupServer, int backupServerPort) {
        return new Bucket(server, serverPort, backupServer, backupServerPort, 0);
    }

    public static Bucket bucket(String server) {
        return new Bucket(server, ProtocolConstants.DEFAULT_PORT, null, 0, 0);

    }

    public static Bucket bucket(String server, String backupServer) {
        return new Bucket(server, ProtocolConstants.DEFAULT_PORT, backupServer, 0, ProtocolConstants.DEFAULT_PORT);

    }

    public static Bucket bucket(String server, int port) {
        return new Bucket(server, port, null, 0, 0);

    }

    public String server() {
        return server;
    }

    public Bucket server(String server) {
        this.server = server;
        return this;
    }

    public String backupServer() {
        return backupServer;
    }

    public Bucket backupServer(String backupServer) {
        this.backupServer = backupServer;
        return this;
    }

    public int serverPort() {
        return serverPort;
    }

    public Bucket serverPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public int backupServerPort() {
        return backupServerPort;
    }

    public Bucket backupServerPort(int backupServerPort) {
        this.backupServerPort = backupServerPort;
        return this;
    }

    public int index() {
        return index;
    }

    public Bucket index(int index) {
        this.index = index;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bucket)) return false;

        Bucket bucket = (Bucket) o;

        if (backupServerPort != bucket.backupServerPort) return false;
        if (index != bucket.index) return false;
        if (serverPort != bucket.serverPort) return false;
        if (backupServer != null ? !backupServer.equals(bucket.backupServer) : bucket.backupServer != null)
            return false;
        if (server != null ? !server.equals(bucket.server) : bucket.server != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = server != null ? server.hashCode() : 0;
        result = 31 * result + (backupServer != null ? backupServer.hashCode() : 0);
        result = 31 * result + serverPort;
        result = 31 * result + backupServerPort;
        result = 31 * result + index;
        return result;
    }


    @Override
    public String toString() {
        return "Bucket{" +
                "server='" + server + '\'' +
                ", backupServer='" + backupServer + '\'' +
                ", serverPort=" + serverPort +
                ", backupServerPort=" + backupServerPort +
                ", index=" + index +
                '}';
    }
}
