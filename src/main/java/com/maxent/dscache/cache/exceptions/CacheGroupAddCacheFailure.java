package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/9/27.
 */
public class CacheGroupAddCacheFailure extends CacheException{
    public CacheGroupAddCacheFailure() {
    }

    public CacheGroupAddCacheFailure(String message) {
        super(message);
    }

    public CacheGroupAddCacheFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheGroupAddCacheFailure(Throwable cause) {
        super(cause);
    }

    public CacheGroupAddCacheFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
