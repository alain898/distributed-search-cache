package com.maxent.dscache.cache;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.maxent.dscache.cache.collection.IPartition;
import com.maxent.dscache.cache.collection.ListPartition;
import com.maxent.dscache.common.partitioner.HashPartitioner;
import com.maxent.dscache.common.partitioner.IPartitioner;
import com.maxent.dscache.common.persist.Flusher;
import com.maxent.dscache.common.persist.PersistUtils;
import com.maxent.dscache.common.tools.JsonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    private final int blockCapacity;
    private final long blockNumber;

    private final IPartitioner partitioner;
    private final List<IPartition<E>> partitions;

    private final BlockingQueue<E> persistQueue = new ArrayBlockingQueue<>(1024);

    private final String persistFile;
    private final Flusher flusher;
    private final String persistDir = DEFAULT_PERSIST_DIR;

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
        this.blockCapacity = blockCapacity;
        this.blockNumber = blockNumber;

        this.persistFile = String.format("%s_%s", cacheName, subCacheId);
        this.flusher = PersistUtils.createFlusher(persistFile, DEFAULT_PERSIST_DIR, persistFile);

        logger.info(String.format("cacheName[%s], subCacheId[%s], cacheEntryClass[%s], " +
                        "partitionNumber[%d], blockCapacity[%d], blockNumber[%d]",
                this.cacheName, this.subCacheId, this.cacheEntryClass,
                this.partitionNumber, this.blockCapacity, this.blockNumber));
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
        saveWithoutPersist(entry);
        persist(entry);
    }

    private void saveWithoutPersist(E entry) {
        int partition = partitioner.getPartition(entry.key());
        partitions.get(partition).add(entry);
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
                    String content = Joiner.on(",").join(timestamp, JsonUtils.toJson(entry));
                    flusher.flush(content);
                }
            }
        }
    }

    public void clear() {
        partitions.forEach(IPartition::clear);
        partitions.clear();
    }

    public void warmUp() {
        long deltaTime = 2 * 60 * 60 * 1000L;
        try {
            loadSubCacheData(deltaTime);
        } catch (Exception e) {
            logger.error("failed to warm up");
        }
    }

    private long getLastValidTime(Path path) {
        ReversedLinesFileReader reversedLinesFileReader = null;
        try {
            reversedLinesFileReader =
                    new ReversedLinesFileReader(path.toFile(), Charsets.UTF_8);
            String line;
            while ((line = reversedLinesFileReader.readLine()) != null) {
                String[] splits = line.split(",");
                if (splits.length == 2) {
                    try {
                        return Long.parseLong(splits[0]);
                    } catch (Exception e) {
                        logger.warn(String.format("failed to parse[%s]", splits[0]));
                    }
                }
            }
        } catch (Exception e) {
            logger.error(String.format("failed to getLastValidTime[%s]", String.valueOf(path)), e);
            return -1;
        } finally {
            if (reversedLinesFileReader != null) {
                try {
                    reversedLinesFileReader.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return -1;
    }

    private long getLastValidTime(List<Path> paths) {
        for (Path path : paths) {
            Long lastValidTime = getLastValidTime(path);
            if (lastValidTime != -1) {
                return lastValidTime;
            }
        }
        return -1;
    }

    private List<Path> getFilesAfter(List<Path> paths, long timestamp) {
        List<Path> result = new ArrayList<>();
        for (Path path : paths) {
            long lastValidTime = getLastValidTime(path);
            if (lastValidTime >= timestamp) {
                result.add(path);
            }
        }
        return result;
    }

    public void loadSubCacheData(long deltaTime) throws IOException {
        String persistDirPath = new File(persistDir).getName();
        List<Path> paths = Files.
                list(Paths.get(persistDirPath)).
                filter(path -> path.getFileName().toString().startsWith(persistFile)).
                sorted().
                collect(Collectors.toList());

        if (CollectionUtils.isEmpty(paths)) {
            return;
        }

        long lastValidTime = getLastValidTime(paths);
        List<Path> files = Lists.reverse(getFilesAfter(paths, lastValidTime - deltaTime));
        for (Path file : files) {
            LineIterator iterator = FileUtils.lineIterator(file.toFile(), Charsets.UTF_8.toString());
            while (iterator.hasNext()) {
                String line = iterator.nextLine();
                String[] splits = line.split(",");
                if (splits.length != 2) {
                    continue;
                }
                String entryJson = splits[1];
                E entry = JsonUtils.fromJson(entryJson, cacheEntryClass);
                saveWithoutPersist(entry);
            }
        }

    }

    private boolean isEqualKey(String key1, String key2) {
        return StringUtils.equals(key1, key2);
    }

    public static void main(String[] args) throws IOException {
        String persistDir = ".";
        String persistFile = "pom11";
        String persistDirPath = new File(persistDir).getName();
        List<String> paths = Files.
                list(Paths.get(persistDirPath)).
                map(path -> path.getFileName().toString()).
                filter(path -> path.startsWith(persistFile)).
                sorted().
                collect(Collectors.toList());
        for (String path : paths) {
            System.out.println(path);
        }
    }
}
