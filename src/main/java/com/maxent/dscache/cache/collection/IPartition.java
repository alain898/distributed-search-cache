package com.maxent.dscache.cache.collection;

/**
 * Created by alain on 16/8/16.
 */
public interface IPartition<E> {
    void add(E e);

    E get(long index);

    long getLastIndex();

    void clear();

    long size();
}
