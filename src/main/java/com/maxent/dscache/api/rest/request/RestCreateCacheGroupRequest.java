package com.maxent.dscache.api.rest.request;

/**
 * Created by alain on 16/9/6.
 */
public class RestCreateCacheGroupRequest {
    private String cacheGroupName;
    private String entryClassName;
    private int cachesNumber;
    private int subCachesPerCache;
    private int partitionsPerSubCache;
    private int blockCapacity;
    private int blocksPerPartition;

    public String getCacheGroupName() {
        return cacheGroupName;
    }

    public void setCacheGroupName(String cacheGroupName) {
        this.cacheGroupName = cacheGroupName;
    }

    public String getEntryClassName() {
        return entryClassName;
    }

    public void setEntryClassName(String entryClassName) {
        this.entryClassName = entryClassName;
    }

    public int getCachesNumber() {
        return cachesNumber;
    }

    public void setCachesNumber(int cachesNumber) {
        this.cachesNumber = cachesNumber;
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

    public int getBlockCapacity() {
        return blockCapacity;
    }

    public void setBlockCapacity(int blockCapacity) {
        this.blockCapacity = blockCapacity;
    }

    public int getBlocksPerPartition() {
        return blocksPerPartition;
    }

    public void setBlocksPerPartition(int blocksPerPartition) {
        this.blocksPerPartition = blocksPerPartition;
    }
}
