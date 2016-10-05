package com.maxent.dscache.cache;

import java.util.List;

/**
 * Created by alain on 16/9/3.
 */
public class CacheGroupMeta {
    private String cacheGroupName;
    private String entryClassName;
    private int subCachesPerCache;
    private int cacheGroupCapacity;     // 2^C
    private int partitionsPerSubCache;
    private int blocksPerPartition;
    private int blockCapacity;

    private List<CacheMeta> cacheMetas;

    private int currentCachesNumber;    // 2^cm

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

    public int getCacheGroupCapacity() {
        return cacheGroupCapacity;
    }

    public void setCacheGroupCapacity(int cacheGroupCapacity) {
        this.cacheGroupCapacity = cacheGroupCapacity;
    }

    public String getEntryClassName() {
        return entryClassName;
    }

    public void setEntryClassName(String entryClassName) {
        this.entryClassName = entryClassName;
    }

    public int getSubCachesPerCache() {
        return subCachesPerCache;
    }

    public void setSubCachesPerCache(int subCachesPerCache) {
        this.subCachesPerCache = subCachesPerCache;
    }

    public int getPartitionsPerSubCache() {
        return partitionsPerSubCache;
    }

    public void setPartitionsPerSubCache(int partitionsPerSubCache) {
        this.partitionsPerSubCache = partitionsPerSubCache;
    }

    public int getBlocksPerPartition() {
        return blocksPerPartition;
    }

    public void setBlocksPerPartition(int blocksPerPartition) {
        this.blocksPerPartition = blocksPerPartition;
    }

    public int getBlockCapacity() {
        return blockCapacity;
    }

    public void setBlockCapacity(int blockCapacity) {
        this.blockCapacity = blockCapacity;
    }
}
