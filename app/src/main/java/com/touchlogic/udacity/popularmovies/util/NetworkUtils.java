package com.touchlogic.udacity.popularmovies.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.touchlogic.udacity.popularmovies.BuildConfig;
import com.touchlogic.udacity.popularmovies.DataModels.MoviePoster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public final class NetworkUtils {

    protected final static String MOVIEPOSTER_BASE_URL = "api.themoviedb.org";
    private final static String MOVIEPOSTER_BASE_IMAGE_URL = "image.tmdb.org";

    private NetworkUtils(){

    }

    /// Get new movies from the server based on the seleted sorting option
    public static void getMoviesBasedOnSorting(MoviePoster.Sorting networkSorting, RetrofitController.MoviePostersReturned moviePostersReturned){
        switch (networkSorting) {
            case mostPopular:
                RetrofitController.getPopularMovies(moviePostersReturned);
                break;
            case highestRated:
                RetrofitController.getTopRatedMovies(moviePostersReturned);
                break;
        }
    }

    // source: https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent (July 25, 2018)
    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    /// quality = {0: original, 1: w780, 2: w500, 3: w342}
    public static String getMovieImageURL(String imageID, int quality) {
        // example URL ---> http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg

        String qualityStr;
        switch (quality){
            case 0: qualityStr = "original"; break;
            case 1: qualityStr = "w780"; break;
            case 2: qualityStr = "w500"; break;
            case 3: qualityStr = "w342"; break;
            default:qualityStr = "w342"; break;
        }

        // remove the initial '/' from the image filename
        String fixedImage = imageID.subSequence(1, imageID.length()).toString();
        String[] stringsToAppend = {"t", "p", qualityStr, fixedImage};  //sizes: https://www.themoviedb.org/talk/53c11d4ec3a3684cf4006400
        URL url = buildUrl(MOVIEPOSTER_BASE_IMAGE_URL, stringsToAppend, false);
        return url.toString();
    }

    public static URL buildUrl(String authority, String[] appendPaths, boolean includeAPIKey) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(authority);

        for (String path : appendPaths) {
            builder.appendPath(path);
        }

        if (includeAPIKey) {
            builder.appendQueryParameter("api_key", BuildConfig.APP_KEY);
        }

        Uri builtUri = builder.build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }






}
