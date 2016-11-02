package com.alain898.dscache.cache;

/**
 * Created by alain on 16/8/18.
 */
public interface ICacheEntry {
    String key();

    double match(ICacheEntry entry);

    double threadshold();
}
