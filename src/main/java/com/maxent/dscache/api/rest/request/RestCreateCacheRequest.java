package com.maxent.dscache.api.rest.request;

/**
 * Created by alain on 16/8/20.
 */
public class RestCreateCacheRequest {
    private String name;
    private String provider;

    private int partitions;
    private int blocks_per_partition;
    private int block_capacity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getPartitions() {
        return partitions;
    }

    public void setPartitions(int partitions) {
        this.partitions = partitions;
    }

    public int getBlocks_per_partition() {
        return blocks_per_partition;
    }

    public void setBlocks_per_partition(int blocks_per_partition) {
        this.blocks_per_partition = blocks_per_partition;
    }

    public int getBlock_capacity() {
        return block_capacity;
    }

    public void setBlock_capacity(int block_capacity) {
        this.block_capacity = block_capacity;
    }
}
