package com.touchlogic.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.touchlogic.udacity.popularmovies.DataModels.MoviePoster;
import com.touchlogic.udacity.popularmovies.util.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "moviePoster";

    private MoviePoster moviePoster;

    private TextView title;
    private TextView description;
    private TextView ratings;
    private TextView releaseDate;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_view);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        MoviePoster moviePoster = intent.getParcelableExtra(EXTRA_MOVIE);
        if (moviePoster == null) {
            // EXTRA_MOVIE not found in intent
            closeOnError();
            return;
        }

        this.moviePoster = moviePoster;

        populateUI();
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI() {
        title = findViewById(R.id.tv_title);
        title.setText(moviePoster.title);

        description = findViewById(R.id.tv_plot_synopsis);
        description.setText(moviePoster.overview);

        ratings = findViewById(R.id.tv_vote_average);
        ratings.setText(String.format("Avg. Rating: %s", moviePoster.vote_average));

        releaseDate = findViewById(R.id.tv_release_date);
        releaseDate.setText(moviePoster.release_date);

        imageView = findViewById(R.id.imageView);
        String posterURL = NetworkUtils.getMovieImageURL(moviePoster.poster_path);
        Picasso.with(getBaseContext())
                .load(posterURL)
                .into(imageView);

    }
}
