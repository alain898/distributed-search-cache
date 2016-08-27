package com.maxent.dscache.api.rest.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by alain on 16/8/18.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RestAddHostsResponse extends RestCommonResponse {
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
