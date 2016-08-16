package com.alain898.collection;

/**
 * Created by alain on 16/8/16.
 */
public interface IPartition<T> {
    void add(T t);

    T get(int index);

    IBlock getBlock(int blockIndex);

    int getLastIndex();
}
