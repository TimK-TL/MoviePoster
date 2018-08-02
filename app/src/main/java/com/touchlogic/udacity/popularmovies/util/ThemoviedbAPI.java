package com.touchlogic.udacity.popularmovies.util;

import com.touchlogic.udacity.popularmovies.DataModels.MoviePosterResponse;
import com.touchlogic.udacity.popularmovies.DataModels.MovieReviewResponse;
import com.touchlogic.udacity.popularmovies.DataModels.MovieTrailerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ThemoviedbAPI {

    @GET("3/movie/popular")
    Call<MoviePosterResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("3/movie/top_rated")
    Call<MoviePosterResponse> getTopRatedMovies(@Query("api_key") String apiKey );

    @GET("3/movie/{movieID}/videos")
    Call<MovieTrailerResponse> getTrailersForMovie(@Path("movieID") int movieID, @Query("api_key") String apiKey);

    @GET("3/movie/{movieID}/reviews")
    Call<MovieReviewResponse> getReviewsForMovie(@Path("movieID") int movieID, @Query("api_key") String apiKey);


    }
