package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/30.
 */
public class CacheSaveFailure extends CacheException{
    public CacheSaveFailure() {
    }

    public CacheSaveFailure(String message) {
        super(message);
    }

    public CacheSaveFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheSaveFailure(Throwable cause) {
        super(cause);
    }

    public CacheSaveFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
