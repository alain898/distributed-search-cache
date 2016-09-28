package com.maxent.dscache.api.rest.controller;

import com.maxent.dscache.api.rest.request.*;
import com.maxent.dscache.api.rest.response.*;
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

    CacheClusterService cacheClusterService = CacheClusterService.getInstance();

    @POST
    @Path("/hosts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestAddHostsResponse addHosts(@Context final HttpServletResponse httpServletResponse,
                                         final RestAddHostsRequest request) {
        try {
            List<Host> newHosts = HostUtils.parseHosts(request.getHosts());
            cacheClusterService.addHosts(newHosts, true);
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
    @Path("/cache/create")
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
                    request.getBlockCapacity(),
                    true);
            RestCreateCacheResponse response = new RestCreateCacheResponse();
            response.setName(request.getName());
            return response;
        } catch (Exception e) {
            logger.error("createCache failed", e);
            return RestHelper.createErrorResponse(RestCreateCacheResponse.class, "createCache failed");
        }
    }

    @POST
    @Path("/cache/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestDeleteCacheResponse deleteCache(@Context final HttpServletResponse httpServletResponse,
                                               final RestDeleteCacheRequest request) {

        try {
            cacheClusterService.deleteCache(request.getCacheName());
            RestDeleteCacheResponse response = new RestDeleteCacheResponse();
            response.setMessage("success");
            return response;
        } catch (Exception e) {
            logger.error("createCache failed", e);
            return RestHelper.createErrorResponse(RestDeleteCacheResponse.class, "createCache failed");
        }
    }

    @POST
    @Path("/cache_group/create")
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
    @Path("/cache_group/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestUpdateCacheGroupResponse updateCacheGroup(@Context final HttpServletResponse httpServletResponse,
                                                         final RestUpdateCacheGroupRequest request) {

        try {
            cacheClusterService.updateCacheGroup(
                    request.getCacheGroupName(),
                    request.getAddedCaches());
            RestUpdateCacheGroupResponse response = new RestUpdateCacheGroupResponse();
            response.setMessage("success");
            return response;
        } catch (Exception e) {
            logger.error("updateCache failed", e);
            return RestHelper.createErrorResponse(RestUpdateCacheGroupResponse.class, "add cache failed");
        }
    }

    @POST
    @Path("/cache_group/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestDeleteCacheGroupResponse deleteCacheGroup(@Context final HttpServletResponse httpServletResponse,
                                                         final RestDeleteCacheGroupRequest request) {

        try {
            cacheClusterService.deleteCacheGroup(request.getCacheGroupName());
            RestDeleteCacheGroupResponse response = new RestDeleteCacheGroupResponse();
            response.setMessage("success");
            return response;
        } catch (Exception e) {
            logger.error("createCache failed", e);
            return RestHelper.createErrorResponse(RestDeleteCacheGroupResponse.class, "add cache failed");
        }
    }
}
