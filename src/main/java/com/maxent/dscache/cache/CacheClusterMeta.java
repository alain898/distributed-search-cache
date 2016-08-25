package com.maxent.dscache.cache;

import java.util.List;

/**
 * Created by alain on 16/8/24.
 */
public class CacheClusterMeta {
    private String version;
    private List<CacheMeta> caches;
    private List<Host> hosts;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<CacheMeta> getCaches() {
        return caches;
    }

    public void setCaches(List<CacheMeta> caches) {
        this.caches = caches;
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }
}
