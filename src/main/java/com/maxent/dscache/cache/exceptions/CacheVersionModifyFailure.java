package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/9/26.
 */
public class CacheVersionModifyFailure extends Exception{
    public CacheVersionModifyFailure() {
    }

    public CacheVersionModifyFailure(String message) {
        super(message);
    }

    public CacheVersionModifyFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheVersionModifyFailure(Throwable cause) {
        super(cause);
    }

    public CacheVersionModifyFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
