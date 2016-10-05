package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/10/4.
 */
public class CacheClusterRestoreFailure extends Exception {
    public CacheClusterRestoreFailure() {
    }

    public CacheClusterRestoreFailure(String message) {
        super(message);
    }

    public CacheClusterRestoreFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheClusterRestoreFailure(Throwable cause) {
        super(cause);
    }

    public CacheClusterRestoreFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
