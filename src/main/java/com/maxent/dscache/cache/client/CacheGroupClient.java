package com.maxent.dscache.cache.client;

import com.maxent.dscache.cache.CacheClusterService;
import com.maxent.dscache.cache.CacheGroupMeta;
import com.maxent.dscache.cache.CacheMeta;
import com.maxent.dscache.cache.ICacheEntry;
import com.maxent.dscache.cache.client.response.CacheSearchResponse;
import com.maxent.dscache.common.partitioner.HashPartitioner;
import com.maxent.dscache.common.partitioner.IPartitioner;

import java.util.List;

/**
 * Created by alain on 16/9/5.
 */
public class CacheGroupClient {
    private CacheClusterService clusterCenter;
    private CacheClient cacheClient;

    public CacheGroupClient(CacheClusterService clusterCenter, CacheClient cacheClient) {
        this.clusterCenter = clusterCenter;
        this.cacheClient = cacheClient;
    }

    public CacheSearchResponse search(String cacheGroupName, ICacheEntry entry) {
        CacheGroupMeta cacheGroupMeta = clusterCenter.getCacheGroupMeta(cacheGroupName);
        String key = entry.key();
        IPartitioner partitioner = new HashPartitioner(cacheGroupMeta.getCacheGroupCapacity());
        int partition = partitioner.getPartition(key);
        int cacheIndex = partition % cacheGroupMeta.getCurrentCachesNumber();
        List<CacheMeta> cacheMetaList = cacheGroupMeta.getCacheMetas();
        CacheMeta cacheMeta = cacheMetaList.get(cacheIndex);
        return cacheClient.search(cacheMeta.getName(), entry);
    }
}
