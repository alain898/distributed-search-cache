package com.maxent.dscache.api.rest.controller;

import com.maxent.dscache.api.rest.request.RestCreateSubCacheRequest;
import com.maxent.dscache.api.rest.request.RestDeleteSubCacheRequest;
import com.maxent.dscache.api.rest.request.RestSaveEntryRequest;
import com.maxent.dscache.api.rest.request.RestSubCacheSearchRequest;
import com.maxent.dscache.api.rest.response.RestCreateSubCacheResponse;
import com.maxent.dscache.api.rest.response.RestDeleteSubCacheResponse;
import com.maxent.dscache.api.rest.response.RestSaveEntryResponse;
import com.maxent.dscache.api.rest.response.RestSubCacheSearchResponse;
import com.maxent.dscache.api.rest.tools.RestHelper;
import com.maxent.dscache.cache.ICacheEntry;
import com.maxent.dscache.cache.SubCacheService;
import com.maxent.dscache.cache.exceptions.CacheDeleteFailureException;
import com.maxent.dscache.cache.exceptions.CacheExistException;
import com.maxent.dscache.common.tools.JsonUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alain on 16/8/17.
 */
@Singleton
@Path("/subcache")
public class SubCacheController {
    private static final Logger logger = LoggerFactory.getLogger(SubCacheController.class);

    private SubCacheService subCacheService = new SubCacheService();

    @Context
    private HttpServletResponse httpServletResponse2;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestCreateSubCacheResponse create(@Context final HttpServletResponse httpServletResponse,
                                             final RestCreateSubCacheRequest request) {

        try {
            subCacheService.createSubCache(
                    request.getName(),
                    request.getEntryClassName(),
                    request.getSubCacheId(),
                    request.getPartitionsPerSubCache(),
                    request.getBlocksPerPartition(),
                    request.getBlockCapacity());

            return new RestCreateSubCacheResponse(
                    request.getName(),
                    request.getEntryClassName(),
                    request.getSubCacheId(),
                    request.getPartitionsPerSubCache(),
                    request.getBlocksPerPartition(),
                    request.getBlockCapacity());
        } catch (CacheExistException e) {
            String errInfo = String.format("cache[%s] already exist", request.getName());
            logger.warn(errInfo, e);
            return RestHelper.createErrorResponse(RestCreateSubCacheResponse.class, errInfo);
        } catch (Exception e) {
            String errInfo = String.format(
                    "failed to create cache[%s], exception[%s]",
                    request.getName(), e.getMessage());
            logger.error(errInfo, e);
            return RestHelper.createErrorResponse(RestCreateSubCacheResponse.class, errInfo);
        }
    }

    @POST
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestDeleteSubCacheResponse delete(@Context final HttpServletResponse httpServletResponse,
                                             final RestDeleteSubCacheRequest request) {

        try {
            subCacheService.deleteSubCache(
                    request.getName(),
                    request.getSubCacheId());
            return new RestDeleteSubCacheResponse("success");
        } catch (Exception e) {
            String errInfo = String.format(
                    "failed to delete cache[%s], exception[%s]",
                    request.getName(), e.getMessage());
            logger.error(errInfo, e);
            return RestHelper.createErrorResponse(RestDeleteSubCacheResponse.class, errInfo);
        }
    }

    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestSaveEntryResponse save(@Context final HttpServletResponse httpServletResponse,
                                      final RestSaveEntryRequest request) {
        try {
            subCacheService.saveEntry(
                    request.getCacheName(),
                    request.getSubCacheId(),
                    request.getQueryEntry());
            return new RestSaveEntryResponse("success");
        } catch (Exception e) {
            String errInfo = String.format(
                    "failed to save, request[%s], exception[%s]",
                    JsonUtils.toJson(request), e.getMessage());
            logger.error(errInfo, e);
            return RestHelper.createErrorResponse(RestSaveEntryResponse.class, errInfo);
        }
    }

    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestSubCacheSearchResponse search(@Context final HttpServletResponse httpServletResponse,
                                             final RestSubCacheSearchRequest request) {

        try {
            List<Pair<ICacheEntry, Double>> results = subCacheService.match(
                    request.getCacheName(),
                    request.getSubCacheId(),
                    request.getQueryEntry());

            List<ICacheEntry> entries = new ArrayList<>();
            List<Double> scores = new ArrayList<>();
            for (Pair<ICacheEntry, Double> r : results) {
                entries.add(r.getLeft());
                scores.add(r.getRight());
            }

            RestSubCacheSearchResponse response = new RestSubCacheSearchResponse();
            response.setEntries(entries);
            response.setScores(scores);

            return response;
        } catch (Exception e) {
            String errInfo = String.format(
                    "failed to create cache[%s], exception[%s]",
                    request.getCacheName(), e.getMessage());
            logger.error(errInfo, e);
            return RestHelper.createErrorResponse(RestSubCacheSearchResponse.class, errInfo);
        }
    }
}
