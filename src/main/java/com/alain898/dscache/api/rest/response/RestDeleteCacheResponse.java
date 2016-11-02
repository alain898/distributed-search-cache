package com.alain898.dscache.api.rest.response;

/**
 * Created by alain on 16/9/10.
 */
public class RestDeleteCacheResponse extends RestCommonResponse{
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
