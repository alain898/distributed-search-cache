package com.maxent.dscache.cache;

/**
 * Created by alain on 16/8/24.
 */
public class ReplicationMeta {
    private int id;
    private String zkNodeName;
    private Host host;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public String getZkNodeName() {
        return zkNodeName;
    }

    public void setZkNodeName(String zkNodeName) {
        this.zkNodeName = zkNodeName;
    }
}
