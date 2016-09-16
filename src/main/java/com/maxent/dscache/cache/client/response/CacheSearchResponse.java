package com.maxent.dscache.cache.client.response;

import java.util.List;
import java.util.Map;

/**
 * Created by alain on 16/8/21.
 */
public class CacheSearchResponse {
    private List<Double> scores;
    private List<Map> entries;

    public CacheSearchResponse(List<Double> scores, List<Map> entries) {
        this.scores = scores;
        this.entries = entries;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
    }

    public List<Map> getEntries() {
        return entries;
    }

    public void setEntries(List<Map> entries) {
        this.entries = entries;
    }
}
