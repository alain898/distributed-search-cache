package com.maxent.dscache.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alain on 16/8/20.
 */
public class SubCacheFactory {
    private static final Logger logger = LoggerFactory.getLogger(SubCacheFactory.class);

    public static <E extends ICacheEntry> SubCache<E> newCache(String name,
                                                               String provider,
                                                               int partitions,
                                                               int blocks_per_partition,
                                                               int block_capacity) {
        Class clazz;
        try {
            clazz = ClassLoader.getSystemClassLoader().loadClass(provider);
        } catch (Exception e) {
            String errInfo = String.format("failed to load provider[%s]", provider);
            throw new RuntimeException(errInfo, e);
        }

        boolean isCacheEntry = ICacheEntry.class.isAssignableFrom(clazz);
        if (!isCacheEntry) {
            String errInfo = String.format("provide[%s] is not ICacheEntry", provider);
            throw new RuntimeException(errInfo);
        }

        return new SubCache<>(clazz, partitions, block_capacity, blocks_per_partition);
    }
}
