package com.maxent.dscache.cache;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.maxent.dscache.api.rest.request.RestCreateSubCacheRequest;
import com.maxent.dscache.api.rest.request.RestDeleteSubCacheRequest;
import com.maxent.dscache.api.rest.response.RestCreateSubCacheResponse;
import com.maxent.dscache.api.rest.response.RestDeleteSubCacheResponse;
import com.maxent.dscache.cache.exceptions.CacheCreateFailureException;
import com.maxent.dscache.cache.exceptions.CacheExistException;
import com.maxent.dscache.cache.exceptions.CacheGroupCreateFailureException;
import com.maxent.dscache.cache.exceptions.CacheHostExistException;
import com.maxent.dscache.common.http.HttpClient;
import com.maxent.dscache.common.tools.ClassUtils;
import com.maxent.dscache.common.tools.JsonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alain on 16/8/20.
 */
public enum CacheClusterService implements IService {
    INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(CacheClusterService.class);

    private static final long DEFAULT_START_VERSION = 0;

    private String zookeeperConnectionUrl = "127.0.0.1:2181";

    private CuratorFramework zkClient;

    private final String CACHE_CLUSTER_PATH = "/cache_cluster";
    private final String CACHES_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/caches");
    private final String HOSTS_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/hosts");
    private final String CACHE_GROUPS_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/cache_groups");
    private final String HOST_PATH_PREFIX = "host_";

    InterProcessReadWriteLock clusterGlobalLock = new InterProcessReadWriteLock(zkClient, CACHE_CLUSTER_PATH);

    private final CacheClusterViewer cacheClusterViewer;

    CacheClusterService() throws RuntimeException {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            zkClient = CuratorFrameworkFactory.newClient(zookeeperConnectionUrl, retryPolicy);
            zkClient.start();

            clusterGlobalLock = new InterProcessReadWriteLock(zkClient, CACHE_CLUSTER_PATH);

            cacheClusterViewer = CacheClusterViewerFactory.getCacheClusterViewer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        cacheClusterViewer.close();
        zkClient.close();
    }

    private void createSubCachesInCluster(CacheMeta cacheMeta) throws CacheCreateFailureException {
        HttpClient httpClient = new HttpClient();
        List<SubCacheMeta> subCaches = cacheMeta.getSubCacheMetas();

        try {
            int totalPartitionNumber = cacheMeta.getSubCacheMetas().size() * cacheMeta.getPartitionsPerSubCache();
            for (SubCacheMeta subCache : subCaches) {
                ReplicationMeta meta = subCache.getReplicationMetas().get(0);
                Host host = meta.getHost();
                String url = String.format("http://%s:%d", host.getHost(), host.getPort());
                String path = "/subcache/create";
                RestCreateSubCacheRequest restCreateSubCacheRequest = new RestCreateSubCacheRequest();
                restCreateSubCacheRequest.setName(cacheMeta.getName());
                restCreateSubCacheRequest.setEntryClassName(cacheMeta.getEntryClassName());
                restCreateSubCacheRequest.setTotalPartitionNumber(totalPartitionNumber);
                restCreateSubCacheRequest.setSubCacheId(String.valueOf(subCache.getId()));
                restCreateSubCacheRequest.setPartitionsPerSubCache(cacheMeta.getPartitionsPerSubCache());
                restCreateSubCacheRequest.setBlocksPerPartition(cacheMeta.getBlocksPerPartition());
                restCreateSubCacheRequest.setBlockCapacity(cacheMeta.getBlockCapacity());
                RestCreateSubCacheResponse createCacheResponse =
                        httpClient.post(url, path, restCreateSubCacheRequest, RestCreateSubCacheResponse.class);
                if (createCacheResponse == null) {
                    throw new CacheCreateFailureException(String.format(
                            "failed to create subcache[%s]", JsonUtils.toJson(subCache)));
                }
                if (createCacheResponse.getError() != null) {
                    throw new CacheCreateFailureException(String.format(
                            "failed to create subcache[%s], error[%s]",
                            JsonUtils.toJson(subCache), createCacheResponse.getError()));
                }
            }
        } catch (Exception e) {
            try {
                for (SubCacheMeta subCache : subCaches) {
                    ReplicationMeta meta = subCache.getReplicationMetas().get(0);
                    Host host = meta.getHost();
                    String url = String.format("http://%s:%d", host.getHost(), host.getPort());
                    String path = "/subcache/delete";
                    RestDeleteSubCacheRequest restDeleteSubCacheRequest = new RestDeleteSubCacheRequest();
                    restDeleteSubCacheRequest.setName(cacheMeta.getName());
                    restDeleteSubCacheRequest.setSubCacheId(String.valueOf(subCache.getId()));
                    httpClient.post(url, path, restDeleteSubCacheRequest, RestDeleteSubCacheResponse.class);
                }
            } catch (Exception e1) {
                logger.error("failed to clean cacheMeta[%s] after create failed", e);
            }
            throw e;
        }
    }

    private void doCreateCache(CacheMeta cacheMeta) throws Exception {
        String name = cacheMeta.getName();
        String cacheZkPath = StringUtils.join(CACHES_PATH, "/", name);
        CacheZnode cacheZnode = new CacheZnode();
        cacheZnode.setName(cacheMeta.getName());
        cacheZnode.setVersion(cacheMeta.getVersion());
        cacheZnode.setEntryClassName(cacheMeta.getEntryClassName());
        cacheZnode.setPartitionsPerSubCache(cacheMeta.getPartitionsPerSubCache());
        cacheZnode.setBlockCapacity(cacheMeta.getBlockCapacity());
        cacheZnode.setBlocksPerPartition(cacheMeta.getBlocksPerPartition());
        cacheZnode.setCacheGroup(cacheMeta.getCacheGroup());
        cacheZnode.setForwardCache(cacheMeta.getForwardCache());
        cacheZnode.setForwardThreshold(cacheMeta.getForwardThreshold());
        zkClient.create().forPath(cacheZkPath);
        zkClient.setData().forPath(cacheZkPath, JsonUtils.toJson(cacheZnode).getBytes(Charsets.UTF_8));

        for (SubCacheMeta subCacheMeta : cacheMeta.getSubCacheMetas()) {
            SubCacheZnode subCacheZnode = new SubCacheZnode();
            subCacheZnode.setId(subCacheMeta.getId());
            String subCacheZkPath = StringUtils.join(cacheZkPath, "/", subCacheMeta.getZkNodeName());
            zkClient.create().forPath(subCacheZkPath);
            zkClient.setData().forPath(subCacheZkPath, JsonUtils.toJson(subCacheZnode).getBytes(Charsets.UTF_8));

            for (ReplicationMeta replicationMeta : subCacheMeta.getReplicationMetas()) {
                ReplicationZnode replicationZnode = new ReplicationZnode();
                replicationZnode.setId(replicationMeta.getId());
                replicationZnode.setHostId(replicationMeta.getHost().getId());
                String replicationZkPath = StringUtils.join(subCacheZkPath, "/", replicationMeta.getZkNodeName());
                zkClient.create().forPath(replicationZkPath);
                zkClient.setData().forPath(replicationZkPath,
                        JsonUtils.toJson(replicationZnode).getBytes(Charsets.UTF_8));
            }
        }
    }

    private String incVersion(String version) {
        return String.valueOf(Integer.parseInt(version) + 1);
    }

    public void increaseClusterVersion() throws Exception {
        CacheClusterZnode cacheClusterZnode = JsonUtils.fromJson(
                new String(zkClient.getData().forPath(CACHE_CLUSTER_PATH), Charsets.UTF_8),
                CacheClusterZnode.class);
        String zkClusterVersion = cacheClusterZnode.getVersion();
        cacheClusterZnode.setVersion(incVersion(zkClusterVersion));
        zkClient.setData().forPath(CACHE_CLUSTER_PATH,
                JsonUtils.toJson(cacheClusterZnode).getBytes(Charsets.UTF_8));
    }

    private String genIndexString(long index) {
        return String.format("%016d", index);
    }

    private Map<Integer, Integer> countHostsUsageOfCacheGroup(CacheGroupMeta cacheGroupMeta, List<Host> hosts) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(hosts), "hosts is empty");

        final Map<Integer, Integer> hostsCount = new HashMap<>();
        hosts.forEach(host -> hostsCount.put(host.getId(), 0));

        if (cacheGroupMeta == null || cacheGroupMeta.getCacheMetas() == null) {
            return hostsCount;
        }

        for (CacheMeta cacheMeta : cacheGroupMeta.getCacheMetas()) {
            for (SubCacheMeta subCacheMeta : cacheMeta.getSubCacheMetas()) {
                countHosts(hostsCount, subCacheMeta.getReplicationMetas().get(0).getId());
            }
        }
        return hostsCount;
    }

    private void countHosts(Map<Integer, Integer> hostsCount, int hostId) {
        if (hostsCount.containsKey(hostId)) {
            hostsCount.put(hostId, hostsCount.get(hostId) + 1);
        } else {
            hostsCount.put(hostId, 1);
        }
    }

    private int chooseThenCountHost(Map<Integer, Integer> hostsCount) {
        int min = Integer.MAX_VALUE;
        int keyWithMinValue = -1;
        for (Map.Entry<Integer, Integer> entry : hostsCount.entrySet()) {
            if (entry.getValue() < min) {
                min = entry.getValue();
                keyWithMinValue = entry.getKey();
            }
        }
        countHosts(hostsCount, keyWithMinValue);
        return keyWithMinValue;
    }

    public CacheMeta createCache(String name, String entryClassName,
                                 int subCaches, int partitionsPerSubCache,
                                 int blockCapacity, int blocksPerPartition,
                                 String cacheGroup, String forwardCache,
                                 long forwardThreshold,
                                 boolean incClusterVersion) throws Exception {
        lockIfVersionMatched();
        try {
            CacheClusterMeta cacheClusterMeta = cacheClusterViewer.getCacheClusterMeta();
            List<Host> hosts = cacheClusterMeta.getHosts();
            List<CacheMeta> caches = cacheClusterMeta.getCaches();
            for (CacheMeta cache : caches) {
                if (cache.getName().equals(name)) {
                    throw new CacheExistException(String.format("cache[%s] already exist", name));
                }
            }

            CacheMeta cacheMeta = new CacheMeta();
            cacheMeta.setVersion(String.valueOf(DEFAULT_START_VERSION));
            cacheMeta.setName(name);
            cacheMeta.setEntryClassName(entryClassName);
            cacheMeta.setEntryClass(ClassUtils.loadClass(entryClassName, ICacheEntry.class));
            cacheMeta.setBlockCapacity(blockCapacity);
            cacheMeta.setBlocksPerPartition(blocksPerPartition);
            cacheMeta.setCacheGroup(cacheGroup);
            cacheMeta.setForwardCache(forwardCache);
            cacheMeta.setForwardThreshold(forwardThreshold);

            Map<Integer, Integer> hostsCount = countHostsUsageOfCacheGroup(
                    cacheClusterViewer.getCacheGroupMeta(cacheMeta.getCacheGroup()),
                    cacheClusterViewer.getHosts());
            List<SubCacheMeta> subCacheMetas = new ArrayList<>(subCaches);
            for (int i = 0; i < subCaches; i++) {
                SubCacheMeta subCacheMeta = new SubCacheMeta();
                subCacheMeta.setId(i);
                subCacheMeta.setZkNodeName(String.format("subcache_%s", genIndexString(i)));
                ReplicationMeta replicationMeta = new ReplicationMeta();
                replicationMeta.setId(0);
                replicationMeta.setHost(hosts.get(chooseThenCountHost(hostsCount)));
                replicationMeta.setZkNodeName(String.format("replication_%s", genIndexString(0)));
                List<ReplicationMeta> replicationMetas = new ArrayList<>();
                replicationMetas.add(replicationMeta);
                subCacheMeta.setReplicationMetas(replicationMetas);
                subCacheMetas.add(subCacheMeta);
            }
            cacheMeta.setSubCacheMetas(subCacheMetas);
            cacheMeta.setPartitionsPerSubCache(partitionsPerSubCache);


            // 先改变集群的状态
            createSubCachesInCluster(cacheMeta);

            // 再改变集群在zookeeper中的状态
            doCreateCache(cacheMeta);

            // 增加版本号
            if (incClusterVersion) {
                increaseClusterVersion();
            }

            return cacheMeta;

        } finally {
            try {
                unlockIfVersionMatched();
            } catch (Exception e) {
                logger.error(String.format("failed to release clusterGlobalLock on zknode[%s]", CACHE_CLUSTER_PATH), e);
            }
        }
    }

    public CacheMeta createCache(String name, String entryClassName,
                                 int subCaches, int partitionsPerSubCache,
                                 int blockCapacity, int blocksPerPartition,
                                 boolean incClusterVersion)
            throws Exception {
        return createCache(name, entryClassName,
                subCaches, partitionsPerSubCache,
                blockCapacity, blocksPerPartition,
                null, null, -1, incClusterVersion);
    }

    private void lockIfVersionMatched() throws Exception {
        while (true) {
            clusterGlobalLock.writeLock().acquire();
            CacheClusterZnode cacheClusterZnode = JsonUtils.fromJson(
                    new String(zkClient.getData().forPath(CACHE_CLUSTER_PATH), Charsets.UTF_8),
                    CacheClusterZnode.class);
            String zkClusterVersion = cacheClusterZnode.getVersion();
            String localClusterVersion = cacheClusterViewer.getCacheClusterMeta().getVersion();
            if (StringUtils.equals(zkClusterVersion, localClusterVersion)) {
                break;
            } else {
                clusterGlobalLock.writeLock().release();
                Thread.sleep(100);
            }
        }
    }

    private void unlockIfVersionMatched() throws Exception {
        while (true) {
            CacheClusterZnode cacheClusterZnode = JsonUtils.fromJson(
                    new String(zkClient.getData().forPath(CACHE_CLUSTER_PATH), Charsets.UTF_8),
                    CacheClusterZnode.class);
            String zkClusterVersion = cacheClusterZnode.getVersion();
            String localClusterVersion = cacheClusterViewer.getCacheClusterMeta().getVersion();
            if (StringUtils.equals(zkClusterVersion, localClusterVersion)) {
                clusterGlobalLock.writeLock().release();
                break;
            } else {
                Thread.sleep(100);
            }
        }
    }

    private void doAddHost(Host host) throws Exception {
        String hostPath = StringUtils.join(
                HOSTS_PATH, "/", String.format("%s_%s", HOST_PATH_PREFIX, genIndexString(host.getId())));
        zkClient.create().forPath(hostPath);
        zkClient.setData().forPath(hostPath, JsonUtils.toJson(host).getBytes(Charsets.UTF_8));
    }

    public void addHosts(List<Host> newHosts) throws Exception {
        lockIfVersionMatched();
        try {
            CacheClusterMeta cacheClusterMeta = cacheClusterViewer.getCacheClusterMeta();
            List<Host> hosts = cacheClusterMeta.getHosts();
            for (Host newHost : newHosts) {
                if (hosts.contains(newHost)) {
                    throw new CacheHostExistException(String.format(
                            "host[%s] already exist", JsonUtils.toJson(newHost)));
                }
            }

            int newHostIdStart = hosts.size();
            for (int i = 0; i < newHosts.size(); i++) {
                Host newHost = newHosts.get(i);
                newHost.setId(newHostIdStart + i);
                doAddHost(newHost);
            }

            // 增加版本号
            increaseClusterVersion();
        } finally {
            try {
                unlockIfVersionMatched();
            } catch (Exception e) {
                logger.error(String.format("failed to release clusterGlobalLock on zknode[%s]", CACHE_CLUSTER_PATH), e);
            }
        }
    }

    public void updateCacheGroup(String cacheGroupName,
                                 int addedCaches) throws Exception {
        lockIfVersionMatched();
        try {
            CacheClusterMeta cacheClusterMeta = cacheClusterViewer.getCacheClusterMeta();
            List<CacheGroupMeta> cacheGroups = cacheClusterMeta.getCacheGroups();
            CacheGroupMeta cacheGroupMeta = null;
            for (CacheGroupMeta cacheGroup : cacheGroups) {
                if (cacheGroup.getCacheGroupName().equals(cacheGroupName)) {
                    cacheGroupMeta = cacheGroup;
                }
            }
            if (cacheGroupMeta == null) {
                throw new CacheGroupCreateFailureException("failed to add cache in cacheGroupName");
            }

            String entryClassName = cacheGroupMeta.getEntryClassName();
            int subCachesPerCache = cacheGroupMeta.getSubCachesPerCache();
            int partitionsPerSubCache = cacheGroupMeta.getPartitionsPerSubCache();
            int blockCapacity = cacheGroupMeta.getBlockCapacity();
            int blocksPerPartition = cacheGroupMeta.getBlocksPerPartition();
            int cachesNumber = cacheGroupMeta.getCurrentCachesNumber();
            List<CacheMeta> newCaches = new ArrayList<>();
            for (int i = 0; i < addedCaches; i++) {
                String cacheName = String.format("%s_cache_%s", cacheGroupName, genIndexString(cachesNumber + i));
                String forwardCache = String.format("%s_cache_%s", cacheGroupName, genIndexString(i % cachesNumber));
                long forwardThreshold = 99; //TODO: add to request parameter
                CacheMeta newCache = createCache(
                        cacheName, entryClassName,
                        subCachesPerCache, partitionsPerSubCache,
                        blockCapacity, blocksPerPartition,
                        cacheGroupName,
                        forwardCache, forwardThreshold,
                        false);
                newCaches.add(newCache);
            }

            doAddCacheInCacheGroup(cacheGroupMeta, newCaches);

            // 增加版本号
            increaseClusterVersion();

        } finally {
            try {
                unlockIfVersionMatched();
            } catch (Exception e) {
                logger.error(String.format("failed to release clusterGlobalLock on zknode[%s]", CACHE_CLUSTER_PATH), e);
            }
        }
    }

    public void createCacheGroup(String cacheGroupName,
                                 String entryClassName,
                                 int cacheGroupCapacity,
                                 int cachesNumber,
                                 int subCachesPerCache,
                                 int partitionsPerSubCache,
                                 int blocksPerPartition,
                                 int blockCapacity) throws Exception {
        lockIfVersionMatched();
        try {
            CacheClusterMeta cacheClusterMeta = cacheClusterViewer.getCacheClusterMeta();
            List<CacheGroupMeta> cacheGroups = cacheClusterMeta.getCacheGroups();
            for (CacheGroupMeta cacheGroup : cacheGroups) {
                if (cacheGroup.getCacheGroupName().equals(cacheGroupName)) {
                    throw new CacheGroupCreateFailureException(
                            String.format("cacheGroupName[%s] exist", cacheGroupName));
                }
            }

            List<CacheMeta> cacheMetas = new ArrayList<>();
            for (int i = 0; i < cachesNumber; i++) {
                String cacheName = String.format("%s_cache_%s", cacheGroupName, genIndexString(i));
                CacheMeta cacheMeta = createCache(
                        cacheName, entryClassName,
                        subCachesPerCache, partitionsPerSubCache,
                        blockCapacity, blocksPerPartition,
                        cacheGroupName, null, -1,
                        false);
                cacheMetas.add(cacheMeta);
            }

            CacheGroupMeta cacheGroupMeta = new CacheGroupMeta();
            cacheGroupMeta.setCacheGroupName(cacheGroupName);
            cacheGroupMeta.setCacheGroupCapacity(cacheGroupCapacity);
            cacheGroupMeta.setCurrentCachesNumber(cachesNumber);
            cacheGroupMeta.setCacheMetas(cacheMetas);
            cacheGroupMeta.setLastCachesNumber(-1);
            cacheGroupMeta.setEntryClassName(entryClassName);
            cacheGroupMeta.setSubCachesPerCache(subCachesPerCache);
            cacheGroupMeta.setPartitionsPerSubCache(partitionsPerSubCache);
            cacheGroupMeta.setBlocksPerPartition(blocksPerPartition);
            cacheGroupMeta.setBlockCapacity(blockCapacity);

            doCreateCacheGroupInZookeeper(cacheGroupMeta);

            // 增加版本号
            increaseClusterVersion();

        } finally {
            try {
                unlockIfVersionMatched();
            } catch (Exception e) {
                logger.error(String.format("failed to release clusterGlobalLock on zknode[%s]", CACHE_CLUSTER_PATH), e);
            }
        }
    }

    private void doCreateCacheGroupInZookeeper(CacheGroupMeta cacheGroupMeta) throws Exception {
        lockIfVersionMatched();
        try {
            CacheGroupZnode cacheGroupZnode = new CacheGroupZnode();

            cacheGroupZnode.setCacheGroupName(cacheGroupMeta.getCacheGroupName());
            cacheGroupZnode.setCacheGroupCapacity(cacheGroupMeta.getCacheGroupCapacity());
            cacheGroupZnode.setCurrentCachesNumber(cacheGroupMeta.getCurrentCachesNumber());
            cacheGroupZnode.setLastCachesNumber(cacheGroupMeta.getLastCachesNumber());
            cacheGroupZnode.setEntryClassName(cacheGroupMeta.getEntryClassName());
            cacheGroupZnode.setPartitionsPerSubCache(cacheGroupMeta.getPartitionsPerSubCache());
            cacheGroupZnode.setSubCachesPerCache(cacheGroupMeta.getSubCachesPerCache());
            cacheGroupZnode.setBlockCapacity(cacheGroupMeta.getBlockCapacity());
            cacheGroupZnode.setBlocksPerPartition(cacheGroupMeta.getBlocksPerPartition());

            String name = cacheGroupZnode.getCacheGroupName();
            String cacheGroupZkPath = StringUtils.join(CACHE_GROUPS_PATH, "/", name);
            zkClient.create().forPath(cacheGroupZkPath);
            zkClient.setData().forPath(cacheGroupZkPath,
                    JsonUtils.toJson(cacheGroupZnode).getBytes(Charsets.UTF_8));

            for (CacheMeta cacheMeta : cacheGroupMeta.getCacheMetas()) {
                String cacheZkPath = StringUtils.join(cacheGroupZkPath, "/", cacheMeta.getName());
                zkClient.create().forPath(cacheZkPath);
            }

        } finally {
            try {
                unlockIfVersionMatched();
            } catch (Exception e) {
                logger.error(String.format("failed to release clusterGlobalLock on zknode[%s]", CACHE_CLUSTER_PATH), e);
            }
        }

    }


    private void doAddCacheInCacheGroup(CacheGroupMeta cacheGroupMeta, List<CacheMeta> newCacheMetas) throws Exception {
        lockIfVersionMatched();
        try {
            List<CacheMeta> allCacheMetas = new ArrayList<>();
            allCacheMetas.addAll(cacheGroupMeta.getCacheMetas());
            allCacheMetas.addAll(newCacheMetas);


            CacheGroupZnode cacheGroupZnode = new CacheGroupZnode();
            cacheGroupZnode.setCacheGroupName(cacheGroupMeta.getCacheGroupName());
            cacheGroupZnode.setCacheGroupCapacity(cacheGroupMeta.getCacheGroupCapacity());
            cacheGroupZnode.setCurrentCachesNumber(cacheGroupMeta.getCurrentCachesNumber());
            cacheGroupZnode.setLastCachesNumber(cacheGroupMeta.getLastCachesNumber());
            cacheGroupZnode.setEntryClassName(cacheGroupMeta.getEntryClassName());
            cacheGroupZnode.setPartitionsPerSubCache(cacheGroupMeta.getPartitionsPerSubCache());
            cacheGroupZnode.setSubCachesPerCache(cacheGroupMeta.getSubCachesPerCache());
            cacheGroupZnode.setBlockCapacity(cacheGroupMeta.getBlockCapacity());
            cacheGroupZnode.setBlocksPerPartition(cacheGroupMeta.getBlocksPerPartition());

            String name = cacheGroupZnode.getCacheGroupName();
            String cacheGroupZkPath = StringUtils.join(CACHE_GROUPS_PATH, "/", name);
            zkClient.setData().forPath(cacheGroupZkPath,
                    JsonUtils.toJson(cacheGroupZnode).getBytes(Charsets.UTF_8));

            for (CacheMeta cacheMeta : allCacheMetas) {
                String cacheZkPath = StringUtils.join(cacheGroupZkPath, "/", cacheMeta.getName());
                zkClient.create().forPath(cacheZkPath);
            }

            // // TODO: 16/9/8 roll back if failed

        } finally {
            try {
                unlockIfVersionMatched();
            } catch (Exception e) {
                logger.error(String.format("failed to release clusterGlobalLock on zknode[%s]", CACHE_CLUSTER_PATH), e);
            }
        }

    }

    public void deleteCacheGroup(String cacheGroupName) throws Exception {
        lockIfVersionMatched();
        try {
            CacheClusterMeta cacheClusterMeta = cacheClusterViewer.getCacheClusterMeta();
            List<CacheGroupMeta> cacheGroups = cacheClusterMeta.getCacheGroups();
            CacheGroupMeta cacheGroupMeta = null;
            for (CacheGroupMeta cacheGroup : cacheGroups) {
                if (cacheGroup.getCacheGroupName().equals(cacheGroupName)) {
                    cacheGroupMeta = cacheGroup;
                }
            }

            if (cacheGroupMeta == null) {
                return;
            }

            doDeleteCacheGroup(cacheGroupMeta);

        } finally {
            try {
                unlockIfVersionMatched();
            } catch (Exception e) {
                logger.error(String.format("failed to release clusterGlobalLock on zknode[%s]", CACHE_CLUSTER_PATH), e);
            }
        }
    }

    private void doDeleteCacheGroup(CacheGroupMeta cacheGroupMeta) throws Exception {
        List<CacheMeta> cacheMetas = cacheGroupMeta.getCacheMetas();
        for (CacheMeta cache : cacheMetas) {
            deleteCache(cache.getName());
        }
    }

    public void deleteCache(String cacheName) throws Exception {
        lockIfVersionMatched();
        HttpClient httpClient = new HttpClient();
        try {
            CacheClusterMeta cacheClusterMeta = cacheClusterViewer.getCacheClusterMeta();
            List<CacheMeta> caches = cacheClusterMeta.getCaches();
            CacheMeta cacheMeta = null;
            for (CacheMeta cache : caches) {
                if (cache.getName().equals(cacheName)) {
                    cacheMeta = cache;
                }
            }

            if (cacheMeta == null) {
                return;
            }

            List<SubCacheMeta> subCaches = cacheMeta.getSubCacheMetas();
            for (SubCacheMeta subCache : subCaches) {
                ReplicationMeta meta = subCache.getReplicationMetas().get(0);
                Host host = meta.getHost();
                String url = String.format("http://%s:%d", host.getHost(), host.getPort());
                String path = "/subcache/delete";
                RestDeleteSubCacheRequest restDeleteSubCacheRequest = new RestDeleteSubCacheRequest();
                restDeleteSubCacheRequest.setName(cacheMeta.getName());
                restDeleteSubCacheRequest.setSubCacheId(String.valueOf(subCache.getId()));
                httpClient.post(url, path, restDeleteSubCacheRequest, RestDeleteSubCacheResponse.class);
            }

            String name = cacheMeta.getName();
            String cacheZkPath = StringUtils.join(CACHES_PATH, "/", name);
            zkClient.delete().forPath(cacheZkPath);

        } finally {
            try {
                unlockIfVersionMatched();
            } catch (Exception e) {
                logger.error(String.format("failed to release clusterGlobalLock on zknode[%s]", CACHE_CLUSTER_PATH), e);
            }
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}


