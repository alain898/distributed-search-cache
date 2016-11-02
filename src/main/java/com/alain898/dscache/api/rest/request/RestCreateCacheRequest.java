package com.alain898.dscache.api.rest.request;

/**
 * Created by alain on 16/8/20.
 */
public class RestCreateCacheRequest {
    String name;
    String entryClassName;
    int subCaches;
    int partitionsPerSubCache;
    int blockCapacity;
    int blocksPerPartition;

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

    public int getSubCaches() {
        return subCaches;
    }

    public void setSubCaches(int subCaches) {
        this.subCaches = subCaches;
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
