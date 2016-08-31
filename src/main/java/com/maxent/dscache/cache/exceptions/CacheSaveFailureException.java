package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/30.
 */
public class CacheSaveFailureException extends CacheException{
    public CacheSaveFailureException() {
    }

    public CacheSaveFailureException(String message) {
        super(message);
    }

    public CacheSaveFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheSaveFailureException(Throwable cause) {
        super(cause);
    }

    public CacheSaveFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
