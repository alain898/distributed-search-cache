package com.maxent.dscache.api.rest.request;

import java.util.Map;

/**
 * Created by alain on 16/8/26.
 */
public class RestSubCacheSearchRequest {
    private String cache_name;
    private Map query_entry;

    public String getCache_name() {
        return cache_name;
    }

    public void setCache_name(String cache_name) {
        this.cache_name = cache_name;
    }

    public Map getQuery_entry() {
        return query_entry;
    }

    public void setQuery_entry(Map query_entry) {
        this.query_entry = query_entry;
    }
}
