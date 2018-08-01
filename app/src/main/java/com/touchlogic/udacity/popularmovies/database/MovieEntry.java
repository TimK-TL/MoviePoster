package com.touchlogic.udacity.popularmovies.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.touchlogic.udacity.popularmovies.DataModels.MoviePoster;

import java.util.Objects;

@Entity(tableName = "movie")
public class MovieEntry {

    @PrimaryKey
    public int id;

    // The following fields are returned by the REST API, but are not currently implemented in the app
    //    public boolean adult;
    //    public String original_language;
    //    public String original_title;
    //    public int[] genre_ids;
    //    public int vote_count;

    public float vote_average;
    public boolean video;
    public String backdrop_path;
    public String overview;
    public String title;
    public float popularity;
    public String poster_path;
    public String release_date;

    /// If the user chosen to favorite this movie
    public boolean isFavorited;

    public MovieEntry(int id, float vote_average, boolean video, String backdrop_path, String overview, String title, float popularity, String poster_path, String release_date, boolean isFavorited) {
        this.id = id;
        this.vote_average = vote_average;
        this.video = video;
        this.backdrop_path = backdrop_path;
        this.overview = overview;
        this.title = title;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.isFavorited = isFavorited;
    }

    public MovieEntry(int id, MoviePoster moviePoster) {
        this.id = id;
        this.video = moviePoster.video;
        this.vote_average = moviePoster.vote_average;
        this.title = moviePoster.title;
        this.popularity = moviePoster.popularity;
        this.poster_path = moviePoster.poster_path;
        this.backdrop_path = moviePoster.backdrop_path;
        this.overview = moviePoster.overview;
        this.release_date = moviePoster.release_date;
        this.isFavorited = moviePoster.isFavorited;
    }

    public MovieEntry(MoviePoster moviePoster) {
        this.video = moviePoster.video;
        this.vote_average = moviePoster.vote_average;
        this.title = moviePoster.title;
        this.popularity = moviePoster.popularity;
        this.poster_path = moviePoster.poster_path;
        this.backdrop_path = moviePoster.backdrop_path;
        this.overview = moviePoster.overview;
        this.release_date = moviePoster.release_date;
        this.isFavorited = moviePoster.isFavorited;
    }

    public int getId() {
        return id;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieEntry that = (MovieEntry) o;
        return id == that.id &&
                video == that.video &&
                Float.compare(that.vote_average, vote_average) == 0 &&
                Float.compare(that.popularity, popularity) == 0 &&
                isFavorited == that.isFavorited &&
                Objects.equals(title, that.title) &&
                Objects.equals(poster_path, that.poster_path) &&
                Objects.equals(backdrop_path, that.backdrop_path) &&
                Objects.equals(overview, that.overview) &&
                Objects.equals(release_date, that.release_date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, video, vote_average, title, popularity, poster_path, backdrop_path, overview, release_date, isFavorited);
    }

    public void setId(int id) {
        this.id = id;
    }

}
