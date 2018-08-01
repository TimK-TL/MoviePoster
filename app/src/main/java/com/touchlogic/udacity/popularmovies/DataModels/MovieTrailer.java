package com.touchlogic.udacity.popularmovies.DataModels;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieTrailer {

    public String key;
    public String name;
    public String site;

    public MovieTrailer(JSONObject movieItem) {
        try {
            this.key = movieItem.getString("key");
            this.name = movieItem.getString("name");
            this.site = movieItem.getString("site");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
