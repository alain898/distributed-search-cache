package com.alain898.dscache.cache;

import com.alain898.dscache.cache.exceptions.*;
import com.alain898.dscache.common.tools.JsonUtils;
import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alain on 16/8/20.
 */
public class SubCacheService {
    private static final Logger logger = LoggerFactory.getLogger(SubCacheService.class);

    private final Object lock = new Object();
    private Map<String, Map<String, SubCache<ICacheEntry>>> caches = new ConcurrentHashMap<>();
    private final CacheClusterViewer cacheClusterViewer;

    private final Host host;

    private static volatile SubCacheService subCacheService = null;

    public static void configure(Config config) {
        synchronized (SubCacheService.class) {
            subCacheService = new SubCacheService(config);
        }
    }

    public static SubCacheService getInstance() {
        if (subCacheService == null) {
            throw new RuntimeException(
                    "SubCacheService not configured, please configure before using it.");
        }
        return subCacheService;
    }

    private SubCacheService(Config config) {
        try {
            if (config == null) {
                config = ConfigFactory.load();
            }

            String ip = config.getString("server.ip");
            int port = config.getInt("server.port");
            this.host = new Host(ip, port);

            cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
            restoreCaches();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * restore caches before service start, so no need lock cache cluster
     */
    private void restoreCaches() throws Exception {
        CacheClusterMeta cacheClusterMeta = cacheClusterViewer.getCacheClusterMeta();
        List<Host> hosts = cacheClusterMeta.getHosts();
        if (hosts == null) {
            return;
        }
        for (Host h : hosts) {
            if (host.equals(h)) {
                host.setId(h.getId());
            }
        }
        if (host.getId() == Host.INVALID_ID) {
            return;
        }

        List<CacheMeta> cacheMetas = cacheClusterMeta.getCaches();
        for (CacheMeta cacheMeta : cacheMetas) {
            List<SubCacheMeta> subCacheMetas = cacheMeta.getSubCacheMetas();
            int totalPartitionNumber = subCacheMetas.size() * cacheMeta.getPartitionsPerSubCache();
            for (SubCacheMeta subCacheMeta : subCacheMetas) {
                Host h = subCacheMeta.getReplicationMetas().get(0).getHost();
                if (host.equals(h)) {
                    String cacheName = cacheMeta.getName();
                    String entryClassName = cacheMeta.getEntryClassName();
                    String subCacheId = String.valueOf(subCacheMeta.getId());
                    int partitions = cacheMeta.getPartitionsPerSubCache();
                    int blocksPerPartition = cacheMeta.getBlocksPerPartition();
                    int blockCapacity = cacheMeta.getBlockCapacity();
                    createSubCache(cacheName, entryClassName, totalPartitionNumber, subCacheId,
                            partitions, blocksPerPartition, blockCapacity);
                    logger.info(String.format(
                            "successfully create cacheName[%s], entryClassName[%s], subCacheId[%s], " +
                                    "partitions[%d], blocksPerPartition[%d], blockCapacity[%d]",
                            cacheName, entryClassName, subCacheId,
                            partitions, blocksPerPartition, blockCapacity));
                }
            }
        }

        for (Map.Entry<String, Map<String, SubCache<ICacheEntry>>> cache : caches.entrySet()) {
            Map<String, SubCache<ICacheEntry>> subCacheMap = cache.getValue();
            for (Map.Entry<String, SubCache<ICacheEntry>> subCacheEntry : subCacheMap.entrySet()) {
                SubCache<ICacheEntry> subCache = subCacheEntry.getValue();
                subCache.warmUp();
            }
        }
    }

    public void createSubCache(final String cacheName,
                               final String entryClassName,
                               final int totalPartitionNumber,
                               final String subCacheId,
                               final int partitions,
                               final int blocks_per_partition,
                               final int block_capacity)
            throws CacheExistException, CacheCreateFailure {

        Preconditions.checkArgument(StringUtils.isNotBlank(cacheName), "cacheName is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(entryClassName), "entryClassName is blank");
        Preconditions.checkArgument(totalPartitionNumber > 0, "totalPartitionNumber is not positive");
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
                        totalPartitionNumber,
                        subCacheId,
                        partitions,
                        blocks_per_partition,
                        block_capacity);
                caches.putIfAbsent(cacheName, new ConcurrentHashMap<>());
                caches.get(cacheName).put(subCacheId, subCache);
            } catch (Exception e) {
                throw new CacheCreateFailure(String.format(
                        "cacheName[%s] subCache[%s] create failed", cacheName, subCacheId), e);
            }
        }
    }


    public void deleteSubCache(final String cacheName,
                               final String subCacheId)
            throws CacheDeleteFailure {

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

    public void saveEntry(final String cacheName,
                          final String subCacheId,
                          final Map query)
            throws CacheSaveFailure {
        SubCache<ICacheEntry> subCache = getSubCache(cacheName, subCacheId);
        if (subCache == null) {
            throw new CacheSaveFailure(String.format(
                    "cannot find cache[%s], subCache[%s]", cacheName, subCacheId));
        }

        Class<ICacheEntry> cacheEntryClass = subCache.getCacheEntryClass();

        ICacheEntry queryEntry = JsonUtils.fromMap(query, cacheEntryClass);

        subCache.save(queryEntry);
    }

    private SubCache<ICacheEntry> getSubCache(String cacheName, String subCacheId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(cacheName), "cacheName is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(subCacheId), "subCacheId is blank");

        return caches.getOrDefault(cacheName, new ConcurrentHashMap<>()).get(subCacheId);
    }

    public List<Pair<ICacheEntry, Double>> search(String cacheName,
                                                  String subCacheId,
                                                  Map query)
            throws CacheSearchFailure {

        SubCache<ICacheEntry> subCache = getSubCache(cacheName, subCacheId);
        if (subCache == null) {
            throw new CacheSearchFailure(String.format(
                    "cannot find cache[%s], subCache[%s]", cacheName, subCacheId));
        }

        Class<ICacheEntry> cacheEntryClass = subCache.getCacheEntryClass();

        ICacheEntry queryEntry = JsonUtils.fromMap(query, cacheEntryClass);

        return subCache.search(queryEntry);

    }


    public List<Pair<ICacheEntry, Double>> search(String cacheName,
                                                  String subCacheId,
                                                  String searchMode,
                                                  String searchPolicy,
                                                  Map query)
            throws CacheSearchFailure {

        SubCache<ICacheEntry> subCache = getSubCache(cacheName, subCacheId);
        if (subCache == null) {
            throw new CacheSearchFailure(String.format(
                    "cannot find cache[%s], subCache[%s]", cacheName, subCacheId));
        }

        Class<ICacheEntry> cacheEntryClass = subCache.getCacheEntryClass();

        ICacheEntry queryEntry = JsonUtils.fromMap(query, cacheEntryClass);

        return subCache.search(queryEntry, SearchPolicy.valueOf(searchPolicy), SearchMode.valueOf(searchMode));
    }
}
