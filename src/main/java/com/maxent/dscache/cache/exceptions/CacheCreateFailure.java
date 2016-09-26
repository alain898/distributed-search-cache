package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/27.
 */
public class CacheCreateFailure extends CacheException{
    public CacheCreateFailure() {
    }

    public CacheCreateFailure(String message) {
        super(message);
    }

    public CacheCreateFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheCreateFailure(Throwable cause) {
        super(cause);
    }

    public CacheCreateFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
