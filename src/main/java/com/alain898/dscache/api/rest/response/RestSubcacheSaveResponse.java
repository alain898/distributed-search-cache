package com.alain898.dscache.api.rest.response;

/**
 * Created by alain on 16/8/30.
 */
public class RestSubcacheSaveResponse extends RestCommonResponse {
    private String message;

    public RestSubcacheSaveResponse() {
    }

    public RestSubcacheSaveResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
