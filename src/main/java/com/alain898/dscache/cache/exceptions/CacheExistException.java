package com.alain898.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/20.
 */
public class CacheExistException extends CacheException{
    public CacheExistException() {
    }

    public CacheExistException(String message) {
        super(message);
    }

    public CacheExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheExistException(Throwable cause) {
        super(cause);
    }

    public CacheExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
