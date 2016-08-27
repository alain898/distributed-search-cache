package com.maxent.dscache.cache;

/**
 * Created by alain on 16/8/20.
 */
public class Host {
    private static int INVALID_ID = -1;
    private static int INVALID_PORT = -1;
    private int id;
    private String host;
    private int port;

    public Host() {
        this(INVALID_ID, null, INVALID_PORT);
    }

    public Host(String host, int port) {
        this(INVALID_ID, host, port);
    }

    public Host(int id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

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
