package com.maxent.dscache.cache;

import java.util.List;

/**
 * Created by alain on 16/9/6.
 */
public class CacheGroupZnode {
    private String cacheGroupName;
    private List<String> caches;
    private int cacheGroupCapacity;     // 2^C
    private int currentCachesNumber;    // 2^cm
    private int lastCachesNumber;       // 2^lm

    public String getCacheGroupName() {
        return cacheGroupName;
    }

    public void setCacheGroupName(String cacheGroupName) {
        this.cacheGroupName = cacheGroupName;
    }

    public List<String> getCaches() {
        return caches;
    }

    public void setCaches(List<String> caches) {
        this.caches = caches;
    }

    public int getCacheGroupCapacity() {
        return cacheGroupCapacity;
    }

    public void setCacheGroupCapacity(int cacheGroupCapacity) {
        this.cacheGroupCapacity = cacheGroupCapacity;
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
}
