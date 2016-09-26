package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/27.
 */
public class CacheInitializeFailure extends CacheException {
    public CacheInitializeFailure() {
    }

    public CacheInitializeFailure(String message) {
        super(message);
    }

    public CacheInitializeFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheInitializeFailure(Throwable cause) {
        super(cause);
    }

    public CacheInitializeFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
