package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/9/6.
 */
public class CacheGroupCreateFailure extends CacheException{
    public CacheGroupCreateFailure() {
    }

    public CacheGroupCreateFailure(String message) {
        super(message);
    }

    public CacheGroupCreateFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheGroupCreateFailure(Throwable cause) {
        super(cause);
    }

    public CacheGroupCreateFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
