package com.alain898.dscache.cache.exceptions;

/**
 * Created by alain on 16/8/27.
 */
public class InvalidHostException extends RuntimeException {
    public InvalidHostException() {
    }

    public InvalidHostException(String message) {
        super(message);
    }

    public InvalidHostException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidHostException(Throwable cause) {
        super(cause);
    }

    public InvalidHostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
