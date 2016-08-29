package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/29.
 */
public class CacheDeleteFailureException extends CacheException {
    public CacheDeleteFailureException() {
    }

    public CacheDeleteFailureException(String message) {
        super(message);
    }

    public CacheDeleteFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheDeleteFailureException(Throwable cause) {
        super(cause);
    }

    public CacheDeleteFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
