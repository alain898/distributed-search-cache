package com.alain898.dscache.api.rest.request;

import java.util.Map;

/**
 * Created by alain on 16/8/26.
 */
public class RestSubCacheSearchRequest {
    private String cacheName;
    private String subCacheId;
    private String searchMode;
    private String searchPolicy;
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

    public String getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(String searchMode) {
        this.searchMode = searchMode;
    }

    public String getSearchPolicy() {
        return searchPolicy;
    }

    public void setSearchPolicy(String searchPolicy) {
        this.searchPolicy = searchPolicy;
    }
}
