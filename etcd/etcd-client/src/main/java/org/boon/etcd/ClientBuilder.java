package org.boon.etcd;

import org.boon.Lists;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.util.List;

/**
 * Created by rhightower on 10/9/14.
 */
public class ClientBuilder {

    private boolean useSSL;
    private int poolSize=20;

    private int timeOutInMilliseconds=5000;


    private String sslTrustStorePath;
    private String sslTrustStorePassword;

    private String sslKeyStorePath;
    private String sslKeyStorePassword;

    private SSLContext sslContext;

    private boolean followLeader = true;


    private  URI[] hosts;

    private boolean sslAuthRequired;

    private boolean sslTrustAll;


    public boolean useSSL() {
        return useSSL;
    }

    public void useSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public int poolSize() {
        return poolSize;
    }

    public ClientBuilder poolSize(int poolSize) {
        this.poolSize = poolSize;
        return this;
    }

    public int timeOutInMilliseconds() {
        return timeOutInMilliseconds;
    }

    public ClientBuilder timeOutInMilliseconds(int timeOutInMilliseconds) {
        this.timeOutInMilliseconds = timeOutInMilliseconds;
        return this;
    }

    public String sslTrustStorePath() {
        return sslTrustStorePath;
    }

    public ClientBuilder sslTrustStorePath(String sslTrustStorePath) {
        this.sslTrustStorePath = sslTrustStorePath;
        return this;
    }

    public String sslTrustStorePassword() {
        return sslTrustStorePassword;
    }

    public ClientBuilder sslTrustStorePassword(String sslTrustStorePassword) {
        this.sslTrustStorePassword = sslTrustStorePassword;
        return this;
    }

    public String sslKeyStorePath() {
        return sslKeyStorePath;
    }

    public ClientBuilder sslKeyStorePath(String sslKeyStorePath) {
        this.sslKeyStorePath = sslKeyStorePath;
        return this;
    }

    public String sslKeyStorePassword() {
        return sslKeyStorePassword;
    }

    public ClientBuilder sslKeyStorePassword(String sslKeyStorePassword) {
        this.sslKeyStorePassword = sslKeyStorePassword;
        return this;
    }

    public boolean sslAuthRequired() {
        return sslAuthRequired;
    }

    public ClientBuilder sslAuthRequired(boolean sslAuthRequired) {
        this.sslAuthRequired = sslAuthRequired;
        return this;
    }

    public boolean sslTrustAll() {
        return sslTrustAll;
    }

    public ClientBuilder sslTrustAll(boolean sslTrustAll) {
        this.sslTrustAll = sslTrustAll;
        return this;
    }

    public List<URI> hosts() {
        return Lists.list(hosts);
    }

    public ClientBuilder hosts(URI... hosts) {
        this.hosts = hosts;
        return this;
    }



    public SSLContext sslContext() {
        return sslContext;
    }

    public ClientBuilder sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }


    public Etcd createClient() {
        return new EtcdClient(null, this);
    }

    public static ClientBuilder builder() {
        return new ClientBuilder();
    }

    public boolean followLeader() {
        return followLeader;
    }

    public ClientBuilder followLeader(boolean followLeader) {
        this.followLeader = followLeader;
        return this;
    }
}
