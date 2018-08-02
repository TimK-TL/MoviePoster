package com.touchlogic.udacity.popularmovies.DataModels;

import java.util.List;

public class MovieReviewResponse {
    public List<MovieReview> results;

    public List<MovieReview> getResults() {
        return results;
    }

    public void setResults(List<MovieReview> results) {
        this.results = results;
    }
}
