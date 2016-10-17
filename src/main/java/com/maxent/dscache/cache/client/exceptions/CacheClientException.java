package com.maxent.dscache.cache.client.exceptions;

/**
 * Created by alain on 16/10/17.
 */
public class CacheClientException extends RuntimeException{
    public CacheClientException() {
    }

    public CacheClientException(String message) {
        super(message);
    }

    public CacheClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheClientException(Throwable cause) {
        super(cause);
    }

    public CacheClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
