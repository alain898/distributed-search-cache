package com.maxent.dscache.api.rest.request;

import com.maxent.dscache.cache.ICacheEntry;

/**
 * Created by alain on 16/8/21.
 */
public class RestCacheSearchRequest {
    private String cacheName;
    private ICacheEntry entries;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public ICacheEntry getEntries() {
        return entries;
    }

    public void setEntries(ICacheEntry entries) {
        this.entries = entries;
    }
}
