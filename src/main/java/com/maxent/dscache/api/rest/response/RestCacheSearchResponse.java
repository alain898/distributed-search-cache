package com.maxent.dscache.api.rest.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.maxent.dscache.cache.ICacheEntry;

import java.util.List;

/**
 * Created by alain on 16/8/21.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RestCacheSearchResponse {
    private List<Double> scores;
    private List<ICacheEntry> entries;

    public RestCacheSearchResponse() {
    }

    public RestCacheSearchResponse(List<Double> scores, List<ICacheEntry> entries) {
        this.scores = scores;
        this.entries = entries;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
    }

    public List<ICacheEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ICacheEntry> entries) {
        this.entries = entries;
    }
}
