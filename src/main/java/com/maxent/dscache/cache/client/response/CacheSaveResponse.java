package com.maxent.dscache.cache.client.response;

/**
 * Created by alain on 16/9/16.
 */
public class CacheSaveResponse {
    private String message;

    public CacheSaveResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
