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
 * Created by alain on 16/9/11.
 */
public class CacheClusterViewer {
    private static final Logger logger = LoggerFactory.getLogger(CacheClusterService.class);

    private String zookeeperConnectionUrl = "127.0.0.1:2181";

    private CuratorFramework zkClient;

    private final String CACHE_CLUSTER_PATH = "/cache_cluster";
    private final String CACHES_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/caches");
    private final String HOSTS_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/hosts");
    private final String CACHE_GROUPS_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/cache_groups");

    private final InterProcessReadWriteLock clusterGlobalLock;

    private volatile CacheClusterMeta cacheCluster;


    public CacheClusterViewer() throws RuntimeException {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            zkClient = CuratorFrameworkFactory.newClient(zookeeperConnectionUrl, retryPolicy);
            zkClient.start();

            clusterGlobalLock = new InterProcessReadWriteLock(zkClient, CACHE_CLUSTER_PATH);

            cacheCluster = doGetCacheClusterMeta();

            // TODO: add cluster change watcher
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        zkClient.close();
    }


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
            String fullHostPath = StringUtils.join(HOSTS_PATH, "/", hostPath);
            Host host = JsonUtils.fromJson(
                    new String(zkClient.getData().forPath(fullHostPath), Charsets.UTF_8),
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
            String fullCachePath = StringUtils.join(CACHES_PATH, "/", cachePath);
            CacheZnode cacheZnode = JsonUtils.fromJson(
                    new String(zkClient.getData().forPath(fullCachePath), Charsets.UTF_8),
                    CacheZnode.class);

            List<SubCacheMeta> subCaches = new ArrayList<>();
            List<String> subCachesPath = zkClient.getChildren().forPath(fullCachePath);
            for (String subCachePath : subCachesPath) {
                String fullSubCachePath = StringUtils.join(fullCachePath, "/", subCachePath);
                SubCacheZnode subCacheZnode = JsonUtils.fromJson(
                        new String(zkClient.getData().forPath(fullSubCachePath), Charsets.UTF_8),
                        SubCacheZnode.class);
                SubCacheMeta subCacheMeta = new SubCacheMeta();
                subCacheMeta.setId(subCacheZnode.getId());
                List<ReplicationMeta> replications = new ArrayList<>();
                List<String> replicationsPath = zkClient.getChildren().forPath(fullSubCachePath);
                for (String replicationPath : replicationsPath) {
                    String fullReplicationPath = StringUtils.join(fullSubCachePath, "/", replicationPath);
                    ReplicationZnode replicationZnode = JsonUtils.fromJson(
                            new String(zkClient.getData().forPath(fullReplicationPath), Charsets.UTF_8),
                            ReplicationZnode.class);
                    ReplicationMeta replicationMeta = new ReplicationMeta();
                    replicationMeta.setId(replicationZnode.getId());
                    replicationMeta.setHost(hosts.get(replicationZnode.getHostId()));
                    replicationMeta.setZkNodeName(replicationPath);
                    replications.add(replicationMeta);
                }
                subCacheMeta.setReplicationMetas(replications);
                subCaches.add(subCacheMeta);
            }

            CacheMeta cacheMeta = new CacheMeta();
            cacheMeta.setVersion(cacheZnode.getVersion());
            cacheMeta.setName(cacheZnode.getName());
            cacheMeta.setPartitionsPerSubCache(cacheZnode.getPartitionsPerSubCache());
            cacheMeta.setBlocksPerPartition(cacheZnode.getBlocksPerPartition());
            cacheMeta.setBlockCapacity(cacheZnode.getBlockCapacity());
            cacheMeta.setEntryClassName(cacheZnode.getEntryClassName());
            cacheMeta.setEntryClass(ClassUtils.loadClass(cacheZnode.getEntryClassName(), ICacheEntry.class));
            cacheMeta.setSubCacheMetas(subCaches);
            int partitions = cacheZnode.getPartitionsPerSubCache() * subCachesPath.size();
            cacheMeta.setPartitioner(new HashPartitioner(partitions));

            caches.add(cacheMeta);
        }
        cacheClusterMeta.setCaches(caches);

        /**
         * restore cache groups
         */
        List<CacheGroupMeta> cacheGroupMetas = new ArrayList<>();
        List<String> cacheGroups = zkClient.getChildren().forPath(CACHE_GROUPS_PATH);
        for (String cacheGroupPath : cacheGroups) {
            String fullCacheGroupPath = StringUtils.join(CACHE_GROUPS_PATH, "/", cacheGroupPath);
            CacheGroupZnode cacheGroupZnode = JsonUtils.fromJson(
                    new String(zkClient.getData().forPath(fullCacheGroupPath), Charsets.UTF_8),
                    CacheGroupZnode.class);

            List<CacheMeta> cachesInGroup = new ArrayList<>();
            List<String> cacheNames = zkClient.getChildren().forPath(fullCacheGroupPath);
            for (String cacheName : cacheNames) {
                cachesInGroup.add(getCache(cacheClusterMeta, cacheName));
            }

            CacheGroupMeta cacheGroupMeta = new CacheGroupMeta();
            cacheGroupMeta.setCacheGroupName(cacheGroupZnode.getCacheGroupName());
            cacheGroupMeta.setEntryClassName(cacheGroupZnode.getEntryClassName());
            cacheGroupMeta.setSubCachesPerCache(cacheGroupZnode.getSubCachesPerCache());
            cacheGroupMeta.setCacheGroupCapacity(cacheGroupZnode.getCacheGroupCapacity());
            cacheGroupMeta.setPartitionsPerSubCache(cacheGroupZnode.getPartitionsPerSubCache());
            cacheGroupMeta.setBlocksPerPartition(cacheGroupZnode.getBlocksPerPartition());
            cacheGroupMeta.setBlockCapacity(cacheGroupZnode.getBlockCapacity());
            cacheGroupMeta.setCurrentCachesNumber(cacheGroupZnode.getCurrentCachesNumber());
            cacheGroupMeta.setLastCachesNumber(cacheGroupZnode.getLastCachesNumber());
            cacheGroupMeta.setCacheMetas(cachesInGroup);
            cacheGroupMetas.add(cacheGroupMeta);
        }
        cacheClusterMeta.setCacheGroups(cacheGroupMetas);
        return cacheClusterMeta;
    }

    public CacheClusterMeta getCacheClusterMeta() throws Exception {
        return this.cacheCluster;
    }

    private CacheMeta getCache(CacheClusterMeta cacheCluster, String name) {
        for (CacheMeta cache : cacheCluster.getCaches()) {
            if (cache.getName().equals(name)) {
                return cache;
            }
        }
        return null;
    }

    public CacheMeta getCache(String name) {
        return getCache(cacheCluster, name);
    }

    public List<Host> getHosts() {
        return cacheCluster.getHosts();
    }

    private CacheGroupMeta getCacheGroupMeta(CacheClusterMeta cacheCluster, String name) {
        for (CacheGroupMeta cacheGroup : cacheCluster.getCacheGroups()) {
            if (cacheGroup.getCacheGroupName().equals(name)) {
                return cacheGroup;
            }
        }
        return null;
    }

    public CacheGroupMeta getCacheGroupMeta(String name) {
        return getCacheGroupMeta(cacheCluster, name);
    }

}
