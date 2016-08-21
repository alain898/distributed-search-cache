package com.maxent.dscache.cache;

/**
 * Created by alain on 16/8/20.
 */
public class PhysicalHost {
    /**
     * VirtualHost 和 PhysicalHost 是多对多的对应关系,
     */
    private String host;
    private int port;

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
