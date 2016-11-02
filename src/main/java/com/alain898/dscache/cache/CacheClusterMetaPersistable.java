package com.alain898.dscache.cache;

import com.alain898.dscache.common.partitioner.HashPartitioner;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alain on 16/9/24.
 */
public class CacheClusterMetaPersistable {
    private String version;
    private List<CacheMetaPersistable> caches;
    private List<Host> hosts;
    private List<CacheGroupMetaPersistable> cacheGroups;

    public static CacheClusterMetaPersistable makePersitable(CacheClusterMeta cacheClusterMeta) {
        CacheClusterMetaPersistable cacheClusterMetaPersistable = new CacheClusterMetaPersistable();
        cacheClusterMetaPersistable.version = cacheClusterMeta.getVersion();

        if (cacheClusterMeta.getCaches() == null) {
            cacheClusterMetaPersistable.caches = null;
        } else {
            cacheClusterMetaPersistable.caches = cacheClusterMeta.getCaches().stream().
                    map(CacheMetaPersistable::makePersitable).collect(Collectors.toList());
        }

        cacheClusterMetaPersistable.hosts = cacheClusterMeta.getHosts();

        if (cacheClusterMeta.getCacheGroups() == null) {
            cacheClusterMetaPersistable.cacheGroups = null;
        } else {
            cacheClusterMetaPersistable.cacheGroups = cacheClusterMeta.getCacheGroups().stream().
                    map(CacheGroupMetaPersistable::makePersitable).collect(Collectors.toList());
        }

        return cacheClusterMetaPersistable;
    }

    public static CacheClusterMeta fromPersitable(CacheClusterMetaPersistable cacheClusterMetaPersistable) {
        CacheClusterMeta cacheClusterMeta = new CacheClusterMeta();
        cacheClusterMeta.setVersion(cacheClusterMetaPersistable.version);
        cacheClusterMeta.setHosts(cacheClusterMetaPersistable.hosts);

        if (cacheClusterMetaPersistable.caches == null) {
            cacheClusterMeta.setCaches(null);
        } else {
            cacheClusterMeta.setCaches(cacheClusterMetaPersistable.caches.stream().
                    map(CacheMetaPersistable::fromPersitable).collect(Collectors.toList()));
        }

        if (cacheClusterMetaPersistable.cacheGroups == null) {
            cacheClusterMeta.setCacheGroups(null);
        } else {
            cacheClusterMeta.setCacheGroups(cacheClusterMetaPersistable.cacheGroups.stream().
                    map(CacheGroupMetaPersistable::fromPersitable).collect(Collectors.toList()));
        }

        return cacheClusterMeta;
    }


    private static class CacheMetaPersistable {
        private String version;

        private String name;
        private String partitioner;

        private String entryClassName;
        private String entryClass;

        private List<SubCacheMetaPersistable> subCacheMetas;
        private int blockCapacity;
        private int blocksPerPartition;
        private int partitionsPerSubCache;

        private String cacheGroup;
        private String forwardCache;
        private long forwardThreshold;

        public static CacheMetaPersistable makePersitable(CacheMeta cacheMeta) {
            if (cacheMeta == null) {
                return null;
            }

            CacheMetaPersistable cacheMetaPersistable = new CacheMetaPersistable();
            cacheMetaPersistable.version = cacheMeta.getVersion();
            cacheMetaPersistable.name = cacheMeta.getName();
            cacheMetaPersistable.partitioner = cacheMeta.getPartitioner().getClass().getCanonicalName();
            cacheMetaPersistable.entryClassName = cacheMeta.getEntryClassName();
            cacheMetaPersistable.entryClass = cacheMeta.getEntryClass().getCanonicalName();

            if (cacheMeta.getSubCacheMetas() == null) {
                cacheMetaPersistable.subCacheMetas = null;
            } else {
                cacheMetaPersistable.subCacheMetas = cacheMeta.getSubCacheMetas().stream().
                        map(SubCacheMetaPersistable::makePersitable).collect(Collectors.toList());
            }

            cacheMetaPersistable.blockCapacity = cacheMeta.getBlockCapacity();
            cacheMetaPersistable.blocksPerPartition = cacheMeta.getBlocksPerPartition();
            cacheMetaPersistable.partitionsPerSubCache = cacheMeta.getPartitionsPerSubCache();
            cacheMetaPersistable.cacheGroup = cacheMeta.getCacheGroup();
            cacheMetaPersistable.forwardCache = cacheMeta.getForwardCache();
            cacheMetaPersistable.forwardThreshold = cacheMeta.getForwardThreshold();

            return cacheMetaPersistable;
        }

        public static CacheMeta fromPersitable(CacheMetaPersistable cacheMetaPersistable) {
            if (cacheMetaPersistable == null) {
                return null;
            }

            CacheMeta cacheMeta = new CacheMeta();
            cacheMeta.setVersion(cacheMetaPersistable.version);
            cacheMeta.setName(cacheMetaPersistable.name);
            int partitions = 0;
            if (cacheMetaPersistable.subCacheMetas != null) {
                partitions = cacheMetaPersistable.partitionsPerSubCache * cacheMetaPersistable.subCacheMetas.size();
            }
            cacheMeta.setPartitioner(new HashPartitioner(partitions));
            cacheMeta.setEntryClassName(cacheMetaPersistable.entryClassName);
            cacheMeta.setEntryClass(loadClass(cacheMetaPersistable.entryClass, ICacheEntry.class));

            if (cacheMetaPersistable.subCacheMetas == null) {
                cacheMeta.setSubCacheMetas(null);
            } else {
                cacheMeta.setSubCacheMetas(cacheMetaPersistable.subCacheMetas.stream().
                        map(SubCacheMetaPersistable::fromPersitable).collect(Collectors.toList()));
            }

            cacheMeta.setBlockCapacity(cacheMetaPersistable.blockCapacity);
            cacheMeta.setBlocksPerPartition(cacheMetaPersistable.blocksPerPartition);
            cacheMeta.setPartitionsPerSubCache(cacheMetaPersistable.partitionsPerSubCache);
            cacheMeta.setCacheGroup(cacheMetaPersistable.cacheGroup);
            cacheMeta.setForwardCache(cacheMetaPersistable.forwardCache);
            cacheMeta.setForwardThreshold(cacheMetaPersistable.forwardThreshold);

            return cacheMeta;
        }
    }

    private static <T> Class<T> loadClass(String className, Class<T> tClass) {
        Class clazz;
        try {
            clazz = ClassLoader.getSystemClassLoader().loadClass(className);
        } catch (Exception e) {
            String errInfo = String.format("failed to load className[%s]", className);
            throw new RuntimeException(errInfo, e);
        }

        if (!tClass.isAssignableFrom(clazz)) {
            String errInfo = String.format("className[%s] is not tClass[%s]", className, tClass.getCanonicalName());
            throw new RuntimeException(errInfo);
        }

        return clazz;
    }

    private static class SubCacheMetaPersistable {
        private int id;
        private String zkNodeName;
        private List<ReplicationMetaPersistable> replicationMetas;

        private static SubCacheMetaPersistable makePersitable(SubCacheMeta subCacheMeta) {
            if (subCacheMeta == null) {
                return null;
            }

            SubCacheMetaPersistable subCacheMetaPersistable = new SubCacheMetaPersistable();
            subCacheMetaPersistable.id = subCacheMeta.getId();
            subCacheMetaPersistable.zkNodeName = subCacheMeta.getZkNodeName();

            if (subCacheMeta.getReplicationMetas() == null) {
                return null;
            } else {
                subCacheMetaPersistable.replicationMetas = subCacheMeta.getReplicationMetas().stream().
                        map(ReplicationMetaPersistable::makePersitable).collect(Collectors.toList());
            }

            return subCacheMetaPersistable;
        }

        private static SubCacheMeta fromPersitable(SubCacheMetaPersistable subCacheMetaPersistable) {
            if (subCacheMetaPersistable == null) {
                return null;
            }

            SubCacheMeta subCacheMeta = new SubCacheMeta();
            subCacheMeta.setId(subCacheMetaPersistable.id);
            subCacheMeta.setZkNodeName(subCacheMetaPersistable.zkNodeName);

            if (subCacheMetaPersistable.replicationMetas == null) {
                subCacheMeta.setReplicationMetas(null);
            } else {
                subCacheMeta.setReplicationMetas(subCacheMetaPersistable.replicationMetas.stream().
                        map(ReplicationMetaPersistable::fromPersitable).collect(Collectors.toList()));
            }

            return subCacheMeta;
        }
    }

    private static class ReplicationMetaPersistable {
        private int id;
        private String zkNodeName;
        private Host host;

        private static ReplicationMetaPersistable makePersitable(
                ReplicationMeta replicationMeta) {
            if (replicationMeta == null) {
                return null;
            }

            ReplicationMetaPersistable replicationMetaPersistable = new ReplicationMetaPersistable();
            replicationMetaPersistable.id = replicationMeta.getId();
            replicationMetaPersistable.host = replicationMeta.getHost();
            replicationMetaPersistable.zkNodeName = replicationMeta.getZkNodeName();

            return replicationMetaPersistable;
        }

        private static ReplicationMeta fromPersitable(
                ReplicationMetaPersistable replicationMetaPersistable) {
            if (replicationMetaPersistable == null) {
                return null;
            }

            ReplicationMeta replicationMeta = new ReplicationMeta();
            replicationMeta.setId(replicationMetaPersistable.id);
            replicationMeta.setZkNodeName(replicationMetaPersistable.zkNodeName);
            replicationMeta.setHost(replicationMetaPersistable.host);

            return replicationMeta;
        }
    }

    private static class CacheGroupMetaPersistable {
        private String cacheGroupName;
        private String entryClassName;
        private int subCachesPerCache;
        private int cacheGroupCapacity;     // 2^C
        private int partitionsPerSubCache;
        private int blocksPerPartition;
        private int blockCapacity;

        private List<CacheMetaPersistable> cacheMetas;

        private int currentCachesNumber;    // 2^cm

        private static CacheGroupMetaPersistable makePersitable(CacheGroupMeta cacheGroupMeta) {
            if (cacheGroupMeta == null) {
                return null;
            }

            CacheGroupMetaPersistable cacheGroupMetaPersistable = new CacheGroupMetaPersistable();
            cacheGroupMetaPersistable.cacheGroupName = cacheGroupMeta.getCacheGroupName();
            cacheGroupMetaPersistable.entryClassName = cacheGroupMeta.getEntryClassName();
            cacheGroupMetaPersistable.subCachesPerCache = cacheGroupMeta.getSubCachesPerCache();
            cacheGroupMetaPersistable.cacheGroupCapacity = cacheGroupMeta.getCacheGroupCapacity();
            cacheGroupMetaPersistable.partitionsPerSubCache = cacheGroupMeta.getPartitionsPerSubCache();
            cacheGroupMetaPersistable.blocksPerPartition = cacheGroupMeta.getBlocksPerPartition();
            cacheGroupMetaPersistable.blockCapacity = cacheGroupMeta.getBlockCapacity();

            if (cacheGroupMeta.getCacheMetas() == null) {
                cacheGroupMetaPersistable.cacheMetas = null;
            } else {
                cacheGroupMetaPersistable.cacheMetas = cacheGroupMeta.getCacheMetas().stream().
                        map(CacheMetaPersistable::makePersitable).collect(Collectors.toList());
            }

            cacheGroupMetaPersistable.currentCachesNumber = cacheGroupMeta.getCurrentCachesNumber();

            return cacheGroupMetaPersistable;
        }

        private static CacheGroupMeta fromPersitable(CacheGroupMetaPersistable cacheGroupMetaPersistable) {
            if (cacheGroupMetaPersistable == null) {
                return null;
            }

            CacheGroupMeta cacheGroupMeta = new CacheGroupMeta();
            cacheGroupMeta.setCacheGroupName(cacheGroupMetaPersistable.cacheGroupName);
            cacheGroupMeta.setEntryClassName(cacheGroupMetaPersistable.entryClassName);
            cacheGroupMeta.setSubCachesPerCache(cacheGroupMetaPersistable.subCachesPerCache);
            cacheGroupMeta.setCacheGroupCapacity(cacheGroupMetaPersistable.cacheGroupCapacity);
            cacheGroupMeta.setPartitionsPerSubCache(cacheGroupMetaPersistable.partitionsPerSubCache);
            cacheGroupMeta.setBlocksPerPartition(cacheGroupMetaPersistable.blocksPerPartition);
            cacheGroupMeta.setBlockCapacity(cacheGroupMetaPersistable.blockCapacity);

            if (cacheGroupMetaPersistable.cacheMetas == null) {
                cacheGroupMeta.setCacheMetas(null);
            } else {
                cacheGroupMeta.setCacheMetas(cacheGroupMetaPersistable.cacheMetas.stream().
                        map(CacheMetaPersistable::fromPersitable).collect(Collectors.toList()));
            }

            cacheGroupMeta.setCurrentCachesNumber(cacheGroupMetaPersistable.currentCachesNumber);

            return cacheGroupMeta;
        }
    }
}
