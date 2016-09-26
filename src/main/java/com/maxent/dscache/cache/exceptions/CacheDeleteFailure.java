package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/29.
 */
public class CacheDeleteFailure extends CacheException {
    public CacheDeleteFailure() {
    }

    public CacheDeleteFailure(String message) {
        super(message);
    }

    public CacheDeleteFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheDeleteFailure(Throwable cause) {
        super(cause);
    }

    public CacheDeleteFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
