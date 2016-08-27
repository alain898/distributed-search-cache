package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/27.
 */
public class CacheMatchFailureException extends CacheException{
    public CacheMatchFailureException() {
    }

    public CacheMatchFailureException(String message) {
        super(message);
    }

    public CacheMatchFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheMatchFailureException(Throwable cause) {
        super(cause);
    }

    public CacheMatchFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
