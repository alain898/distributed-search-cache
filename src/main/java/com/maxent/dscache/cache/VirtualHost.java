package com.maxent.dscache.cache;

/**
 * Created by alain on 16/8/20.
 */
public class VirtualHost {
    /**
     * VirtualHost 和 Cache 是一对一的对应关系,
     * 一个 Cache 拥有多个 Partition ,
     * 同种类型的所有 Cache 拥有的 Partition 数量相同,
     * 且初始化指定后, Partition 数量不能再改变, 但 blocks_per_partiton可以改变
     * VirtualHost 的引入是为了解决 partition 数量变更的问题
     *
     * 当根据一个key值,查询对应的partition时,经过如下步骤:
     * VirtualHostId = getPartition(key) / partitions_per_VirtualHost
     * PhiscalHostId = VirtualHostId -> PhiscalHostId
     * 将请求发送到对应的PhiscalHost后,
     * CacheId = VirtualHostId -> CacheId (因为VirtualHost 和 Cache 是一对一的对应关系)
     * Cache = caches.get(CacheId)
     * PartitionId =  getPartition(key) % partitions_per_VirtualHost
     * partition = cache.get(PartitionId)
     */
}
