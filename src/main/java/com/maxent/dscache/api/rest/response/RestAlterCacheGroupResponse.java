package com.maxent.dscache.api.rest.response;

/**
 * Created by alain on 16/9/8.
 */
public class RestAlterCacheGroupResponse extends RestCommonResponse{
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
