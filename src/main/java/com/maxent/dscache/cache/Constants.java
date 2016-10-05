package com.maxent.dscache.cache;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by alain on 16/9/23.
 */
public class Constants {
    public static final String CACHE_CLUSTER_PATH = "/cache_cluster";
    public static final String CACHES_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/caches");
    public static final String HOSTS_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/hosts");
    public static final String CACHE_GROUPS_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/cache_groups");
    public static final String CACHE_BACKUP_PATH = StringUtils.join(CACHE_CLUSTER_PATH, "/backups");

    public static final String CACHE_CLUSTER_INITIAL_VERSION = "0";

    public static final String HOST_PATH_PREFIX = "host_";

    public static final long DEFAULT_START_VERSION = 0;
}
