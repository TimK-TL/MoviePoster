package com.touchlogic.udacity.popularmovies.DataModels;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieReview {

    public String author;
    public String content;
    public String id;
    public String url;

    public MovieReview(JSONObject movieItem) {
        try {
            this.author = movieItem.getString("author");
            this.content = movieItem.getString("content");
            this.id = movieItem.getString("id");
            this.url = movieItem.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
