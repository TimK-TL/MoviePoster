package com.touchlogic.udacity.popularmovies.DataModels;

import java.util.List;

public class MoviePosterResponse {
    public List<MoviePoster> results;

    public List<MoviePoster> getResults() {
        return results;
    }

    public void setResults(List<MoviePoster> results) {
        this.results = results;
    }
}
