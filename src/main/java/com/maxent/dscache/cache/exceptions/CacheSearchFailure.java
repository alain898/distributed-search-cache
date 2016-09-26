package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/27.
 */
public class CacheSearchFailure extends CacheException{
    public CacheSearchFailure() {
    }

    public CacheSearchFailure(String message) {
        super(message);
    }

    public CacheSearchFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheSearchFailure(Throwable cause) {
        super(cause);
    }

    public CacheSearchFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
