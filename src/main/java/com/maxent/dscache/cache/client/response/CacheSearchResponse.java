package com.maxent.dscache.cache.client.response;

import com.maxent.dscache.cache.ICacheEntry;

import java.util.List;

/**
 * Created by alain on 16/8/21.
 */
public class CacheSearchResponse {
    private List<Double> scores;
    private List<ICacheEntry> entries;

    public CacheSearchResponse(List<Double> scores, List<ICacheEntry> entries) {
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
