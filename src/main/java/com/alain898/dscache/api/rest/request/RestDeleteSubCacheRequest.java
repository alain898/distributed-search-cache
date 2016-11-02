package com.alain898.dscache.api.rest.request;

/**
 * Created by alain on 16/8/29.
 */
public class RestDeleteSubCacheRequest {
    private String name;
    private String subCacheId;

    public RestDeleteSubCacheRequest() {
    }

    public RestDeleteSubCacheRequest(String name, String subCacheId) {
        this.name = name;
        this.subCacheId = subCacheId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubCacheId() {
        return subCacheId;
    }

    public void setSubCacheId(String subCacheId) {
        this.subCacheId = subCacheId;
    }
}
