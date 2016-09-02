package com.maxent.dscache.api.rest.request;

/**
 * Created by alain on 16/8/26.
 */
public class RestCreateSubCacheRequest {

    private String name;
    private String entryClassName;
    private int totalPartitionNumber;
    private String subCacheId;
    private int partitionsPerSubCache;
    private int blockCapacity;
    private int blocksPerPartition;

    public RestCreateSubCacheRequest() {
    }

    public RestCreateSubCacheRequest(final String name, final String entryClassName,
                                     final int totalPartitionNumber, final String subCacheId,
                                     final int partitionsPerSubCache, final int blockCapacity,
                                     final int blocksPerPartition) {
        this.name = name;
        this.entryClassName = entryClassName;
        this.totalPartitionNumber = totalPartitionNumber;
        this.subCacheId = subCacheId;
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

    public String getSubCacheId() {
        return subCacheId;
    }

    public void setSubCacheId(String subCacheId) {
        this.subCacheId = subCacheId;
    }

    public int getTotalPartitionNumber() {
        return totalPartitionNumber;
    }

    public void setTotalPartitionNumber(int totalPartitionNumber) {
        this.totalPartitionNumber = totalPartitionNumber;
    }
}
