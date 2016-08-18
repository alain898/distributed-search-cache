package com.maxent.dscache.collection;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by alain on 16/8/16.
 */
public class ArrayListBlock<E> implements IBlock<E> {
    private static final int DEFAULT_INITIAL_CAPACITY = 32;

    private final List<E> elems;

    public ArrayListBlock() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ArrayListBlock(int initialCapacity) {
        Preconditions.checkArgument(initialCapacity >= 0, "negative initialCapacity");

        this.elems = new ArrayList<>(initialCapacity);
    }

    public Iterator iterator() {
        return elems.iterator();
    }

    public int size() {
        return elems.size();
    }

    public boolean isEmpty() {
        return false;
    }

    public E get(int index) {
        return elems.get(index);
    }

    public boolean add(E e) {
        return elems.add(e);
    }

    public E set(int index, E element) {
        return elems.set(index, element);
    }

    public void clear() {
        elems.clear();
    }
}
