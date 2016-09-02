package com.maxent.dscache.cache.client;

import com.maxent.dscache.cache.CacheClusterService;
import com.maxent.dscache.cache.TestCacheEntry;
import com.maxent.dscache.cache.client.response.CacheSearchResponse;
import com.maxent.dscache.common.tools.JsonUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alain on 16/9/2.
 */
public class CacheClientTest {
    @Test
    public void search() throws Exception {
        CacheClusterService clusterService = new CacheClusterService();
        CacheClient cacheClient = new CacheClient(clusterService);
        TestCacheEntry testCacheEntry = new TestCacheEntry();
        testCacheEntry.setField1("field1");
        testCacheEntry.setField2("field2");
        CacheSearchResponse response = cacheClient.search("cache-test1", testCacheEntry);
        System.out.println(JsonUtils.toJson(response));
    }

}