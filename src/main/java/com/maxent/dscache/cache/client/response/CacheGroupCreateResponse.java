package com.maxent.dscache.cache.client.response;

/**
 * Created by alain on 16/9/10.
 */
public class CacheGroupCreateResponse {
    private String message;

    public CacheGroupCreateResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
