package com.maxent.dscache.api.rest.controller;

import com.maxent.dscache.api.rest.request.RestCreateCacheRequest;
import com.maxent.dscache.api.rest.response.RestCreateCacheResponse;
import com.maxent.dscache.api.rest.tools.RestHelper;
import com.maxent.dscache.cache.CacheManager;
import com.maxent.dscache.cache.exceptions.CacheExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

/**
 * Created by alain on 16/8/17.
 */
@Path("/cache")
public class CacheController {
    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    private CacheManager cacheManager = new CacheManager();

    @POST
    public RestCreateCacheResponse create(@Context final HttpServletResponse httpServletResponse,
                                          final RestCreateCacheRequest request) {

        try {
            cacheManager.addCache(
                    request.getName(),
                    request.getProvider(),
                    request.getPartitions(),
                    request.getBlocks_per_partition(),
                    request.getBlock_capacity());
            return RestHelper.doResponse(
                    httpServletResponse,
                    HttpServletResponse.SC_CREATED,
                    new RestCreateCacheResponse(
                            request.getName(),
                            request.getProvider(),
                            request.getPartitions(),
                            request.getBlocks_per_partition(),
                            request.getBlock_capacity()));
        } catch (CacheExistException e) {
            String errInfo = String.format("cache[%s] already exist", request.getName());
            logger.error(errInfo, e);
            return RestHelper.doResponse(
                    httpServletResponse,
                    HttpServletResponse.SC_OK,
                    RestHelper.createErrorResponse(RestCreateCacheResponse.class, errInfo));
        } catch (Exception e) {
            String errInfo = String.format(
                    "failed to create cache[%s], exception[%s]",
                    request.getName(), e.getMessage());
            logger.error(errInfo, e);
            return RestHelper.doResponse(
                    httpServletResponse,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    RestHelper.createErrorResponse(RestCreateCacheResponse.class, errInfo));
        }
    }

    @POST
    @Path("{cache_id}/save")
    public String save(@Context final HttpServletResponse httpServletResponse,
                       final String request) {
        return "save " + request;
    }

    @POST
    @Path("/match")
    public String match(@Context final HttpServletResponse httpServletResponse,
                        final String request) {
        return "match " + request;
    }
}
