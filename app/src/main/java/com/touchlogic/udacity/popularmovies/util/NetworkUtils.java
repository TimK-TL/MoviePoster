package com.touchlogic.udacity.popularmovies.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
    private final static String MOVIEPOSTER_BASE_URL =
            "api.themoviedb.org";

    private final static String MOVIEPOSTER_BASE_IMAGE_URL =
            "image.tmdb.org/t/p/";

    private final static String API_KEY = "";  // Insert your MovieDB API key here


    public static void getPopularMovies(MovieJSONCallback movieJSONCallback) {
        // https://api.themoviedb.org/3/movie/76341?api_key={api_key}

        String[] stringsToAppend = {"3", "movie", "popular"};
        URL url = buildUrl(MOVIEPOSTER_BASE_URL, stringsToAppend, true);
        new MoviePosterQueryTask(movieJSONCallback).execute(url);
    }

    public static void getTopRatedMovies() {

    }

    public static void getMovieImage(MovieJSONCallback movieJSONCallback) {
        // http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg

        String[] stringsToAppend = {"w185", "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"};
        URL url = buildUrl(MOVIEPOSTER_BASE_IMAGE_URL, stringsToAppend, false);
        new MoviePosterQueryTask(movieJSONCallback).execute(url);
    }

    public static JSONArray parsePopularMoviesJSON(String stringReturnedFromAsync) {

        // TODO: change to a MoviePoster struct/class eventually

        try {
            JSONObject jsonObject = new JSONObject(stringReturnedFromAsync);
            JSONArray results = jsonObject.getJSONArray("results");

            String imageFilename;
            Log.i("TAG", "results" + results.toString());
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
            builder.appendQueryParameter("api_key", API_KEY);
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

        public MoviePosterQueryTask(MovieJSONCallback callback){
            this.callback = callback;
        }

        @Override
        protected JSONArray doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String resultingString = null;
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

    public interface MovieJSONCallback {
        void onMoviesReturned(JSONArray moviesArray);
        void onMovieImageReturned(URL imageURL);
    }
}
