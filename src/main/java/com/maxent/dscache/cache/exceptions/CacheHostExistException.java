package com.maxent.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/29.
 */
public class CacheHostExistException extends CacheException{
    public CacheHostExistException() {
    }

    public CacheHostExistException(String message) {
        super(message);
    }

    public CacheHostExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheHostExistException(Throwable cause) {
        super(cause);
    }

    public CacheHostExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
