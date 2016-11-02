package com.alain898.dscache.cache.client.response;

/**
 * Created by alain on 16/9/18.
 */
public class CacheGroupUpdateResponse {
    private String message;

    public CacheGroupUpdateResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
