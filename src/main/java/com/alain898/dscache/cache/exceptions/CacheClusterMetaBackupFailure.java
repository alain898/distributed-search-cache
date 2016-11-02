package com.alain898.dscache.cache.exceptions;

/**
 * Created by alain on 16/9/26.
 */
public class CacheClusterMetaBackupFailure extends CacheException{
    public CacheClusterMetaBackupFailure() {
    }

    public CacheClusterMetaBackupFailure(String message) {
        super(message);
    }

    public CacheClusterMetaBackupFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheClusterMetaBackupFailure(Throwable cause) {
        super(cause);
    }

    public CacheClusterMetaBackupFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
