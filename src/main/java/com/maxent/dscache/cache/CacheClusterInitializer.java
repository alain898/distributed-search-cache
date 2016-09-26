package com.maxent.dscache.cache;

import com.google.common.base.Charsets;
import com.maxent.dscache.cache.exceptions.CacheCheckFailure;
import com.maxent.dscache.cache.exceptions.CacheInitializeFailure;
import com.maxent.dscache.common.tools.JsonUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
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
public class CacheClusterInitializer {
    private static final Logger logger = LoggerFactory.getLogger(CacheClusterInitializer.class);

    private final String zookeeperConnectionUrl;

    private CuratorFramework zkClient;

    public CacheClusterInitializer() throws RuntimeException {
        this(ConfigFactory.load());
    }

    public CacheClusterInitializer(Config config) throws RuntimeException {
        try {
            this.zookeeperConnectionUrl = config.getString("zookeeper.connection_url");
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            zkClient = CuratorFrameworkFactory.newClient(zookeeperConnectionUrl, retryPolicy);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void initClusterIfNot() throws CacheCheckFailure, CacheInitializeFailure {
        try {
            zkClient.start();
            try {
                zkClient.create().forPath(Constants.CACHE_CLUSTER_PATH);
                CacheClusterZnode cacheClusterZnode = new CacheClusterZnode();
                cacheClusterZnode.setVersion(Constants.CACHE_CLUSTER_INITIAL_VERSION);
                zkClient.setData().forPath(Constants.CACHE_CLUSTER_PATH,
                        JsonUtils.toJson(cacheClusterZnode).getBytes(Charsets.UTF_8));
            } catch (KeeperException.NodeExistsException e) {
                logger.info(String.format("zookeeper node[%s] exist", Constants.CACHE_CLUSTER_PATH));
            }

            try {
                zkClient.create().forPath(Constants.CACHES_PATH);
            } catch (KeeperException.NodeExistsException e) {
                logger.info(String.format("zookeeper node[%s] exist", Constants.CACHES_PATH));
            }

            try {
                zkClient.create().forPath(Constants.HOSTS_PATH);
            } catch (KeeperException.NodeExistsException e) {
                logger.info(String.format("zookeeper node[%s] exist", Constants.HOSTS_PATH));
            }

            try {
                zkClient.create().forPath(Constants.CACHE_GROUPS_PATH);
            } catch (KeeperException.NodeExistsException e) {
                logger.info(String.format("zookeeper node[%s] exist", Constants.CACHE_GROUPS_PATH));
            }
            zkClient.close();
        } catch (Exception e) {
            throw new CacheInitializeFailure("failed to check cluster", e);
        }
    }
}
