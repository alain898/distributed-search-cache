package com.maxent.dscache.api.rest.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by alain on 16/8/20.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RestCreateCacheResponse extends RestCommonResponse {
    private String name;
    private String provider;

    private Integer partitions;
    private Integer blocks_per_partition;
    private Integer block_capacity;

    public RestCreateCacheResponse() {
    }

    public RestCreateCacheResponse(String name, String provider, Integer partitions, Integer blocks_per_partition, Integer block_capacity) {
        this.name = name;
        this.provider = provider;
        this.partitions = partitions;
        this.blocks_per_partition = blocks_per_partition;
        this.block_capacity = block_capacity;
    }

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

    public Integer getPartitions() {
        return partitions;
    }

    public void setPartitions(Integer partitions) {
        this.partitions = partitions;
    }

    public Integer getBlocks_per_partition() {
        return blocks_per_partition;
    }

    public void setBlocks_per_partition(Integer blocks_per_partition) {
        this.blocks_per_partition = blocks_per_partition;
    }

    public Integer getBlock_capacity() {
        return block_capacity;
    }

    public void setBlock_capacity(Integer block_capacity) {
        this.block_capacity = block_capacity;
    }
}
