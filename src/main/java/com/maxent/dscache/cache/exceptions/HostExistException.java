package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/9/27.
 */
public class HostExistException extends CacheException{
    public HostExistException() {
    }

    public HostExistException(String message) {
        super(message);
    }

    public HostExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public HostExistException(Throwable cause) {
        super(cause);
    }

    public HostExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
