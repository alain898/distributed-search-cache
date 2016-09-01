package com.maxent.dscache.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alain on 16/8/20.
 */
public class SubCacheFactory {
    private static final Logger logger = LoggerFactory.getLogger(SubCacheFactory.class);

    public static <E extends ICacheEntry> SubCache<E> newCache(String cacheName,
                                                               String subCacheId,
                                                               String entryClassName,
                                                               int partitions,
                                                               int blocks_per_partition,
                                                               int block_capacity) {
        Class clazz;
        try {
            clazz = ClassLoader.getSystemClassLoader().loadClass(entryClassName);
        } catch (Exception e) {
            String errInfo = String.format("failed to load provider[%s]", entryClassName);
            throw new RuntimeException(errInfo, e);
        }

        boolean isCacheEntry = ICacheEntry.class.isAssignableFrom(clazz);
        if (!isCacheEntry) {
            String errInfo = String.format("provide[%s] is not ICacheEntry", entryClassName);
            throw new RuntimeException(errInfo);
        }

        return new SubCache<>(cacheName, subCacheId, clazz, partitions, block_capacity, blocks_per_partition);
    }
}
