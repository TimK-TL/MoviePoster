package com.touchlogic.udacity.popularmovies.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;

public class MoviePoster implements Parcelable {

    // These are all the options returned by the API, but not all have been implemented, because they were deemed unnecessary at this time
    public int vote_count;
    public int id;
    public boolean video;
    public float vote_average;
    public String title;
    public float popularity;
    public String poster_path;
    public String original_language;
    public String original_title;
    public int[] genre_ids;
    public String backdrop_path;
    public boolean adult;
    public String overview;
    public String release_date;

    public static final Parcelable.Creator<MoviePoster> CREATOR
            = new Parcelable.Creator<MoviePoster>() {

        public MoviePoster createFromParcel(Parcel in) {
            MoviePoster posterToMake = new MoviePoster(in);
            return posterToMake;
        }

        public MoviePoster[] newArray(int size) {
            return new MoviePoster[size];
        }
    };

    public MoviePoster(Parcel source) {

        id = source.readInt();
        title = source.readString();
        overview = source.readString();
        poster_path = source.readString();
        vote_average = source.readFloat();
        popularity = source.readFloat();
        release_date = source.readString();

    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeFloat(vote_average);
        dest.writeFloat(popularity);
        dest.writeString(release_date);
    }

    public enum Sorting {
        unsorted, highestRated, mostPopular
    }

    public MoviePoster(JSONObject movieItem) {
        try {
            this.id = movieItem.getInt("id");
            this.video = movieItem.getBoolean("video");
            this.title = movieItem.getString("title");
            this.poster_path = movieItem.getString("poster_path");
            this.backdrop_path = movieItem.getString("backdrop_path");
            this.overview = movieItem.getString("overview");
            this.release_date = movieItem.getString("release_date");
            this.popularity = ((float) movieItem.getLong("popularity"));
            this.vote_average = ((float) movieItem.getLong("vote_average"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void SortMovies(MoviePoster[] moviesToSort, Sorting sortingToUse) {

        Arrays.sort(moviesToSort, new Comparator<MoviePoster>() {
            @Override
            public int compare(MoviePoster o1, MoviePoster o2) {

                if (sortingToUse == Sorting.highestRated) {
                    return o1.vote_average > o2.vote_average ? -1 : 1;
                } else if (sortingToUse == Sorting.mostPopular) {
                    return o1.popularity > o2.popularity ? -1 : 1;
                } else {
                    return 0;
                }

            }
        });

    }

}
