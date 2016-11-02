package com.alain898.dscache.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alain on 16/8/20.
 */
public class SubCacheFactory {
    private static final Logger logger = LoggerFactory.getLogger(SubCacheFactory.class);

    public static <E extends ICacheEntry> SubCache<E> newCache(String cacheName,
                                                               String entryClassName,
                                                               int totalPartitionNumber,
                                                               String subCacheId,
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

        return new SubCache<>(cacheName, clazz, totalPartitionNumber, subCacheId,
                partitions, block_capacity, blocks_per_partition);
    }
}
