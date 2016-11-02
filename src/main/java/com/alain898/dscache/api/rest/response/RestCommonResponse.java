package com.alain898.dscache.api.rest.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by alain on 16/8/18.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public abstract class RestCommonResponse {
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
