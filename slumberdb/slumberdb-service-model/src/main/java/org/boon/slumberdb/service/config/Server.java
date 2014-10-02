package org.boon.slumberdb.service.config;

/**
 * Created by Richard on 7/1/14.
 */
public class Server {
    final String host;
    final int port;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    @Override
    public String toString() {
        return "Server{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Server)) return false;

        Server server = (Server) o;

        if (port != server.port) return false;
        if (host != null ? !host.equals(server.host) : server.host != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}
