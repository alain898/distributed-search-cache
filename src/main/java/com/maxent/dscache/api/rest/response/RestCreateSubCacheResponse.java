package com.maxent.dscache.api.rest.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by alain on 16/8/26.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RestCreateSubCacheResponse extends RestCommonResponse {
    private String name;
    private String entryClassName;
    private String subCacheId;
    private int partitionsPerSubCache;
    private int blockCapacity;
    private int blocksPerPartition;

    public RestCreateSubCacheResponse() {
    }

    public RestCreateSubCacheResponse(String name, String entryClassName, String subCacheId,
                                      int partitionsPerSubCache, int blockCapacity, int blocksPerPartition) {
        this.name = name;
        this.entryClassName = entryClassName;
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
}
