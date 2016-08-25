package com.maxent.dscache.cache;

import com.maxent.dscache.common.partitioner.IPartitioner;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alain on 16/8/20.
 */
public class CacheClusterManager {
    private static final Logger logger = LoggerFactory.getLogger(CacheClusterManager.class);

    private String zookeepers;

    private String zookeeperConnectionUrl = "";

    private CuratorFramework zkClient;

    private String CACHE_CLUSTER_PATH = "/cache_cluster";

    public void init() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.newClient(zookeeperConnectionUrl, retryPolicy);
        zkClient.start();

        InterProcessReadWriteLock lock = new InterProcessReadWriteLock(zkClient, CACHE_CLUSTER_PATH);
        lock.readLock().acquire();
        try {

        } finally {
            lock.readLock().release();
        }
    }

    public IPartitioner getPartitioner(String cacheGroupName) {
        return null;
    }

}
