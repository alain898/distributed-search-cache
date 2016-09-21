package com.maxent.dscache.cache.client;

import com.maxent.dscache.cache.CacheClusterViewer;
import com.maxent.dscache.cache.CacheClusterViewerFactory;
import com.maxent.dscache.cache.TestCacheEntry;
import com.maxent.dscache.cache.client.response.CacheGroupDeleteResponse;
import com.maxent.dscache.cache.client.response.CacheGroupUpdateResponse;
import com.maxent.dscache.cache.client.response.CacheSaveResponse;
import com.maxent.dscache.cache.client.response.CacheSearchResponse;
import com.maxent.dscache.common.tools.JsonUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alain on 16/9/16.
 */
public class CacheGroupClientTest {
    @Before
    public void setUp() throws Exception {
        CacheClusterViewerFactory.configure();
    }

    @Test
    public void search() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
        TestCacheEntry testCacheEntry = new TestCacheEntry();
        testCacheEntry.setField1("field1");
        testCacheEntry.setField2("field2");
        CacheSearchResponse response = cacheGroupClient.search("cache_group_test1", testCacheEntry);
        System.out.println(JsonUtils.toJson(response));
    }

    @Test
    public void save() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
        TestCacheEntry testCacheEntry = new TestCacheEntry();
        testCacheEntry.setField1("field1");
        testCacheEntry.setField2("field2");
        CacheSaveResponse response = cacheGroupClient.save("cache_group_test1", testCacheEntry);
        System.out.println(JsonUtils.toJson(response));
    }

    @Test
    public void update() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
        CacheGroupUpdateResponse response = cacheGroupClient.update("cache_group_test1", 4);
        System.out.println(JsonUtils.toJson(response));
    }

    @Test
    public void create() throws Exception {

    }

    @Test
    public void delete() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
        CacheGroupDeleteResponse response = cacheGroupClient.delete("cache_group_test1");
        System.out.println(JsonUtils.toJson(response));
    }

}