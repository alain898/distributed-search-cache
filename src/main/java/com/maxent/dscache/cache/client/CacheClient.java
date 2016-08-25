package com.maxent.dscache.cache.client;

import com.maxent.dscache.api.rest.request.RestCacheMatchRequest;
import com.maxent.dscache.api.rest.response.RestCacheMatchResponse;
import com.maxent.dscache.cache.CacheClusterManager;
import com.maxent.dscache.cache.Host;
import com.maxent.dscache.cache.ICacheEntry;
import com.maxent.dscache.cache.client.response.CacheSearchResponse;
import com.maxent.dscache.common.http.HttpClient;
import com.maxent.dscache.common.partitioner.IPartitioner;

import java.util.List;

/**
 * Created by alain on 16/8/20.
 */
public class CacheClient {

    private CacheClusterManager clusterCenter;

    public CacheClient(CacheClusterManager clusterCenter) {
        this.clusterCenter = clusterCenter;
    }

    public CacheSearchResponse search(String cacheGroupName, ICacheEntry entry) {
        String keys = entry.key();
        IPartitioner partitioner = clusterCenter.getPartitioner(cacheGroupName);
        int partitionId = partitioner.getPartition(keys);
        List<VirtualHost> virtualHosts = clusterCenter.getVirtualHosts(cacheGroupName);
        int virtualHostsNumber = virtualHosts.size();
        int virtualHostId = partitionId / virtualHostsNumber;
        VirtualHost virtualHost = virtualHosts.get(virtualHostId);
        Host physicalHost = virtualHost.getPhysicalHost();

        String url = String.format("http://%s:%d", physicalHost.getHost(), physicalHost.getPort());
        String path = "/cache/match";
        HttpClient httpClient = new HttpClient();
        RestCacheMatchResponse restCacheMatchResponse =
                httpClient.post(url, path, new RestCacheMatchRequest(), RestCacheMatchResponse.class);
        return new CacheSearchResponse(
                restCacheMatchResponse.getScores(),
                restCacheMatchResponse.getEntries());
    }
}
