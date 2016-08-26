package com.maxent.dscache.api.rest.controller;

import com.maxent.dscache.api.rest.request.RestAddHostsRequest;
import com.maxent.dscache.api.rest.request.RestCreateCacheRequest;
import com.maxent.dscache.api.rest.response.RestAddHostsResponse;
import com.maxent.dscache.api.rest.response.RestCreateCacheResponse;
import com.maxent.dscache.api.rest.tools.RestHelper;
import com.maxent.dscache.cache.CacheClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by alain on 16/8/18.
 */
@Singleton
@Path("/management")
public class ManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ManagementController.class);

    CacheClusterManager cacheClusterManager = new CacheClusterManager();

    @POST
    @Path("/hosts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestAddHostsResponse addHosts(@Context final HttpServletResponse httpServletResponse,
                                         final RestAddHostsRequest request) {
        RestAddHostsResponse response = new RestAddHostsResponse();
        response.setMessage("success");
        return response;
    }

    @POST
    @Path("/caches")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestCreateCacheResponse addCaches(@Context final HttpServletResponse httpServletResponse,
                                             final RestCreateCacheRequest request) {

        try {
            cacheClusterManager.createCache(
                    request.getName(),
                    request.getEntryClassName(),
                    request.getSubCaches(),
                    request.getPartitionsPerSubCache());
            RestCreateCacheResponse response = new RestCreateCacheResponse();
            response.setName(request.getName());
            return response;
        } catch (Exception e) {
            logger.error("createCache failed", e);
            return RestHelper.createErrorResponse(RestCreateCacheResponse.class, "createCache failed");
        }
    }
}
