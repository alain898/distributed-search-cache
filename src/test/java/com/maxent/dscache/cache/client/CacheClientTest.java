package com.maxent.dscache.cache.client;

import com.maxent.dscache.cache.CacheClusterViewer;
import com.maxent.dscache.cache.CacheClusterViewerFactory;
import com.maxent.dscache.cache.TestCacheEntry;
import com.maxent.dscache.cache.client.response.CacheSearchResponse;
import com.maxent.dscache.common.tools.JsonUtils;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

/**
 * Created by alain on 16/9/2.
 */
public class CacheClientTest {
    @Test
    public void search() throws Exception {
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