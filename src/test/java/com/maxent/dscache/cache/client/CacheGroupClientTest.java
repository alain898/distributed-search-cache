package com.maxent.dscache.cache.client;

import com.maxent.dscache.cache.CacheClusterViewer;
import com.maxent.dscache.cache.CacheClusterViewerFactory;
import com.maxent.dscache.cache.TestCacheEntry;
import com.maxent.dscache.cache.client.response.*;
import com.maxent.dscache.common.tools.JsonUtils;
import com.typesafe.config.ConfigFactory;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

/**
 * Created by alain on 16/9/16.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CacheGroupClientTest {
    @Before
    public void setUp() throws Exception {
        CacheClusterViewerFactory.configure(ConfigFactory.load());
    }

    @Test
    public void test001_create() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
        String cacheGroupName = "cache_group_test1";
        String entryClassName = "com.maxent.dscache.cache.TestCacheEntry";
        int cacheGroupCapacity = 256;
        int cachesNumber = 4;
        int subCachesPerCache = 2;
        int partitionsPerSubCache = 16;
        int blockCapacity = 100;
        int blocksPerPartition = 10;
        CreateCacheGroupResponse response = cacheGroupClient.create(
                cacheGroupName, entryClassName, cacheGroupCapacity,
                cachesNumber, subCachesPerCache, partitionsPerSubCache,
                blockCapacity, blocksPerPartition);
        System.out.println(JsonUtils.toJson(response));
    }

    @Test
    public void test002_search() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
        TestCacheEntry testCacheEntry = new TestCacheEntry();
        testCacheEntry.setField1("field1");
        testCacheEntry.setField2("field2");
        CacheSearchResponse response = cacheGroupClient.search("cache_group_test1", testCacheEntry);
        System.out.println(JsonUtils.toJson(response));
    }

    @Test
    public void test003_save() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
        TestCacheEntry testCacheEntry = new TestCacheEntry();
        testCacheEntry.setField1("field1");
        testCacheEntry.setField2("field2");
        CacheSaveResponse response = cacheGroupClient.save("cache_group_test1", testCacheEntry);
        System.out.println(JsonUtils.toJson(response));
    }

    @Test
    public void test004_update() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
        CacheGroupUpdateResponse response = cacheGroupClient.update("cache_group_test1", 4);
        System.out.println(JsonUtils.toJson(response));
    }


    @Test
    public void test005_delete() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheGroupClient cacheGroupClient = new CacheGroupClient(cacheClusterViewer);
        CacheGroupDeleteResponse response = cacheGroupClient.delete("cache_group_test1");
        System.out.println(JsonUtils.toJson(response));
    }

}