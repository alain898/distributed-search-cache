package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/9/6.
 */
public class CacheGroupCreateFailureException extends CacheException{
    public CacheGroupCreateFailureException() {
    }

    public CacheGroupCreateFailureException(String message) {
        super(message);
    }

    public CacheGroupCreateFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheGroupCreateFailureException(Throwable cause) {
        super(cause);
    }

    public CacheGroupCreateFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
