package com.maxent.dscache.cache;

import java.util.List;

/**
 * Created by alain on 16/9/3.
 */
public class CacheGroupMeta {
    private String cacheGroupName;
    private List<CacheMeta> cacheMetas;
    private int cacheGroupCapacity;     // 2^C
    private int currentCachesNumber;    // 2^cm
    private int lastCachesNumber;       // 2^lm


    public String getCacheGroupName() {
        return cacheGroupName;
    }

    public void setCacheGroupName(String cacheGroupName) {
        this.cacheGroupName = cacheGroupName;
    }

    public List<CacheMeta> getCacheMetas() {
        return cacheMetas;
    }

    public void setCacheMetas(List<CacheMeta> cacheMetas) {
        this.cacheMetas = cacheMetas;
    }

    public int getCurrentCachesNumber() {
        return currentCachesNumber;
    }

    public void setCurrentCachesNumber(int currentCachesNumber) {
        this.currentCachesNumber = currentCachesNumber;
    }

    public int getLastCachesNumber() {
        return lastCachesNumber;
    }

    public void setLastCachesNumber(int lastCachesNumber) {
        this.lastCachesNumber = lastCachesNumber;
    }

    public int getCacheGroupCapacity() {
        return cacheGroupCapacity;
    }

    public void setCacheGroupCapacity(int cacheGroupCapacity) {
        this.cacheGroupCapacity = cacheGroupCapacity;
    }
}