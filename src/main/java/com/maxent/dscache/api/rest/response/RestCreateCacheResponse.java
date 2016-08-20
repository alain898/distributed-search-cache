package com.maxent.dscache.api.rest.response;

/**
 * Created by alain on 16/8/20.
 */
public class RestCreateCacheResponse extends RestCommonResponse{
    private String name;
    private String provider;

    private int partitions;
    private int blocks_per_partition;
    private int block_capacity;

    public RestCreateCacheResponse(String name, String provider, int partitions, int blocks_per_partition, int block_capacity) {
        this.name = name;
        this.provider = provider;
        this.partitions = partitions;
        this.blocks_per_partition = blocks_per_partition;
        this.block_capacity = block_capacity;
    }
}
