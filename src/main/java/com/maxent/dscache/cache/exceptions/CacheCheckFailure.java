package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/27.
 */
public class CacheCheckFailure extends CacheException{
    public CacheCheckFailure() {
    }

    public CacheCheckFailure(String message) {
        super(message);
    }

    public CacheCheckFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheCheckFailure(Throwable cause) {
        super(cause);
    }

    public CacheCheckFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
