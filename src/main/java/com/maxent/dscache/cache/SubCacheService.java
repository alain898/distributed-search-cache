package com.maxent.dscache.cache;

import com.google.common.base.Preconditions;
import com.maxent.dscache.cache.exceptions.CacheCreateFailureException;
import com.maxent.dscache.cache.exceptions.CacheExistException;
import com.maxent.dscache.cache.exceptions.CacheMatchFailureException;
import com.maxent.dscache.common.tools.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alain on 16/8/20.
 */
public class SubCacheService {
    private final Object lock = new Object();
    private Map<String, SubCache<ICacheEntry>> caches = new ConcurrentHashMap<>();

    public void createSubCache(final String name,
                               final String entryClassName,
                               final int partitions,
                               final int blocks_per_partition,
                               final int block_capacity)
            throws CacheExistException, CacheCreateFailureException {

        Preconditions.checkArgument(StringUtils.isNotBlank(name), "name is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(entryClassName), "entryClassName is blank");
        Preconditions.checkArgument(partitions > 0, "partitions is not positive");
        Preconditions.checkArgument(blocks_per_partition > 0, "blocks_per_partition is not positive");
        Preconditions.checkArgument(block_capacity > 0, "block_capacity is not positive");

        synchronized (lock) {
            if (caches.containsKey(name)) {
                throw new CacheExistException(String.format("subCache[%s] exist", name));
            }

            try {
                SubCache<ICacheEntry> subCache = SubCacheFactory.newCache(
                        name,
                        entryClassName,
                        partitions,
                        blocks_per_partition,
                        block_capacity);
                caches.put(name, subCache);
            } catch (Exception e) {
                throw new CacheCreateFailureException(String.format("subCache[%s] create failed", name), e);
            }
        }
    }

    public SubCache<ICacheEntry> getSubCache(String name) {
        Preconditions.checkArgument(StringUtils.isNotBlank(name), "name is blank");

        return caches.get(name);
    }

    public List<Pair<ICacheEntry, Double>> match(String cacheName, Map query) throws CacheMatchFailureException {

        SubCache<ICacheEntry> subCache = caches.get(cacheName);
        if (subCache == null) {
            throw new CacheMatchFailureException(String.format("cannot find subCache[%s]", cacheName));
        }

        Class<ICacheEntry> cacheEntryClass = subCache.getCacheEntryClass();

        ICacheEntry queryEntry = JsonUtils.fromMap(query, cacheEntryClass);

        return subCache.match(queryEntry);

    }
}
