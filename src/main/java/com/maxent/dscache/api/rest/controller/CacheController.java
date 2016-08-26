package com.maxent.dscache.api.rest.controller;

import com.maxent.dscache.api.rest.request.RestCreateCacheRequest;
import com.maxent.dscache.api.rest.request.RestCreateSubCacheRequest;
import com.maxent.dscache.api.rest.request.RestSubCacheSearchRequest;
import com.maxent.dscache.api.rest.response.RestCreateCacheResponse;
import com.maxent.dscache.api.rest.response.RestCreateSubCacheResponse;
import com.maxent.dscache.api.rest.response.RestSubCacheSearchResponse;
import com.maxent.dscache.api.rest.tools.RestHelper;
import com.maxent.dscache.cache.ICacheEntry;
import com.maxent.dscache.cache.SubCache;
import com.maxent.dscache.cache.SubCacheManager;
import com.maxent.dscache.cache.TestCacheEntry;
import com.maxent.dscache.cache.exceptions.CacheExistException;
import com.maxent.dscache.common.tools.ClassUtils;
import com.maxent.dscache.common.tools.JsonUtils;
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
public class CacheController {
    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    private SubCacheManager subCacheManager = new SubCacheManager();

    @POST
    public RestCreateSubCacheResponse create(@Context final HttpServletResponse httpServletResponse,
                                             final RestCreateSubCacheRequest request) {

        try {
            subCacheManager.addSubCache(
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
    public RestSubCacheSearchResponse match(@Context final HttpServletResponse httpServletResponse,
                                            final RestSubCacheSearchRequest request) {
        RestSubCacheSearchResponse response = new RestSubCacheSearchResponse();
        response.setError("hello");

        String cacheName = request.getCache_name();

        SubCache<ICacheEntry> subCache = subCacheManager.getSubCache(cacheName);
        if (subCache == null) {
            return RestHelper.createErrorResponse(RestSubCacheSearchResponse.class, "not exist");
        }

        Class<ICacheEntry> cacheEntryClass = subCache.getCacheEntryClass();

        ICacheEntry queryEntry = JsonUtils.fromMap(request.getQuery_entry(), cacheEntryClass);

        List<Pair<ICacheEntry, Double>> results = subCache.match(queryEntry);

        List<ICacheEntry> entries = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
        for (Pair<ICacheEntry, Double> r : results) {
            entries.add(r.getLeft());
            scores.add(r.getRight());
        }
        response.setEntries(entries);
        response.setScores(scores);
        return response;
    }
}
