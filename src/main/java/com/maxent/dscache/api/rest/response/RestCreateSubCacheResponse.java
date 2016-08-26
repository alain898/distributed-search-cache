package com.maxent.dscache.api.rest.response;

/**
 * Created by alain on 16/8/26.
 */
public class RestCreateSubCacheResponse extends RestCommonResponse {
    private String name;
    private String entryClassName;
    private int partitionsPerSubCache;
    private int blockCapacity;
    private int blocksPerPartition;

    public RestCreateSubCacheResponse() {
    }

    public RestCreateSubCacheResponse(String name, String entryClassName, int partitionsPerSubCache, int blockCapacity, int blocksPerPartition) {
        this.name = name;
        this.entryClassName = entryClassName;
        this.partitionsPerSubCache = partitionsPerSubCache;
        this.blockCapacity = blockCapacity;
        this.blocksPerPartition = blocksPerPartition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntryClassName() {
        return entryClassName;
    }

    public void setEntryClassName(String entryClassName) {
        this.entryClassName = entryClassName;
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
