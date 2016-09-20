package com.maxent.dscache.cache;

import com.google.common.base.Charsets;
import com.maxent.dscache.cache.exceptions.CacheCheckFailureException;
import com.maxent.dscache.cache.exceptions.CacheInitializeFailureException;
import com.maxent.dscache.common.tools.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alain on 16/9/20.
 */
public enum CacheClusterInitializer {
    INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(CacheClusterInitializer.class);

    private String zookeeperConnectionUrl = "127.0.0.1:2181";

    private CuratorFramework zkClient;

    private final String CACHE_CLUSTER_PATH = "/cache_cluster";
    private final String CACHES_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/caches");
    private final String HOSTS_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/hosts");
    private final String CACHE_GROUPS_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/cache_groups");

    private final String CACHE_CLUSTER_INITIAL_VERSION = "0";

    CacheClusterInitializer() throws RuntimeException {
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            zkClient = CuratorFrameworkFactory.newClient(zookeeperConnectionUrl, retryPolicy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void initClusterIfNot() throws CacheCheckFailureException, CacheInitializeFailureException {
        try {
            zkClient.start();
            try {
                zkClient.create().forPath(CACHE_CLUSTER_PATH);
                CacheClusterZnode cacheClusterZnode = new CacheClusterZnode();
                cacheClusterZnode.setVersion(CACHE_CLUSTER_INITIAL_VERSION);
                zkClient.setData().forPath(CACHE_CLUSTER_PATH,
                        JsonUtils.toJson(cacheClusterZnode).getBytes(Charsets.UTF_8));
            } catch (KeeperException.NodeExistsException e) {
                logger.info(String.format("zookeeper node[%s] exist", CACHE_CLUSTER_PATH));
            }

            try {
                zkClient.create().forPath(CACHES_PATH);
            } catch (KeeperException.NodeExistsException e) {
                logger.info(String.format("zookeeper node[%s] exist", CACHES_PATH));
            }

            try {
                zkClient.create().forPath(HOSTS_PATH);
            } catch (KeeperException.NodeExistsException e) {
                logger.info(String.format("zookeeper node[%s] exist", HOSTS_PATH));
            }

            try {
                zkClient.create().forPath(CACHE_GROUPS_PATH);
            } catch (KeeperException.NodeExistsException e) {
                logger.info(String.format("zookeeper node[%s] exist", CACHE_GROUPS_PATH));
            }
            zkClient.close();
        } catch (Exception e) {
            throw new CacheInitializeFailureException("failed to check cluster", e);
        }
    }
}
