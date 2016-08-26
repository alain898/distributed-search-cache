package com.maxent.dscache.cache;

import com.google.common.base.Preconditions;
import com.maxent.dscache.cache.exceptions.CacheExistException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alain on 16/8/20.
 */
public class SubCacheManager {
    private final Object lock = new Object();
    private Map<String, SubCache<ICacheEntry>> caches = new HashMap<>();

    public void addSubCache(String name,
                            String provider,
                            int partitions,
                            int blocks_per_partition,
                            int block_capacity) throws CacheExistException {
        Preconditions.checkArgument(StringUtils.isNotBlank(name), "name is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(provider), "provider is blank");
        Preconditions.checkArgument(partitions > 0, "provider is not positive");
        Preconditions.checkArgument(blocks_per_partition > 0, "blocks_per_partition is not positive");
        Preconditions.checkArgument(block_capacity > 0, "block_capacity is not positive");

        synchronized (lock) {
            if (caches.containsKey(name)) {
                throw new CacheExistException(String.format("subCache[%s] exist", name));
            }

            SubCache<ICacheEntry> subCache = SubCacheFactory.newCache(
                    name,
                    provider,
                    partitions,
                    blocks_per_partition,
                    block_capacity);

            caches.put(name, subCache);
        }
    }

    public SubCache<ICacheEntry> getSubCache(String name) {
        return caches.get(name);
    }

}
