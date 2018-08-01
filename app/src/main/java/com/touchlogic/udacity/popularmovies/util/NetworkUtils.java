package com.touchlogic.udacity.popularmovies.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.touchlogic.udacity.popularmovies.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {
    public enum Sorting {
        popularMovies, topRated, favorites
    }

    private final static String MOVIEPOSTER_BASE_URL = "api.themoviedb.org";
    private final static String MOVIEPOSTER_BASE_IMAGE_URL = "image.tmdb.org";

    /// Get new movies from the server based on the seleted sorting option
    public static void getMoviesBasedOnSorting(Sorting networkSorting, MovieJSONCallback movieJSONCallback){
        switch (networkSorting) {
            case popularMovies:
                getPopularMovies(movieJSONCallback);
                break;
            case topRated:
                getTopRatedMovies(movieJSONCallback);
                break;
        }
    }

    public static void getPopularMovies(MovieJSONCallback movieJSONCallback) {
        // example URL ---> https://api.themoviedb.org/3/movie/76341?api_key={api_key}

        String[] stringsToAppend = {"3", "movie", "popular"};
        URL url = buildUrl(MOVIEPOSTER_BASE_URL, stringsToAppend, true);
        new MoviePosterQueryTask(movieJSONCallback).execute(url);
    }

    public static void getTopRatedMovies(MovieJSONCallback movieJSONCallback) {
        // example URL ---> http://api.themoviedb.org/3/movie/top_rated?api_key={api_key}

        String[] stringsToAppend = {"3", "movie", "top_rated"};
        URL url = buildUrl(MOVIEPOSTER_BASE_URL, stringsToAppend, true);
        new MoviePosterQueryTask(movieJSONCallback).execute(url);
    }

    public static void getTrailersForMovie(TrailersJSONCallback trailersJSONCallback, int movieID) {
        // example URL ---> https://api.themoviedb.org/3/movie/353081/videos?api_key={api_key}

        String[] stringsToAppend = {"3", "movie", String.valueOf(movieID), "videos"};
        URL url = buildUrl(MOVIEPOSTER_BASE_URL, stringsToAppend, true);
        new MovieTrailerQueryTask(trailersJSONCallback).execute(url);
    }

    public static void getReviewsForMovie(ReviewsJSONCallback reviewsJSONCallback, int movieID) {
        // example URL ---> https://api.themoviedb.org/3/movie/351286/reviews?api_key={api_key}

        String[] stringsToAppend = {"3", "movie", String.valueOf(movieID), "reviews"};
        URL url = buildUrl(MOVIEPOSTER_BASE_URL, stringsToAppend, true);
        new MovieReviewsQueryTask(reviewsJSONCallback).execute(url);
    }

    // https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
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

        String qualityStr = "";
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

    public static JSONArray parsePopularMoviesJSON(String stringReturnedFromAsync) {

        try {
            JSONObject jsonObject = new JSONObject(stringReturnedFromAsync);
            JSONArray results = jsonObject.getJSONArray("results");
            return results;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

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

    // actually perform the URL and return the result
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    // Call the URL in an async task, and return the result
    public static class MoviePosterQueryTask extends AsyncTask<URL, Void, JSONArray> {

        MovieJSONCallback callback;
        JSONArray resultingArray;

        public MoviePosterQueryTask(MovieJSONCallback callback) {
            this.callback = callback;
        }

        @Override
        protected JSONArray doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String resultingString;
            try {
                resultingString = NetworkUtils.getResponseFromHttpUrl(searchURL);
                resultingArray = parsePopularMoviesJSON(resultingString);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            // return the returnedData via a listener interface
            callback.onMoviesReturned(resultingArray);
        }
    }

    // Call the URL in an async task, and return the result
    public static class MovieTrailerQueryTask extends AsyncTask<URL, Void, JSONArray> {

        TrailersJSONCallback callback;
        JSONArray resultingArray;

        public MovieTrailerQueryTask(TrailersJSONCallback callback) {
            this.callback = callback;
        }

        @Override
        protected JSONArray doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String resultingString;
            try {
                resultingString = NetworkUtils.getResponseFromHttpUrl(searchURL);
                resultingArray = parsePopularMoviesJSON(resultingString);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            // return the returnedData via a listener interface
            callback.onMovieTrailersReturned(resultingArray);
        }
    }

    // Call the URL in an async task, and return the result
    public static class MovieReviewsQueryTask extends AsyncTask<URL, Void, JSONArray> {

        ReviewsJSONCallback callback;
        JSONArray resultingArray;

        public MovieReviewsQueryTask(ReviewsJSONCallback callback) {
            this.callback = callback;
        }

        @Override
        protected JSONArray doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String resultingString;
            try {
                resultingString = NetworkUtils.getResponseFromHttpUrl(searchURL);
                resultingArray = parsePopularMoviesJSON(resultingString);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            // return the returnedData via a listener interface
            callback.onMovieReviewsReturned(resultingArray);
        }
    }

    public interface MovieJSONCallback {
        void onMoviesReturned(JSONArray moviesArray);
    }


    public interface TrailersJSONCallback {
        void onMovieTrailersReturned(JSONArray trailersArray);
    }

    public interface ReviewsJSONCallback {
        void onMovieReviewsReturned(JSONArray reviewsArray);
    }
}
