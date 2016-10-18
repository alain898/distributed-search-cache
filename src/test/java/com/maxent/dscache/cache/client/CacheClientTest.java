package com.maxent.dscache.cache.client;

import com.maxent.dscache.cache.CacheClusterViewer;
import com.maxent.dscache.cache.CacheClusterViewerFactory;
import com.maxent.dscache.cache.TestCacheEntry;
import com.maxent.dscache.cache.client.response.*;
import com.maxent.dscache.common.tools.JsonUtils;
import com.typesafe.config.ConfigFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Created by alain on 16/9/2.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CacheClientTest {

    @Before
    public void setUp() throws Exception {
        CacheClusterViewerFactory.configure(ConfigFactory.load());
    }

    @Test
    public void test001_create() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheClient cacheClient = new CacheClient(cacheClusterViewer);
        String name = "cache-test1";
        String entryClassName = "com.maxent.dscache.cache.TestCacheEntry";
        int subCaches = 4;
        int partitionsPerSubCache = 100;
        int blockCapacity = 10;
        int blocksPerPartition = 100;
        CacheCreateResponse response = cacheClient.create(name, entryClassName,
                subCaches, partitionsPerSubCache,
                blockCapacity, blocksPerPartition);
        String result = JsonUtils.toJson(response);
        System.out.println(result);
        TestCase.assertEquals("{\"cacheName\":\"cache-test1\",\"entryClassName\":\"com.maxent.dscache.cache.TestCacheEntry\",\"subCaches\":4,\"partitionsPerSubCache\":100,\"blockCapacity\":100,\"blocksPerPartition\":10}", result);
        Thread.sleep(2000);
    }

    @Test
    public void test002_save() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheClient cacheClient = new CacheClient(cacheClusterViewer);
        TestCacheEntry testCacheEntry = new TestCacheEntry();
        testCacheEntry.setField1("field1");
        testCacheEntry.setField2("field2");
        CacheSaveResponse response = cacheClient.save("cache-test1", testCacheEntry);
        String result = JsonUtils.toJson(response);
        System.out.println(result);
        TestCase.assertEquals("{\"message\":\"success\"}", result);
        Thread.sleep(2000);
    }

    @Test
    public void test003_search() throws Exception {
        CacheClusterViewerFactory.configure(ConfigFactory.load());
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheClient cacheClient = new CacheClient(cacheClusterViewer);
        TestCacheEntry testCacheEntry = new TestCacheEntry();
        testCacheEntry.setField1("field1");
        testCacheEntry.setField2("field2");
        CacheSearchResponse response = cacheClient.search("cache-test1", testCacheEntry);
        String result = JsonUtils.toJson(response);
        System.out.println(result);
        TestCase.assertEquals("{\"scores\":[1.0],\"entries\":[{\"field1\":\"field1\",\"field2\":\"field2\"}]}", result);
        Thread.sleep(2000);
    }

    @Test
    public void test004_delete() throws Exception {
        CacheClusterViewer cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        CacheClient cacheClient = new CacheClient(cacheClusterViewer);
        CacheDeleteResponse response = cacheClient.delete("cache-test1");
        String result = JsonUtils.toJson(response);
        System.out.println(result);
        TestCase.assertEquals("{\"message\":\"success\"}", result);
        Thread.sleep(2000);
    }

}