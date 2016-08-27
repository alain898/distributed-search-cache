package com.maxent.dscache.api.rest.controller;

import com.maxent.dscache.api.rest.request.RestCreateSubCacheRequest;
import com.maxent.dscache.api.rest.request.RestSubCacheSearchRequest;
import com.maxent.dscache.api.rest.response.RestCreateSubCacheResponse;
import com.maxent.dscache.api.rest.response.RestSubCacheSearchResponse;
import com.maxent.dscache.api.rest.tools.RestHelper;
import com.maxent.dscache.cache.ICacheEntry;
import com.maxent.dscache.cache.SubCacheService;
import com.maxent.dscache.cache.exceptions.CacheExistException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
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

    @POST
    public RestCreateSubCacheResponse create(@Context final HttpServletResponse httpServletResponse,
                                             final RestCreateSubCacheRequest request) {

        try {
            subCacheService.createSubCache(
                    request.getName(),
                    request.getEntryClassName(),
                    request.getPartitionsPerSubCache(),
                    request.getBlocksPerPartition(),
                    request.getBlockCapacity());
            return RestHelper.doResponse(
                    httpServletResponse,
                    HttpServletResponse.SC_CREATED,
                    new RestCreateSubCacheResponse(
                            request.getName(),
                            request.getEntryClassName(),
                            request.getPartitionsPerSubCache(),
                            request.getBlocksPerPartition(),
                            request.getBlockCapacity()));
        } catch (CacheExistException e) {
            String errInfo = String.format("cache[%s] already exist", request.getName());
            logger.warn(errInfo, e);
            return RestHelper.doResponse(
                    httpServletResponse,
                    HttpServletResponse.SC_OK,
                    RestHelper.createErrorResponse(RestCreateSubCacheResponse.class, errInfo));
        } catch (Exception e) {
            String errInfo = String.format(
                    "failed to create cache[%s], exception[%s]",
                    request.getName(), e.getMessage());
            logger.error(errInfo, e);
            return RestHelper.doResponse(
                    httpServletResponse,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    RestHelper.createErrorResponse(RestCreateSubCacheResponse.class, errInfo));
        }
    }

    @POST
    @Path("/save")
    public String save(@Context final HttpServletResponse httpServletResponse,
                       final String request) {
        return "save " + request;
    }

    @POST
    @Path("/search")
    public RestSubCacheSearchResponse search(@Context final HttpServletResponse httpServletResponse,
                                             final RestSubCacheSearchRequest request) {

        try {
            List<Pair<ICacheEntry, Double>> results = subCacheService.match(
                    request.getCache_name(),
                    request.getQuery_entry());

            List<ICacheEntry> entries = new ArrayList<>();
            List<Double> scores = new ArrayList<>();
            for (Pair<ICacheEntry, Double> r : results) {
                entries.add(r.getLeft());
                scores.add(r.getRight());
            }

            RestSubCacheSearchResponse response = new RestSubCacheSearchResponse();
            response.setEntries(entries);
            response.setScores(scores);

            return RestHelper.doResponse(
                    httpServletResponse,
                    HttpServletResponse.SC_CREATED,
                    response);
        } catch (Exception e) {
            String errInfo = String.format(
                    "failed to create cache[%s], exception[%s]",
                    request.getCache_name(), e.getMessage());
            logger.error(errInfo, e);
            return RestHelper.doResponse(
                    httpServletResponse,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    RestHelper.createErrorResponse(RestSubCacheSearchResponse.class, errInfo));
        }
    }
}
