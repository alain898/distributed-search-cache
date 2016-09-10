package com.maxent.dscache.cache.client;

import com.maxent.dscache.api.rest.request.RestCreateCacheGroupRequest;
import com.maxent.dscache.api.rest.request.RestCreateCacheRequest;
import com.maxent.dscache.api.rest.request.RestDeleteCacheGroupRequest;
import com.maxent.dscache.api.rest.request.RestDeleteCacheRequest;
import com.maxent.dscache.api.rest.response.RestCreateCacheGroupResponse;
import com.maxent.dscache.api.rest.response.RestCreateCacheResponse;
import com.maxent.dscache.api.rest.response.RestDeleteCacheGroupResponse;
import com.maxent.dscache.api.rest.response.RestDeleteCacheResponse;
import com.maxent.dscache.cache.*;
import com.maxent.dscache.cache.client.response.*;
import com.maxent.dscache.common.http.HttpClient;
import com.maxent.dscache.common.partitioner.HashPartitioner;
import com.maxent.dscache.common.partitioner.IPartitioner;

import java.util.List;

/**
 * Created by alain on 16/9/5.
 */
public class CacheGroupClient {
    private CacheClusterService clusterCenter;
    private CacheClient cacheClient;

    public CacheGroupClient(CacheClusterService clusterCenter) {
        this.clusterCenter = clusterCenter;
        this.cacheClient = new CacheClient(clusterCenter);
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

    public CreateCacheGroupResponse create(String cacheGroupName,
                                           String entryClassName,
                                           int cachesNumber,
                                           int subCachesPerCache,
                                           int partitionsPerSubCache,
                                           int blockCapacity,
                                           int blocksPerPartition) {
        Host host = clusterCenter.getHosts().get(0);

        String url = String.format("http://%s:%d", host.getHost(), host.getPort());
        String path = "/management/cache_group/create";
        HttpClient httpClient = new HttpClient();
        RestCreateCacheGroupRequest request = new RestCreateCacheGroupRequest();
        request.setCacheGroupName(cacheGroupName);
        request.setEntryClassName(entryClassName);
        request.setCachesNumber(cachesNumber);
        request.setSubCachesPerCache(subCachesPerCache);
        request.setPartitionsPerSubCache(partitionsPerSubCache);
        request.setBlockCapacity(blockCapacity);
        request.setBlocksPerPartition(blocksPerPartition);

        RestCreateCacheGroupResponse response =
                httpClient.post(url, path, request, RestCreateCacheGroupResponse.class);
        return new CreateCacheGroupResponse(response.getMessage());
    }

    public CacheGroupDeleteResponse delete(String cacheGroupName) throws Exception {
        Host host = clusterCenter.getHosts().get(0);

        String url = String.format("http://%s:%d", host.getHost(), host.getPort());
        String path = "/management/cache_group/delete";
        HttpClient httpClient = new HttpClient();
        RestDeleteCacheGroupRequest request = new RestDeleteCacheGroupRequest();
        request.setCacheGroupName(cacheGroupName);

        RestDeleteCacheGroupResponse response =
                httpClient.post(url, path, request, RestDeleteCacheGroupResponse.class);
        return new CacheGroupDeleteResponse(response.getMessage());
    }
}
