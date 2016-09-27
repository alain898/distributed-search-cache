package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/9/27.
 */
public class HostAddFailure extends CacheException{
    public HostAddFailure() {
    }

    public HostAddFailure(String message) {
        super(message);
    }

    public HostAddFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public HostAddFailure(Throwable cause) {
        super(cause);
    }

    public HostAddFailure(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
