package com.maxent.dscache.cache.client.response;

/**
 * Created by alain on 16/9/10.
 */
public class CacheDeleteResponse {
    private String message;

    public CacheDeleteResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
