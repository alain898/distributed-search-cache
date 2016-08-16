package com.alain898.collection;

/**
 * Created by alain on 16/8/16.
 */
public interface IPartition<E> {
    void add(E e);

    E get(long index);

    long getLastIndex();
}
