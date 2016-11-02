package com.alain898.dscache.api.rest.request;

/**
 * Created by alain on 16/9/10.
 */
public class RestDeleteCacheRequest {
    private String cacheName;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }
}
