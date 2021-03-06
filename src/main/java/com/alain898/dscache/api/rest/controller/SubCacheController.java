package com.alain898.dscache.api.rest.controller;

import com.alain898.dscache.api.rest.request.RestSubCacheSearchRequest;
import com.alain898.dscache.api.rest.request.RestSubcacheSaveRequest;
import com.alain898.dscache.api.rest.response.RestCreateSubCacheResponse;
import com.alain898.dscache.api.rest.response.RestSubcacheSaveResponse;
import com.alain898.dscache.api.rest.tools.RestHelper;
import com.alain898.dscache.api.rest.request.RestCreateSubCacheRequest;
import com.alain898.dscache.api.rest.request.RestDeleteSubCacheRequest;
import com.alain898.dscache.api.rest.response.RestDeleteSubCacheResponse;
import com.alain898.dscache.api.rest.response.RestSubCacheSearchResponse;
import com.alain898.dscache.cache.ICacheEntry;
import com.alain898.dscache.cache.SubCacheService;
import com.alain898.dscache.cache.exceptions.CacheExistException;
import com.alain898.dscache.common.tools.JsonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by alain on 16/8/17.
 */
@Singleton
@Path("/subcache")
public class SubCacheController {
    private static final Logger logger = LoggerFactory.getLogger(SubCacheController.class);

    private SubCacheService subCacheService = SubCacheService.getInstance();

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
                    request.getTotalPartitionNumber(),
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
    public RestSubcacheSaveResponse save(@Context final HttpServletResponse httpServletResponse,
                                         final RestSubcacheSaveRequest request) {
        try {
            subCacheService.saveEntry(
                    request.getCacheName(),
                    request.getSubCacheId(),
                    request.getQueryEntry());
            return new RestSubcacheSaveResponse("success");
        } catch (Exception e) {
            String errInfo = String.format(
                    "failed to save, request[%s], exception[%s]",
                    JsonUtils.toJson(request), e.getMessage());
            logger.error(errInfo, e);
            return RestHelper.createErrorResponse(RestSubcacheSaveResponse.class, errInfo);
        }
    }

    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestSubCacheSearchResponse search(@Context final HttpServletResponse httpServletResponse,
                                             final RestSubCacheSearchRequest request) {

        try {
            List<Pair<ICacheEntry, Double>> results = subCacheService.search(
                    request.getCacheName(),
                    request.getSubCacheId(),
                    request.getQueryEntry());

            if (CollectionUtils.isEmpty(results)) {
                return new RestSubCacheSearchResponse();
            }

            List<Map> entries = new ArrayList<>();
            List<Double> scores = new ArrayList<>();
            for (Pair<ICacheEntry, Double> r : results) {
                entries.add(JsonUtils.toMap(r.getLeft()));
                scores.add(r.getRight());
            }

            RestSubCacheSearchResponse response = new RestSubCacheSearchResponse();
            response.setEntries(entries);
            response.setScores(scores);

            return response;
        } catch (Exception e) {
            String errInfo = String.format(
                    "failed to search cache[%s], exception[%s]",
                    request.getCacheName(), e.getMessage());
            logger.error(errInfo, e);
            return RestHelper.createErrorResponse(RestSubCacheSearchResponse.class, errInfo);
        }
    }
}
