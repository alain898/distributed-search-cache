package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/9/26.
 */
public class CacheLockException extends CacheException{
    public CacheLockException() {
    }

    public CacheLockException(String message) {
        super(message);
    }

    public CacheLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheLockException(Throwable cause) {
        super(cause);
    }

    public CacheLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
