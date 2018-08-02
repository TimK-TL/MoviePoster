package com.touchlogic.udacity.popularmovies.DataModels;

import java.util.List;

public class MovieTrailerResponse {
    public List<MovieTrailer> results;

    public List<MovieTrailer> getResults() {
        return results;
    }

    public void setResults(List<MovieTrailer> results) {
        this.results = results;
    }
}
