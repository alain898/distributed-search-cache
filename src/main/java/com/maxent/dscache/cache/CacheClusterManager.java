package com.maxent.dscache.cache;

import com.google.common.base.Charsets;
import com.maxent.dscache.common.partitioner.HashPartitioner;
import com.maxent.dscache.common.tools.ClassUtils;
import com.maxent.dscache.common.tools.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
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

    private String zookeeperConnectionUrl = "";

    private CuratorFramework zkClient;

    private String CACHE_CLUSTER_PATH = "/cache_cluster";
    private String CACHES_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/caches");
    private String HOSTS_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/hosts");

    private CacheClusterMeta cacheCluster;


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
    public void init() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.newClient(zookeeperConnectionUrl, retryPolicy);
        zkClient.start();

        InterProcessReadWriteLock lock = new InterProcessReadWriteLock(zkClient, CACHE_CLUSTER_PATH);
        lock.readLock().acquire();
        try {
            cacheCluster = new CacheClusterMeta();

            CacheClusterZnode cacheClusterZnode = JsonUtils.fromJson(
                    new String(zkClient.getData().forPath(CACHE_CLUSTER_PATH), Charsets.UTF_8),
                    CacheClusterZnode.class);

            /**
             * restore cluster version
             */
            cacheCluster.setVersion(cacheClusterZnode.getVersion());

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
            cacheCluster.setHosts(hosts);


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
            cacheCluster.setCaches(caches);

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
}
