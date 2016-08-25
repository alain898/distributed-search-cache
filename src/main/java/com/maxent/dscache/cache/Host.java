package com.maxent.dscache.cache;

/**
 * Created by alain on 16/8/20.
 */
public class Host {
    /**
     * VirtualHost 和 Host 是多对多的对应关系,
     */
    private int id;
    private String host;
    private int port;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
