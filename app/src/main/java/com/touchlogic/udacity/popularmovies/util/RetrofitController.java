package com.touchlogic.udacity.popularmovies.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.touchlogic.udacity.popularmovies.BuildConfig;
import com.touchlogic.udacity.popularmovies.DataModels.MoviePoster;
import com.touchlogic.udacity.popularmovies.DataModels.MoviePosterResponse;
import com.touchlogic.udacity.popularmovies.DataModels.MovieReview;
import com.touchlogic.udacity.popularmovies.DataModels.MovieReviewResponse;
import com.touchlogic.udacity.popularmovies.DataModels.MovieTrailer;
import com.touchlogic.udacity.popularmovies.DataModels.MovieTrailerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

// NOTE: This class should probably be integrated within the NetworkUtils class
public class RetrofitController {

    private final static String MOVIEDB_BASE_URL = "https://" + NetworkUtils.MOVIEPOSTER_BASE_URL + "/";

    private static ThemoviedbAPI themoviedbAPI;

    public void start() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIEDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        themoviedbAPI = retrofit.create(ThemoviedbAPI.class);
    }

    private static void prepareRetrofit(){
        if (themoviedbAPI == null){
            RetrofitController retrofitController = new RetrofitController();
            retrofitController.start();
        }
    }

    public static void getPopularMovies(MoviePostersReturned moviePostersReturned) {

        prepareRetrofit();
        Call<MoviePosterResponse> moviePosters = themoviedbAPI.getPopularMovies(BuildConfig.APP_KEY);
        moviePosters.enqueue(new Callback<MoviePosterResponse>() {
            @Override
            public void onResponse(Call<MoviePosterResponse> call, Response<MoviePosterResponse> response) {
                if(response.isSuccessful()) {
                    MoviePosterResponse moviePosterResponse = response.body();
                    if (moviePosterResponse != null) {
                        moviePostersReturned.onMoviesReturned(moviePosterResponse.results);
                    }
                } else {
                    Timber.d(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<MoviePosterResponse> call, Throwable t) {
                Timber.d("FAILED: %s", call.request().url().toString());
            }
        });
    }

    public static void getTopRatedMovies(MoviePostersReturned moviePostersReturned) {
        prepareRetrofit();
        Call<MoviePosterResponse> moviePosters = themoviedbAPI.getTopRatedMovies(BuildConfig.APP_KEY);
        moviePosters.enqueue(new Callback<MoviePosterResponse>() {
            @Override
            public void onResponse(Call<MoviePosterResponse> call, Response<MoviePosterResponse> response) {
                if(response.isSuccessful()) {
                    MoviePosterResponse moviePosterResponse = response.body();
                    if (moviePosterResponse != null) {
                        moviePostersReturned.onMoviesReturned(moviePosterResponse.results);
                    }
                } else {
                    Timber.d(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<MoviePosterResponse> call, Throwable t) {
                Timber.d("FAILED: %s", call.request().url().toString());
            }
        });
    }

    public static void getTrailersForMovie(int movieID, TrailersReturned trailersReturned) {
        prepareRetrofit();
        Call<MovieTrailerResponse> movieTrailerResponses = themoviedbAPI.getTrailersForMovie(movieID, BuildConfig.APP_KEY);
        movieTrailerResponses.enqueue(new Callback<MovieTrailerResponse>() {
            @Override
            public void onResponse(Call<MovieTrailerResponse> call, Response<MovieTrailerResponse> response) {
                if(response.isSuccessful()) {
                    MovieTrailerResponse movieTrailerResponse = response.body();
                    if (movieTrailerResponse != null) {
                        trailersReturned.onMovieTrailersReturned(movieTrailerResponse.results);
                    }
                } else {
                    Timber.d(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<MovieTrailerResponse> call, Throwable t) {
                Timber.d("FAILED: %s", call.request().url().toString());
            }
        });
    }

    public static void getReviewsForMovie(int movieID, ReviewsReturned reviewsReturned) {
        prepareRetrofit();
        Call<MovieReviewResponse> movieReviewResponseCall = themoviedbAPI.getReviewsForMovie(movieID, BuildConfig.APP_KEY);
        movieReviewResponseCall.enqueue(new Callback<MovieReviewResponse>() {
            @Override
            public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                if(response.isSuccessful()) {
                    MovieReviewResponse movieReviewResponse = response.body();
                    if (movieReviewResponse != null) {
                        reviewsReturned.onMovieReviewsReturned(movieReviewResponse.results);
                    }
                } else {
                    Timber.d(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<MovieReviewResponse> call, Throwable t) {
                Timber.d("FAILED: %s", call.request().url().toString());
            }
        });
    }

    public interface MoviePostersReturned {
        void onMoviesReturned(List<MoviePoster> moviesPosters);
    }

    public interface TrailersReturned {
        void onMovieTrailersReturned(List<MovieTrailer> trailersArray);
    }

    public interface ReviewsReturned{
        void onMovieReviewsReturned(List<MovieReview> reviewsArray);
    }

}
