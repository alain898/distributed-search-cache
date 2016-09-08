package com.maxent.dscache.api.rest.controller;

import com.maxent.dscache.api.rest.request.RestAddHostsRequest;
import com.maxent.dscache.api.rest.request.RestAlterCacheGroupRequest;
import com.maxent.dscache.api.rest.request.RestCreateCacheGroupRequest;
import com.maxent.dscache.api.rest.request.RestCreateCacheRequest;
import com.maxent.dscache.api.rest.response.RestAddHostsResponse;
import com.maxent.dscache.api.rest.response.RestAlterCacheGroupResponse;
import com.maxent.dscache.api.rest.response.RestCreateCacheGroupResponse;
import com.maxent.dscache.api.rest.response.RestCreateCacheResponse;
import com.maxent.dscache.api.rest.tools.RestHelper;
import com.maxent.dscache.cache.CacheClusterService;
import com.maxent.dscache.cache.Host;
import com.maxent.dscache.common.tools.HostUtils;
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
import java.util.List;

/**
 * Created by alain on 16/8/18.
 */
@Singleton
@Path("/management")
public class ManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ManagementController.class);

    CacheClusterService cacheClusterService = new CacheClusterService();

    @POST
    @Path("/hosts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestAddHostsResponse addHosts(@Context final HttpServletResponse httpServletResponse,
                                         final RestAddHostsRequest request) {
        try {
            List<Host> newHosts = HostUtils.parseHosts(request.getHosts());
            cacheClusterService.addHosts(newHosts);
            RestAddHostsResponse response = new RestAddHostsResponse();
            response.setResult("success");
            return response;
        } catch (Exception e) {
            logger.error("createCache failed", e);
            return RestHelper.createErrorResponse(RestAddHostsResponse.class,
                    "createCache failed: " + e.getMessage());
        }
    }

    @POST
    @Path("/caches")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestCreateCacheResponse createCache(@Context final HttpServletResponse httpServletResponse,
                                               final RestCreateCacheRequest request) {

        try {
            cacheClusterService.createCache(
                    request.getName(),
                    request.getEntryClassName(),
                    request.getSubCaches(),
                    request.getPartitionsPerSubCache(),
                    request.getBlocksPerPartition(),
                    request.getBlockCapacity());
            RestCreateCacheResponse response = new RestCreateCacheResponse();
            response.setName(request.getName());
            return response;
        } catch (Exception e) {
            logger.error("createCache failed", e);
            return RestHelper.createErrorResponse(RestCreateCacheResponse.class, "createCache failed");
        }
    }

    @POST
    @Path("/cache_groups")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestCreateCacheGroupResponse createCacheGroup(@Context final HttpServletResponse httpServletResponse,
                                                         final RestCreateCacheGroupRequest request) {

        try {
            cacheClusterService.createCacheGroup(
                    request.getCacheGroupName(),
                    request.getEntryClassName(),
                    request.getCacheGroupCapacity(),
                    request.getCachesNumber(),
                    request.getSubCachesPerCache(),
                    request.getPartitionsPerSubCache(),
                    request.getBlocksPerPartition(),
                    request.getBlockCapacity());
            RestCreateCacheGroupResponse response = new RestCreateCacheGroupResponse();
            response.setMessage("success");
            return response;
        } catch (Exception e) {
            logger.error("createCache failed", e);
            return RestHelper.createErrorResponse(RestCreateCacheGroupResponse.class, "create cache group failed");
        }
    }

    @POST
    @Path("/cache_groups/alter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestAlterCacheGroupResponse alterCacheGroup(@Context final HttpServletResponse httpServletResponse,
                                                       final RestAlterCacheGroupRequest request) {

        try {
            cacheClusterService.alterCacheGroup(
                    request.getCacheGroupName(),
                    request.getAddedCaches());
            RestAlterCacheGroupResponse response = new RestAlterCacheGroupResponse();
            response.setMessage("success");
            return response;
        } catch (Exception e) {
            logger.error("createCache failed", e);
            return RestHelper.createErrorResponse(RestAlterCacheGroupResponse.class, "add cache failed");
        }
    }
}
