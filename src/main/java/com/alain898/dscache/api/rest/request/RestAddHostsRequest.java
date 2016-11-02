package com.alain898.dscache.api.rest.request;

import java.util.List;

/**
 * Created by alain on 16/8/18.
 */
public class RestAddHostsRequest {
    private List<String> hosts;

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }
}
