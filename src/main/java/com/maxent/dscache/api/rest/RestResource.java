package com.maxent.dscache.api.rest;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by alain on 16/8/16.
 */
public class RestResource extends ResourceConfig {
    public RestResource() {
        packages(RestResource.class.getPackage().getName());
    }

}
