package com.maxent.dscache.cache;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.maxent.dscache.cache.ICacheEntry;
import com.maxent.dscache.cache.collection.IPartition;
import com.maxent.dscache.cache.collection.ListPartition;
import com.maxent.dscache.common.partitioner.HashPartitioner;
import com.maxent.dscache.common.partitioner.IPartitioner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alain on 16/8/18.
 */
public class SubCache<E extends ICacheEntry> {
    private final Class<E> cacheEntryClass;
    private IPartitioner partitioner;
    private List<IPartition<E>> partitions;

    public SubCache(Class<E> cacheEntryClass, int partitionNumber, int blockCapacity, long blockNumber) {
        Preconditions.checkNotNull(cacheEntryClass, "cacheEntryClass is null");
        Preconditions.checkArgument(partitionNumber > 0, "partitionNumber must be positive");
        Preconditions.checkArgument(blockCapacity > 0, "blockCapacity must be positive");
        Preconditions.checkArgument(blockNumber > 0, "blockNumber must be positive");

        this.cacheEntryClass = cacheEntryClass;
        this.partitioner = new HashPartitioner(partitionNumber);
        this.partitions = createPartitions(partitionNumber, blockCapacity, blockNumber);
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

    private boolean isEqualKey(String key1, String key2) {
        return StringUtils.equals(key1, key2);
    }
}
