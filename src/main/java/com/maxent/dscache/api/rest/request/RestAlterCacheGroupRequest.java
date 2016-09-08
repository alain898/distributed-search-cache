package com.maxent.dscache.api.rest.request;

/**
 * Created by alain on 16/9/8.
 */
public class RestAlterCacheGroupRequest {
    private String cacheGroupName;
    private int addedCaches;

    public String getCacheGroupName() {
        return cacheGroupName;
    }

    public void setCacheGroupName(String cacheGroupName) {
        this.cacheGroupName = cacheGroupName;
    }

    public int getAddedCaches() {
        return addedCaches;
    }

    public void setAddedCaches(int addedCaches) {
        this.addedCaches = addedCaches;
    }
}
