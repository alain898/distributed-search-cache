package com.maxent.dscache.api.rest.response;

/**
 * Created by alain on 16/8/30.
 */
public class RestSaveEntryResponse extends RestCommonResponse {
    private String message;

    public RestSaveEntryResponse() {
    }

    public RestSaveEntryResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
