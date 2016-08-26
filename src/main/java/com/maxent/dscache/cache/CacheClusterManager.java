package com.maxent.dscache.cache;

import com.google.common.base.Charsets;
import com.maxent.dscache.api.rest.request.RestCreateCacheRequest;
import com.maxent.dscache.api.rest.response.RestCreateCacheResponse;
import com.maxent.dscache.cache.exceptions.CacheExistException;
import com.maxent.dscache.common.http.HttpClient;
import com.maxent.dscache.common.partitioner.HashPartitioner;
import com.maxent.dscache.common.tools.ClassUtils;
import com.maxent.dscache.common.tools.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alain on 16/8/20.
 */
public class CacheClusterManager {
    private static final Logger logger = LoggerFactory.getLogger(CacheClusterManager.class);

    private static final long DEFAULT_START_VERSION = 0;

    private String zookeeperConnectionUrl = "";

    private CuratorFramework zkClient;

    private String CACHE_CLUSTER_PATH = "/cache_cluster";
    private String CACHES_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/caches");
    private String HOSTS_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/hosts");


    private CacheClusterMeta cacheCluster;

    public void init() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.newClient(zookeeperConnectionUrl, retryPolicy);
        zkClient.start();

        cacheCluster = getCacheClusterMeta();
    }

    /**
     * cache_cluster
     * ├── caches
     * │   ├── cache1
     * │   │   ├── subcache1
     * │   │   │   ├── replication1
     * │   │   │   └── replication2
     * │   │   └── subcache2
     * │   └── cache2
     * └── hosts
     *
     * @throws Exception
     */
    private CacheClusterMeta doGetCacheClusterMeta() throws Exception {
        CacheClusterMeta cacheClusterMeta = new CacheClusterMeta();

        CacheClusterZnode cacheClusterZnode = JsonUtils.fromJson(
                new String(zkClient.getData().forPath(CACHE_CLUSTER_PATH), Charsets.UTF_8),
                CacheClusterZnode.class);

        /**
         * restore cluster version
         */
        cacheClusterMeta.setVersion(cacheClusterZnode.getVersion());

        /**
         * restore hosts
         */
        List<Host> hosts = new ArrayList<>();
        List<String> hostsPath = zkClient.getChildren().forPath(HOSTS_PATH);
        for (String hostPath : hostsPath) {
            Host host = JsonUtils.fromJson(
                    new String(zkClient.getData().forPath(hostPath), Charsets.UTF_8),
                    Host.class);
            hosts.add(host.getId(), host);
        }
        cacheClusterMeta.setHosts(hosts);


        /**
         * restore caches
         */
        List<CacheMeta> caches = new ArrayList<>();
        List<String> cachesPath = zkClient.getChildren().forPath(CACHES_PATH);
        for (String cachePath : cachesPath) {
            CacheZnode cacheZnode = JsonUtils.fromJson(
                    new String(zkClient.getData().forPath(cachePath), Charsets.UTF_8),
                    CacheZnode.class);

            List<SubCacheMeta> subCaches = new ArrayList<>();
            List<String> subCachesPath = zkClient.getChildren().forPath(cachePath);
            for (String subCachePath : subCachesPath) {
                SubCacheZnode subCacheZnode = JsonUtils.fromJson(
                        new String(zkClient.getData().forPath(subCachePath), Charsets.UTF_8),
                        SubCacheZnode.class);
                SubCacheMeta subCacheMeta = new SubCacheMeta();
                subCacheMeta.setId(subCacheZnode.getId());
                List<ReplicationMeta> replications = new ArrayList<>();
                List<String> replicationsPath = zkClient.getChildren().forPath(subCachePath);
                for (String replicationPath : replicationsPath) {
                    ReplicationZnode replicationZnode = JsonUtils.fromJson(
                            new String(zkClient.getData().forPath(replicationPath), Charsets.UTF_8),
                            ReplicationZnode.class);
                    ReplicationMeta replicationMeta = new ReplicationMeta();
                    replicationMeta.setId(replicationZnode.getId());
                    replicationMeta.setHost(hosts.get(replicationZnode.getHostId()));
                    replications.add(replicationMeta);
                }
                subCacheMeta.setReplicationMetas(replications);
                subCaches.add(subCacheMeta);
            }

            CacheMeta cacheMeta = new CacheMeta();
            cacheMeta.setVersion(cacheZnode.getVersion());
            cacheMeta.setName(cacheZnode.getName());
            cacheMeta.setPartitionsPerSubCache(cacheZnode.getPartitionsPerSubCache());
            cacheMeta.setEntryClassName(cacheZnode.getEntryClassName());
            cacheMeta.setEntryClass(ClassUtils.loadClass(cacheZnode.getEntryClassName(), ICacheEntry.class));
            cacheMeta.setSubCacheMetas(subCaches);
            int partitions = cacheZnode.getPartitionsPerSubCache() * subCachesPath.size();
            cacheMeta.setPartitioner(new HashPartitioner(partitions));

            caches.add(cacheMeta);
        }
        cacheClusterMeta.setCaches(caches);
        return cacheClusterMeta;
    }

    public CacheClusterMeta getCacheClusterMeta() throws Exception {
        InterProcessReadWriteLock lock = new InterProcessReadWriteLock(zkClient, CACHE_CLUSTER_PATH);
        lock.readLock().acquire();
        try {
            return doGetCacheClusterMeta();
        } finally {
            // don't worry, if zookeeper connection closed, the lock will release by zookeeper.
            lock.readLock().release();
        }
    }


    public CacheMeta getCache(String name) {
        for (CacheMeta cache : cacheCluster.getCaches()) {
            if (cache.getName().equals(name)) {
                return cache;
            }
        }
        return null;
    }

    private void createCacheInCluster(CacheMeta cacheMeta) {
        HttpClient httpClient = new HttpClient();
        List<SubCacheMeta> subCaches = cacheMeta.getSubCacheMetas();
        for (SubCacheMeta subCache : subCaches) {
            ReplicationMeta meta = subCache.getReplicationMetas().get(0);
            Host host = meta.getHost();
            String url = String.format("http://%s:%d", host.getHost(), host.getPort());
            String path = "/subcache";
            RestCreateCacheResponse createCacheResponse =
                    httpClient.post(url, path, new RestCreateCacheRequest(), RestCreateCacheResponse.class);
            if (createCacheResponse == null) {
                throw new RuntimeException(String.format("failed to create subcache[%s]", JsonUtils.toJson(subCache)));
            }
        }
    }

    private void doCreateCache(CacheMeta cacheMeta) throws Exception {
        String name = cacheMeta.getName();
        String cacheZkPath = StringUtils.join(CACHE_CLUSTER_PATH, "/", name);
        CacheZnode cacheZnode = new CacheZnode();
        cacheZnode.setName(cacheMeta.getName());
        cacheZnode.setVersion(cacheMeta.getVersion());
        cacheZnode.setEntryClassName(cacheMeta.getEntryClassName());
        cacheZnode.setPartitionsPerSubCache(cacheMeta.getPartitionsPerSubCache());
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

    public void createCache(String name, String entryClassName, int subCaches, int partitionsPerSubCache)
            throws Exception {
        InterProcessMutex lock = new InterProcessMutex(zkClient, CACHE_CLUSTER_PATH);
        lock.acquire();
        try {
            CacheClusterMeta cacheClusterMeta = getCacheClusterMeta();
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

            List<SubCacheMeta> subCacheMetas = new ArrayList<>(subCaches);
            for (int i = 0; i < subCaches; i++) {
                SubCacheMeta subCacheMeta = new SubCacheMeta();
                subCacheMeta.setId(i);
                subCacheMeta.setZkNodeName(String.format("subcache%d", i));
                ReplicationMeta replicationMeta = new ReplicationMeta();
                replicationMeta.setHost(hosts.get(i % hosts.size()));
                List<ReplicationMeta> replicationMetas = new ArrayList<>();
                replicationMetas.add(replicationMeta);
                subCacheMeta.setReplicationMetas(replicationMetas);
                subCacheMetas.add(subCacheMeta);
            }
            cacheMeta.setSubCacheMetas(subCacheMetas);
            cacheMeta.setPartitionsPerSubCache(partitionsPerSubCache);


            createCacheInCluster(cacheMeta);

            doCreateCache(cacheMeta);


        } finally {
            lock.release();
        }
    }
}
