package com.alain898.dscache.api.rest.request;

import java.util.Map;

/**
 * Created by alain on 16/8/30.
 */
public class RestSubcacheSaveRequest {
    private String cacheName;
    private String subCacheId;
    private Map queryEntry;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public Map getQueryEntry() {
        return queryEntry;
    }

    public void setQueryEntry(Map queryEntry) {
        this.queryEntry = queryEntry;
    }

    public String getSubCacheId() {
        return subCacheId;
    }

    public void setSubCacheId(String subCacheId) {
        this.subCacheId = subCacheId;
    }
}
