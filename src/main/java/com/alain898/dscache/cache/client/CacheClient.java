package com.alain898.dscache.cache.client;

import com.alain898.dscache.api.rest.request.RestDeleteCacheRequest;
import com.alain898.dscache.api.rest.request.RestSubCacheSearchRequest;
import com.alain898.dscache.api.rest.response.RestSubcacheSaveResponse;
import com.alain898.dscache.cache.*;
import com.google.common.base.Preconditions;
import com.alain898.dscache.api.rest.request.RestCreateCacheRequest;
import com.alain898.dscache.api.rest.request.RestSubcacheSaveRequest;
import com.alain898.dscache.api.rest.response.RestCreateCacheResponse;
import com.alain898.dscache.api.rest.response.RestDeleteCacheResponse;
import com.alain898.dscache.api.rest.response.RestSubCacheSearchResponse;
import com.alain898.dscache.cache.client.response.CacheCreateResponse;
import com.alain898.dscache.cache.client.response.CacheDeleteResponse;
import com.alain898.dscache.cache.client.response.CacheSaveResponse;
import com.alain898.dscache.cache.client.response.CacheSearchResponse;
import com.alain898.dscache.common.http.HttpClient;
import com.alain898.dscache.common.tools.JsonUtils;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by alain on 16/8/20.
 */
public class CacheClient {

    private CacheClusterViewer cacheClusterViewer;

    public CacheClient(CacheClusterViewer cacheClusterViewer) {
        this.cacheClusterViewer = cacheClusterViewer;
    }

    public CacheSearchResponse search(String cacheName, ICacheEntry entry) {
        Preconditions.checkArgument(StringUtils.isNotBlank(cacheName), "cacheName is blank");
        Preconditions.checkNotNull(entry, "entry is null");

        String keys = entry.key();
        CacheMeta cache = cacheClusterViewer.getCache(cacheName);
        int partition = cache.getPartitioner().getPartition(keys);
        int subCacheId = partition / cache.getPartitionsPerSubCache();

        SubCacheMeta subCacheMeta = cache.getSubCacheMetas().get(subCacheId);
        // TODO: choose replication
        Host host = subCacheMeta.getReplicationMetas().get(0).getHost();

        String url = String.format("http://%s:%d", host.getHost(), host.getPort());
        String path = "/subcache/search";
        HttpClient httpClient = new HttpClient();
        RestSubCacheSearchRequest restCacheSearchRequest = new RestSubCacheSearchRequest();
        restCacheSearchRequest.setCacheName(cacheName);
        restCacheSearchRequest.setSubCacheId(String.valueOf(subCacheId));
        restCacheSearchRequest.setQueryEntry(JsonUtils.toMap(entry));
        restCacheSearchRequest.setSearchMode(SearchMode.MATCH_GROUP.toString());
        restCacheSearchRequest.setSearchPolicy(SearchPolicy.MATCH_BEST.toString());
        RestSubCacheSearchResponse restCacheSearchResponse =
                httpClient.post(url, path, restCacheSearchRequest, RestSubCacheSearchResponse.class);
        return new CacheSearchResponse(
                restCacheSearchResponse.getScores(),
                restCacheSearchResponse.getEntries());
    }

    public CacheSaveResponse save(String cacheName, ICacheEntry entry) {
        Preconditions.checkArgument(StringUtils.isNotBlank(cacheName), "cacheName is blank");
        Preconditions.checkNotNull(entry, "entry is null");

        String keys = entry.key();
        CacheMeta cache = cacheClusterViewer.getCache(cacheName);
        int partition = cache.getPartitioner().getPartition(keys);
        int subCacheId = partition / cache.getPartitionsPerSubCache();

        SubCacheMeta subCacheMeta = cache.getSubCacheMetas().get(subCacheId);
        Host host = subCacheMeta.getReplicationMetas().get(0).getHost();

        String url = String.format("http://%s:%d", host.getHost(), host.getPort());
        String path = "/subcache/save";
        HttpClient httpClient = new HttpClient();
        RestSubcacheSaveRequest restSubcacheSaveRequest = new RestSubcacheSaveRequest();
        restSubcacheSaveRequest.setCacheName(cacheName);
        restSubcacheSaveRequest.setSubCacheId(String.valueOf(subCacheId));
        restSubcacheSaveRequest.setQueryEntry(JsonUtils.toMap(entry));
        RestSubcacheSaveResponse restSubcacheSaveResponse =
                httpClient.post(url, path, restSubcacheSaveRequest, RestSubcacheSaveResponse.class);
        return new CacheSaveResponse(restSubcacheSaveResponse.getMessage());
    }


    public CacheCreateResponse create(String cacheName, String entryClassName,
                                      int subCaches, int partitionsPerSubCache,
                                      int blockCapacity, int blocksPerPartition) throws Exception {
        Preconditions.checkArgument(StringUtils.isNotBlank(cacheName), "cacheName is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(entryClassName), "entryClassName is blank");
        Preconditions.checkArgument(Validator.isValidSubCachesNumber(subCaches), "subCachesPerCache must be positive");
        Preconditions.checkArgument(Validator.isValidPartitions(partitionsPerSubCache), "partitionsPerSubCache must be positive");
        Preconditions.checkArgument(Validator.isValidBlockCapacity(blockCapacity), "blockCapacity must be positive");
        Preconditions.checkArgument(Validator.isValidBlocks(blocksPerPartition), "blocksPerPartition must be positive");


        Host host = cacheClusterViewer.getHosts().get(0);

        String url = String.format("http://%s:%d", host.getHost(), host.getPort());
        String path = "/management/cache/create";
        HttpClient httpClient = new HttpClient();
        RestCreateCacheRequest request = new RestCreateCacheRequest();
        request.setName(cacheName);
        request.setEntryClassName(entryClassName);
        request.setSubCaches(subCaches);
        request.setPartitionsPerSubCache(partitionsPerSubCache);
        request.setBlockCapacity(blockCapacity);
        request.setBlocksPerPartition(blocksPerPartition);

        RestCreateCacheResponse response =
                httpClient.post(url, path, request, RestCreateCacheResponse.class);
        return new CacheCreateResponse(response.getName(),
                response.getEntryClassName(),
                response.getSubCaches(),
                response.getPartitionsPerSubCache(),
                response.getBlockCapacity(),
                response.getBlocksPerPartition());
    }

    public CacheDeleteResponse delete(String cacheName) throws Exception {
        Preconditions.checkArgument(StringUtils.isNotBlank(cacheName), "cacheName is blank");

        Host host = cacheClusterViewer.getHosts().get(0);

        String url = String.format("http://%s:%d", host.getHost(), host.getPort());
        String path = "/management/cache/delete";
        HttpClient httpClient = new HttpClient();
        RestDeleteCacheRequest request = new RestDeleteCacheRequest();
        request.setCacheName(cacheName);

        RestDeleteCacheResponse response =
                httpClient.post(url, path, request, RestDeleteCacheResponse.class);
        return new CacheDeleteResponse(response.getMessage());
    }

    public static void main(String[] args) throws Exception {
        CacheClusterViewerFactory.configure(ConfigFactory.load());
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheClient cacheClient = new CacheClient(cacheClusterViewer);
        TestCacheEntry testCacheEntry = new TestCacheEntry();
        testCacheEntry.setField1("field1");
        testCacheEntry.setField2("field2");
        CacheSearchResponse response = cacheClient.search("cache-test1", testCacheEntry);
        System.out.println(JsonUtils.toJson(response));
    }
}
