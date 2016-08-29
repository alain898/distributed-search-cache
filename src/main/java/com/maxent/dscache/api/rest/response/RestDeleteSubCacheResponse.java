package com.maxent.dscache.api.rest.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by alain on 16/8/29.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RestDeleteSubCacheResponse extends RestCommonResponse {
    private String message;

    public RestDeleteSubCacheResponse() {
    }

    public RestDeleteSubCacheResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
