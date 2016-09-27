package com.maxent.dscache.cache;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by alain on 16/9/27.
 */
public class Validator {
    private static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }

    public static boolean isValidCachesNumber(int cachesNumber) {
        return isPowerOfTwo(cachesNumber);
    }

    public static boolean isValidSubCachesNumber(int subcachesNumber) {
        return subcachesNumber > 0;
    }

    public static boolean isValidPartitions(int partitions) {
        return partitions > 0;
    }

    public static boolean isValidBlockCapacity(int blockCapacity) {
        return blockCapacity > 0;
    }

    public static boolean isValidBlocks(int blocks) {
        return blocks > 0;
    }

    public static boolean isValidCacheGroup(String cacheGroup) {
        return StringUtils.isNotBlank(cacheGroup);
    }

    public static boolean isValidForwardThreshold(long forwardThreshold) {
        return forwardThreshold >= 0 && forwardThreshold <= 100;
    }

}
