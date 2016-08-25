package com.maxent.dscache.cache;

import java.util.List;

/**
 * Created by alain on 16/8/24.
 */
public class SubCacheMeta {
    private int id;
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
}
