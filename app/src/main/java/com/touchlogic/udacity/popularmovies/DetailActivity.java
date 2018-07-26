package com.touchlogic.udacity.popularmovies;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private ImageView iv_top_poster;
    private TextView trailerTitle;
    private TextView trailerEntry;
    private LinearLayout linearLayoutTrailers;

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

        trailerTitle = findViewById(R.id.tv_trailer_title);
        trailerTitle.setText("See trailers");

        description = findViewById(R.id.tv_plot_synopsis);
        description.setText(moviePoster.overview);

        ratings = findViewById(R.id.tv_vote_average);
        ratings.setText(String.format("Avg. Rating: %s", moviePoster.vote_average));

        releaseDate = findViewById(R.id.tv_release_date);
        releaseDate.setText(moviePoster.release_date);

        iv_top_poster = findViewById(R.id.iv_top_poster);
        String posterURL = NetworkUtils.getMovieImageURL(moviePoster.poster_path);
        Picasso.with(getBaseContext())
                .load(posterURL)
                .into(iv_top_poster);


        // when you click the poster, then go to the trailer playback (if there is at least one trailer)
        iv_top_poster.setOnClickListener(v -> {
            Log.i("DBG","Clicked poster image - goto trailer");
        });

        // Generate the several trailer options as individual elements in this card (NOTE: not a recyclerView because we expect only a few instances of light-weight views)
        linearLayoutTrailers = findViewById(R.id.linearLayout_trailers);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view;

        int maxCount = 3;
        for (int i = 0; i < maxCount; i++) {
            view = layoutInflater.inflate(R.layout.trailer_entry, null);
            TextView textView = view.findViewById(R.id.tv_trailer_entry);
            textView.setText("Row " + i);

            if (i % 2 == 0){
                textView.setBackgroundColor(getResources().getColor(R.color.material150Green));
            } else {
                textView.setBackgroundColor(getResources().getColor(R.color.material100Green));
            }

            int finalI = i;
            view.setOnClickListener(v -> {
                Log.d("DBG", "Clicked trailer index " + finalI);
            });

            // we -1 because there's a padding-entry at the end of the LinearLayout we want to place this list above
            linearLayoutTrailers.addView(view, linearLayoutTrailers.getChildCount()-1);
        }

    }
}
