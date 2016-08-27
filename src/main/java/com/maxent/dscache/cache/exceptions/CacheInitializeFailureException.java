package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/27.
 */
public class CacheInitializeFailureException extends CacheException {
    public CacheInitializeFailureException() {
    }

    public CacheInitializeFailureException(String message) {
        super(message);
    }

    public CacheInitializeFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheInitializeFailureException(Throwable cause) {
        super(cause);
    }

    public CacheInitializeFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
