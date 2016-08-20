package com.maxent.dscache.cache.collection;

import com.maxent.dscache.annotations.NotThreadSafe;
import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by alain on 16/8/16.
 */
@NotThreadSafe
public class ListPartition<E> implements IPartition<E> {

    private Map<Long, IBlock<E>> blocks = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final int blockCapacity;
    private final long totalBlockNumber;

    private long lastIndex = -1;
    private long currentBlockNumber = 0;

    public ListPartition(int blockCapacity, long totalBlockNumber) {
        Preconditions.checkArgument(blockCapacity > 0, "blockCapacity is not positive");
        Preconditions.checkArgument(totalBlockNumber > 0, "totalBlockNumber is not positive");

        this.blockCapacity = blockCapacity;
        this.totalBlockNumber = totalBlockNumber;
    }

    @Override
    public void add(E e) {
        lock.writeLock().lock();
        try {
            long addIndex = lastIndex + 1;
            long blockIndex = addIndex / blockCapacity;
            int indexWithinBlock = (int) (addIndex % blockCapacity);
            if (indexWithinBlock == 0) {
                blocks.put(blockIndex, new ArrayListBlock<>(blockCapacity));
                currentBlockNumber++;
            }
            if (currentBlockNumber > totalBlockNumber) {
                IBlock<E> block = blocks.remove(blockIndex - totalBlockNumber);
                if (block != null) {
                    block.clear();
                }
            }
            IBlock<E> block = blocks.get(blockIndex);
            block.add(e);
            lastIndex++;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public E get(long index) {
        lock.readLock().lock();
        try {
            if (index < 0 || index > lastIndex) {
                return null;
            }
            long blockIndex = index / blockCapacity;
            int indexWithinBlock = (int) (index % blockCapacity);
            IBlock<E> block = blocks.get(blockIndex);
            if (block == null) {
                return null;
            }
            return block.get(indexWithinBlock);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public long getLastIndex() {
        lock.readLock().lock();
        try {
            return lastIndex;
        } finally {
            lock.readLock().unlock();
        }
    }
}
