package com.maxent.dscache.cache;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alain on 16/9/17.
 */
public class CacheClusterViewerFactory {
    private static final Logger logger = LoggerFactory.getLogger(CacheClusterViewerFactory.class);

    private static volatile CacheClusterViewer cacheClusterViewer = null;

    /**
     * this function will lock the cache cluster to guarantee the integrity of CacheClusterMeta
     * in CacheClusterViewer, so must not call it during the cluster is locked.
     * <p>
     * on cache cluster servers, this function should be called before starting cache services to
     * make sure the deadlock dose not happen.
     * <p>
     * on cache cluster clients, it's recommend this method called only once before getInstance
     * called, then all calls of getInstance will return the same CacheClusterViewer instance.
     */
    public static void configure(Config config) throws Exception {
        if (config == null) {
            config = ConfigFactory.load();
        }

        String zookeeperConnectionUrl = config.getString("zookeeper.connection_url");
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework zkClient = CuratorFrameworkFactory.newClient(zookeeperConnectionUrl, retryPolicy);
        zkClient.start();

        InterProcessReadWriteLock clusterGlobalLock =
                new InterProcessReadWriteLock(zkClient, Constants.CACHE_CLUSTER_PATH);
        clusterGlobalLock.readLock().acquire();
        try {
            cacheClusterViewer = new CacheClusterViewer(config);
        } finally {
            try {
                clusterGlobalLock.readLock().release();
            } catch (Exception e) {
                logger.error(String.format("failed to release clusterGlobalLock on zknode[%s]",
                        Constants.CACHE_CLUSTER_PATH), e);
            }
        }

        zkClient.close();
    }

    public static CacheClusterViewer getCacheClusterViewer() {
        if (cacheClusterViewer == null) {
            throw new RuntimeException(
                    "CacheClusterViewerFactory not configured, please configure before using it.");
        }
        return cacheClusterViewer;
    }
}
