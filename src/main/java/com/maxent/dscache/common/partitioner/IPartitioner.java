package com.maxent.dscache.common.partitioner;

/**
 * Created by alain on 16/8/18.
 */
public interface IPartitioner {
    int getPartition(String key);
}
