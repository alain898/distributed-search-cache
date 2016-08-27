package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/27.
 */
public class CacheCreateFailureException extends CacheException{
    public CacheCreateFailureException() {
    }

    public CacheCreateFailureException(String message) {
        super(message);
    }

    public CacheCreateFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheCreateFailureException(Throwable cause) {
        super(cause);
    }

    public CacheCreateFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
