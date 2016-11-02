package com.alain898.dscache.cache;

/**
 * Created by alain on 16/8/25.
 */
public class CacheZnode {
    private String version;
    private String name;

    private String entryClassName;
    private int partitionsPerSubCache;
    private int blockCapacity;
    private int blocksPerPartition;

    private String cacheGroup;
    private String forwardCache;
    private long forwardThreshold;

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

    public String getCacheGroup() {
        return cacheGroup;
    }

    public void setCacheGroup(String cacheGroup) {
        this.cacheGroup = cacheGroup;
    }

    public String getForwardCache() {
        return forwardCache;
    }

    public void setForwardCache(String forwardCache) {
        this.forwardCache = forwardCache;
    }

    public long getForwardThreshold() {
        return forwardThreshold;
    }

    public void setForwardThreshold(long forwardThreshold) {
        this.forwardThreshold = forwardThreshold;
    }
}
