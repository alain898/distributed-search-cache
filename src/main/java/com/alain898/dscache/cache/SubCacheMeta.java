package com.alain898.dscache.cache;

import java.util.List;

/**
 * Created by alain on 16/8/24.
 */
public class SubCacheMeta {
    private int id;
    private String zkNodeName;
    private List<ReplicationMeta> replicationMetas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ReplicationMeta> getReplicationMetas() {
        return replicationMetas;
    }

    public void setReplicationMetas(List<ReplicationMeta> replicationMetas) {
        this.replicationMetas = replicationMetas;
    }

    public String getZkNodeName() {
        return zkNodeName;
    }

    public void setZkNodeName(String zkNodeName) {
        this.zkNodeName = zkNodeName;
    }
}
