package com.alain898.dscache.api.rest.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Map;

/**
 * Created by alain on 16/8/26.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RestSubCacheSearchResponse extends RestCommonResponse {
    private List<Double> scores;
    private List<Map> entries;

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
