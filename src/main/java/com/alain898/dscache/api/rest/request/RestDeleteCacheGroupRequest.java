package com.alain898.dscache.api.rest.request;

/**
 * Created by alain on 16/9/8.
 */
public class RestDeleteCacheGroupRequest {
    private String cacheGroupName;

    public String getCacheGroupName() {
        return cacheGroupName;
    }

    public void setCacheGroupName(String cacheGroupName) {
        this.cacheGroupName = cacheGroupName;
    }
}
