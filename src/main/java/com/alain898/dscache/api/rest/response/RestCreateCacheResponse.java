package com.alain898.dscache.api.rest.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by alain on 16/8/20.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RestCreateCacheResponse extends RestCommonResponse {
    String name;
    String entryClassName;
    int subCaches;
    int partitionsPerSubCache;
    int blockCapacity;
    int blocksPerPartition;

    public RestCreateCacheResponse() {
    }

    public RestCreateCacheResponse(String name, String entryClassName, int subCaches, int partitionsPerSubCache, int blockCapacity, int blocksPerPartition) {
        this.name = name;
        this.entryClassName = entryClassName;
        this.subCaches = subCaches;
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
