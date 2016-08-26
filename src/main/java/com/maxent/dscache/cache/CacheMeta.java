package com.maxent.dscache.cache;

import com.maxent.dscache.common.partitioner.IPartitioner;

import java.util.List;

/**
 * Created by alain on 16/8/20.
 */
public class CacheMeta {

    private String version;

    private String name;
    private IPartitioner partitioner;

    private String entryClassName;
    private Class<ICacheEntry> entryClass;

    private List<SubCacheMeta> subCacheMetas;
    private int blockCapacity;
    private int blocksPerPartition;
    private int partitionsPerSubCache;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IPartitioner getPartitioner() {
        return partitioner;
    }

    public void setPartitioner(IPartitioner partitioner) {
        this.partitioner = partitioner;
    }

    public String getEntryClassName() {
        return entryClassName;
    }

    public void setEntryClassName(String entryClassName) {
        this.entryClassName = entryClassName;
    }

    public Class<ICacheEntry> getEntryClass() {
        return entryClass;
    }

    public void setEntryClass(Class<ICacheEntry> entryClass) {
        this.entryClass = entryClass;
    }

    public List<SubCacheMeta> getSubCacheMetas() {
        return subCacheMetas;
    }

    public void setSubCacheMetas(List<SubCacheMeta> subCacheMetas) {
        this.subCacheMetas = subCacheMetas;
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
