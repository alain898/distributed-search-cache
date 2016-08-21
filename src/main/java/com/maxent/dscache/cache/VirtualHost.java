package com.maxent.dscache.cache;

import com.maxent.dscache.cache.client.CacheGroup;

/**
 * Created by alain on 16/8/20.
 */
public class VirtualHost {
    /**
     * 一个 CacheGroup 是一个分布式搜索缓存实例,
     * 一个 CacheGroup 会包含多个VirtualHost, 每个VirtualHost上会包含且仅仅包含一个(同时也是一种)Cache,
     * VirtualHost 是 CacheGroup 下面的概念,
     * 不同 CacheGroup 里面的 VirtualHost 之间没有任何关系,
     * PhysicalHost 则是集群中各个物理主机的实体, 是全局范围内一致的。
     * VirtualHost 和 Cache 是一对一的对应关系,
     * 一个 Cache 拥有多个 Partition ,
     * 同种类型的所有 Cache 拥有的 Partition 数量相同,
     * 且初始化指定后, Partition 数量不能再改变, 但 blocks_per_partiton可以改变
     * VirtualHost 的引入是为了解决 partition 数量变更的问题
     * <p>
     * 当根据一个key值,查询对应的partition时,经过如下步骤:
     * VirtualHostId = getPartition(key) / partitions_per_VirtualHost
     * PhiscalHostId = VirtualHostId -> PhiscalHostId
     * 将请求发送到对应的PhiscalHost后,
     * CacheId = VirtualHostId -> CacheId (因为VirtualHost 和 Cache 是一对一的对应关系)
     * Cache = caches.get(CacheId)
     * PartitionId =  getPartition(key) % partitions_per_VirtualHost
     * partition = cache.get(PartitionId)
     */

    public CacheGroup cacheGroup;
    public PhysicalHost physicalHost;

    public CacheGroup getCacheGroup() {
        return cacheGroup;
    }

    public void setCacheGroup(CacheGroup cacheGroup) {
        this.cacheGroup = cacheGroup;
    }

    public PhysicalHost getPhysicalHost() {
        return physicalHost;
    }

    public void setPhysicalHost(PhysicalHost physicalHost) {
        this.physicalHost = physicalHost;
    }
}
