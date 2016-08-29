package com.maxent.dscache.cache;

import com.google.common.base.Preconditions;
import com.maxent.dscache.cache.exceptions.CacheCreateFailureException;
import com.maxent.dscache.cache.exceptions.CacheDeleteFailureException;
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
    private Map<String, Map<String, SubCache<ICacheEntry>>> caches = new ConcurrentHashMap<>();

    public void createSubCache(final String cacheName,
                               final String entryClassName,
                               final String subCacheId,
                               final int partitions,
                               final int blocks_per_partition,
                               final int block_capacity)
            throws CacheExistException, CacheCreateFailureException {

        Preconditions.checkArgument(StringUtils.isNotBlank(cacheName), "cacheName is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(entryClassName), "entryClassName is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(subCacheId), "subCacheId is blank");
        Preconditions.checkArgument(partitions > 0, "partitions is not positive");
        Preconditions.checkArgument(blocks_per_partition > 0, "blocks_per_partition is not positive");
        Preconditions.checkArgument(block_capacity > 0, "block_capacity is not positive");

        synchronized (lock) {
            if (caches.containsKey(cacheName) && caches.get(cacheName).containsKey(subCacheId)) {
                throw new CacheExistException(String.format("cache[%s] subCache[%s] exist", cacheName, subCacheId));
            }

            try {
                SubCache<ICacheEntry> subCache = SubCacheFactory.newCache(
                        cacheName,
                        entryClassName,
                        partitions,
                        blocks_per_partition,
                        block_capacity);
                caches.putIfAbsent(cacheName, new ConcurrentHashMap<>());
                caches.get(cacheName).put(subCacheId, subCache);
            } catch (Exception e) {
                throw new CacheCreateFailureException(String.format(
                        "cacheName[%s] subCache[%s] create failed", cacheName, subCacheId), e);
            }
        }
    }

    public void deleteSubCache(final String cacheName,
                               final String subCacheId)
            throws CacheDeleteFailureException {

        Preconditions.checkArgument(StringUtils.isNotBlank(cacheName), "cacheName is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(subCacheId), "subCacheId is blank");

        synchronized (lock) {
            Map<String, SubCache<ICacheEntry>> subCaches = caches.get(cacheName);
            if (subCaches != null) {
                SubCache<ICacheEntry> subCache = subCaches.remove(subCacheId);
                subCache.clear();
            }
            if (subCaches == null || subCaches.size() == 0) {
                caches.remove(cacheName);
            }
        }
    }

    private SubCache<ICacheEntry> getSubCache(String cacheName, String subCacheId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(cacheName), "cacheName is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(subCacheId), "subCacheId is blank");

        return caches.getOrDefault(cacheName, new ConcurrentHashMap<>()).get(subCacheId);
    }

    public List<Pair<ICacheEntry, Double>> match(String cacheName, String subCacheId, Map query)
            throws CacheMatchFailureException {

        SubCache<ICacheEntry> subCache = getSubCache(cacheName, subCacheId);
        if (subCache == null) {
            throw new CacheMatchFailureException(String.format(
                    "cannot find cache[%s], subCache[%s]", cacheName, subCacheId));
        }

        Class<ICacheEntry> cacheEntryClass = subCache.getCacheEntryClass();

        ICacheEntry queryEntry = JsonUtils.fromMap(query, cacheEntryClass);

        return subCache.match(queryEntry);

    }
}
