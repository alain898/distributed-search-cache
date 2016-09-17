package com.maxent.dscache.cache;

/**
 * Created by alain on 16/9/17.
 */
public class CacheClusterViewerFactory {
    private static volatile CacheClusterViewer cacheClusterViewer = null;

    /**
     * this function will lock the cache cluster to guarantee the integrity of CacheClusterMeta
     * in CacheClusterViewer, so must not call it during the cluster is locked.
     * <p>
     * on cache cluster servers, this function should be called before starting cache services to
     * make sure the deadlock dose not happen.
     * <p>
     * on cache cluster clients, it's recommend this method called only once before getCacheClusterViewer
     * called, then all calls of getCacheClusterViewer will return the same CacheClusterViewer instance.
     */
    public static void configure() {
        synchronized (CacheClusterViewerFactory.class) {
            cacheClusterViewer = new CacheClusterViewer();
        }
    }

    public static CacheClusterViewer getCacheClusterViewer() {
        if (cacheClusterViewer == null) {
            throw new RuntimeException(
                    "CacheClusterViewerFactory not configured, please configure before using it.");
        }
        return cacheClusterViewer;
    }
}
