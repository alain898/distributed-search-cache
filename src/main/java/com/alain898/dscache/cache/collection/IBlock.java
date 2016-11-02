package com.alain898.dscache.cache.collection;

/**
 * Created by alain on 16/8/16.
 */
public interface IBlock<E> extends Iterable<E> {
    int size();

    boolean isEmpty();

    E get(int index);

    boolean add(E e);

    E set(int index, E element);

    void clear();
}
