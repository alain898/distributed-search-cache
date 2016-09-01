package com.maxent.dscache.cache;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.maxent.dscache.cache.collection.IPartition;
import com.maxent.dscache.cache.collection.ListPartition;
import com.maxent.dscache.common.partitioner.HashPartitioner;
import com.maxent.dscache.common.partitioner.IPartitioner;
import com.maxent.dscache.common.persist.Flusher;
import com.maxent.dscache.common.persist.PersistUtils;
import com.maxent.dscache.common.tools.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by alain on 16/8/18.
 */
public class SubCache<E extends ICacheEntry> {
    private static final Logger logger = LoggerFactory.getLogger(SubCache.class);

    private static final String DEFAULT_PERSIST_DIR = "/services/data/dscache";

    private final String cacheName;
    private final String subCacheId;
    private final Class<E> cacheEntryClass;
    private final int partitionNumber;
    private final IPartitioner partitioner;
    private final List<IPartition<E>> partitions;

    private final BlockingQueue<E> persistQueue = new ArrayBlockingQueue<>(1024);

    private final String dataFile;
    private final Flusher flusher;

    private volatile boolean shutdown = false;

    public SubCache(String cacheName, String subCacheId, Class<E> cacheEntryClass,
                    int partitionNumber, int blockCapacity, long blockNumber) {
        Preconditions.checkArgument(StringUtils.isNotBlank(cacheName), "cacheName is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(subCacheId), "subCacheId is blank");
        Preconditions.checkNotNull(cacheEntryClass, "cacheEntryClass is null");
        Preconditions.checkArgument(partitionNumber > 0, "partitionNumber must be positive");
        Preconditions.checkArgument(blockCapacity > 0, "blockCapacity must be positive");
        Preconditions.checkArgument(blockNumber > 0, "blockNumber must be positive");

        this.cacheName = cacheName;
        this.subCacheId = subCacheId;
        this.cacheEntryClass = cacheEntryClass;
        this.partitionNumber = partitionNumber;
        this.partitioner = new HashPartitioner(partitionNumber);
        this.partitions = createPartitions(partitionNumber, blockCapacity, blockNumber);

        this.dataFile = String.format("%s_%s", cacheName, subCacheId);
        this.flusher = PersistUtils.createFlusher(dataFile, DEFAULT_PERSIST_DIR, dataFile);

        logger.info(String.format("cacheName[%s], subCacheId[%s], cacheEntryClass[%s], " +
                        "partitionNumber[%d], blockCapacity[%d], blockNumber[%d]",
                cacheName, subCacheId, cacheEntryClass,
                partitionNumber, blockCapacity, blockNumber));
    }

    private List<IPartition<E>> createPartitions(int partitionNumber, int blockCapacity, long totalBlockNumber) {
        List<IPartition<E>> partitions = new ArrayList<>(partitionNumber);
        for (int i = 0; i < partitionNumber; i++) {
            partitions.add(new ListPartition<>(blockCapacity, totalBlockNumber));
        }
        return partitions;
    }


    public Class<E> getCacheEntryClass() {
        return cacheEntryClass;
    }

    public List<Pair<E, Double>> match(E query) {
        if (query == null) {
            return null;
        }

        int partitionId = partitioner.getPartition(query.key());
        IPartition<E> partition = partitions.get(partitionId);

        double maxScore = query.threadshold();
        E maxScoreEntry = null;
        long lastIndex = partition.getLastIndex();
        for (; lastIndex >= 0; lastIndex--) {
            E entry = partition.get(lastIndex);
            if (entry == null) {
                break;
            }
            if (!isEqualKey(entry.key(), query.key())) {
                continue;
            }
            //TODO: Add Match Return Policy
            double score = query.match(entry);
            if (score > maxScore) {
                maxScore = score;
                maxScoreEntry = entry;
            }
        }
        if (maxScoreEntry == null) {
            return null;
        }
        return Lists.newArrayList(Pair.of(maxScoreEntry, maxScore));
    }

    public void save(E entry) {
        int partition = partitioner.getPartition(entry.key());
        partitions.get(partition).add(entry);
        persist(entry);
    }

    private void persist(E entry) {
        if (entry != null) {
            persistQueue.offer(entry);
        }
    }

    private class PersistWorker implements Runnable {
        @Override
        public void run() {
            int bufferCapacity = 1024;
            List<E> buffer = new ArrayList<>(bufferCapacity);
            while (!shutdown) {
                try {
                    E entry = persistQueue.poll(1, TimeUnit.SECONDS);
                    if (entry != null) {
                        buffer.add(entry);
                    }
                    if (buffer.size() == bufferCapacity) {
                        flush(buffer);
                        buffer.clear();
                    }
                } catch (InterruptedException e) {
                    logger.warn("InterruptedException caught, exit");
                    if (!buffer.isEmpty()) {
                        flush(buffer);
                        buffer.clear();
                    }
                    break;
                }
            }
        }

        private void flush(List<E> buffer) {
            if (buffer != null) {
                for (E entry : buffer) {
                    long timestamp = System.currentTimeMillis();
                    String content = StringUtils.join(timestamp, JsonUtils.toJson(entry));
                    flusher.flush(content);
                }
            }
        }
    }

    public void clear() {
        partitions.forEach(IPartition::clear);
        partitions.clear();
    }

    private boolean isEqualKey(String key1, String key2) {
        return StringUtils.equals(key1, key2);
    }
}
