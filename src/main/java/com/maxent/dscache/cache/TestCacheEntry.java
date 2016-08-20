package com.maxent.dscache.cache;

/**
 * Created by alain on 16/8/20.
 */
public class TestCacheEntry implements ICacheEntry {
    @Override
    public String key() {
        return "";
    }

    @Override
    public double match(ICacheEntry entry) {
        return 1;
    }

    @Override
    public double threadshold() {
        return 0.5;
    }
}
