package com.maxent.dscache.api.rest.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by alain on 16/8/18.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RestAddHostsResponse extends RestCommonResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
