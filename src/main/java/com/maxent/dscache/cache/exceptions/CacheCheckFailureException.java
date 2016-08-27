package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/27.
 */
public class CacheCheckFailureException extends CacheException{
    public CacheCheckFailureException() {
    }

    public CacheCheckFailureException(String message) {
        super(message);
    }

    public CacheCheckFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheCheckFailureException(Throwable cause) {
        super(cause);
    }

    public CacheCheckFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
