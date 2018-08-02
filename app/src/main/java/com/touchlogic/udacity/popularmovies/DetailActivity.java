package com.touchlogic.udacity.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.touchlogic.udacity.popularmovies.DataModels.MoviePoster;
import com.touchlogic.udacity.popularmovies.DataModels.MovieReview;
import com.touchlogic.udacity.popularmovies.DataModels.MovieTrailer;
import com.touchlogic.udacity.popularmovies.database.AppDatabase;
import com.touchlogic.udacity.popularmovies.database.MovieEntry;
import com.touchlogic.udacity.popularmovies.util.NetworkUtils;
import com.touchlogic.udacity.popularmovies.util.RetrofitController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements RetrofitController.TrailersReturned, RetrofitController.ReviewsReturned {

    public static final String EXTRA_MOVIE = "moviePoster";

    private MoviePoster moviePoster;

    @BindView(R.id.tv_title) TextView title;
    @BindView(R.id.tv_plot_synopsis) TextView description;
    @BindView(R.id.tv_vote_average) TextView ratings;
    @BindView(R.id.tv_release_date) TextView releaseDate;
    @BindView(R.id.iv_top_poster) ImageView iv_top_poster;
    @BindView(R.id.tv_trailer_title) TextView trailerTitle;
    @BindView(R.id.tv_reviews_title) TextView reviewsTitle;
    @BindView(R.id.linearLayout_trailers) LinearLayout linearLayoutTrailers;
    @BindView(R.id.linearLayout_reviews) LinearLayout linearLayoutReviews;


    // Member variables for the Database access
    private AppDatabase mDb;
    private LiveData<List<MovieEntry>> moviesFav;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);
        ButterKnife.bind(this);

        mDb = AppDatabase.getInstance(getApplicationContext());
        moviesFav = mDb.taskDao().loadAllTasks();
        moviesFav.observe(this, movieEntries -> {

            if (movieEntries != null) {
                for (MovieEntry movieItem : movieEntries ) {
                    if ( movieItem.id == moviePoster.id ){
                        moviePoster.isFavorited = movieItem.isFavorited;
                        updateFavStar();    // attempt to update the star immediately, but this may fail due to a timing issue
                    }
                }
            } else {
                Timber.d("FAILED; no movies were found!");
            }
        });


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

        RetrofitController.getTrailersForMovie(this.moviePoster.id, this);
        RetrofitController.getReviewsForMovie(this.moviePoster.id, this);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    /**
    * update the Favorite Star in the top-right corner of the movie, indicating whether the movie has been favorited or not
    * */
    private void updateFavStar(){

        AppExecutors.getInstance().mainThread().execute(() -> {

            if (menu != null) {
                MenuItem menuItem = menu.findItem(R.id.mi_fav);

                if (moviePoster.isFavorited) {
                    menuItem.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                } else {
                    menuItem.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_off));
                }
            } else {
                Timber.d("FAILED to find menu variable");
            }

        });

    }

    private void populateUI() {

        title.setText(moviePoster.title);
        trailerTitle.setText(R.string.trailerTitle);
        reviewsTitle.setText(R.string.reviewTitle);
        description.setText(moviePoster.overview);
        ratings.setText(String.format("Avg. Rating: %s", moviePoster.vote_average));
        releaseDate.setText(moviePoster.release_date);

        // it seems as if I need to save the previous image to a file, to be able to efficiently load it before the high-res image is ready... but I haven't done this.
        String posterURLHigh = NetworkUtils.getMovieImageURL(moviePoster.poster_path, 1);
        Picasso.with(getBaseContext())
                .load(posterURLHigh)
                .into(iv_top_poster);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);

        // disable the auto-fade functionality
        collapsingToolbarLayout.setContentScrim(null);
    }

    private void populateTrailerItems(MovieTrailer[] trailers) {
        // Generate the several trailer options as individual elements in this card (NOTE: not a recyclerView because we expect only a few instances of light-weight views)
        LayoutInflater layoutInflater = getLayoutInflater();
        View view;

        int maxCount = trailers.length;

        for (int i = 0; i < maxCount; i++) {
            view = layoutInflater.inflate(R.layout.trailer_entry, null);
            TextView textView = view.findViewById(R.id.tv_trailer_entry);
            textView.setText(trailers[i].name);

            if (i % 2 == 0) {
                textView.setBackgroundColor(getResources().getColor(R.color.material150Green));
            } else {
                textView.setBackgroundColor(getResources().getColor(R.color.material100Green));
            }

            // When the items are clicked, open YouTube (app or web)
            int finalI = i;
            view.setOnClickListener(v -> NetworkUtils.watchYoutubeVideo(getBaseContext(), trailers[finalI].key));

            // we -1 because there's a padding-entry at the end of the LinearLayout we want to place this list above
            linearLayoutTrailers.addView(view, linearLayoutTrailers.getChildCount() - 1);
        }
    }

    private void populateReviewItems(MovieReview[] reviews) {
        // Generate the several trailer options as individual elements in this card (NOTE: not a recyclerView because we expect only a few instances of light-weight views)
        LayoutInflater layoutInflater = getLayoutInflater();
        View view;

        int maxCount = reviews.length;

        for (int i = 0; i < maxCount; i++) {
            view = layoutInflater.inflate(R.layout.review_entry, null);
            TextView tvReviewAuthor = view.findViewById(R.id.tv_review_author);
            TextView tvReviewEntry = view.findViewById(R.id.tv_review_entry);
            tvReviewAuthor.setText(reviews[i].author);
            tvReviewEntry.setText(reviews[i].content);

            // we -1 because there's a padding-entry at the end of the LinearLayout we want to place this list above
            linearLayoutReviews.addView(view, linearLayoutReviews.getChildCount() - 1);
        }
    }

    @Override
    public void onMovieTrailersReturned(List<MovieTrailer> trailersArray) {
        // load the trailer texts and links into the details page
        if (trailersArray.size() > 0) {
            populateTrailerItems(trailersArray.toArray(new MovieTrailer[trailersArray.size()]));
        }
    }

    @Override
    public void onMovieReviewsReturned(List<MovieReview> reviewsArray) {
        if (reviewsArray.size() > 0) {
            populateReviewItems(reviewsArray.toArray(new MovieReview[reviewsArray.size()]));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_favorite, menu);
        this.menu = menu;
        return true;
    }

    /*
    It is okay that you used this approach to update the main UI when user changes the sort criteria.
    But it doesn't retain the choice of user when you quit your app or the configuration changes ( device rotated). It is going to set "Popular movie" by default
    You can use SharedPreferences to save this value and get it onCreate()
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();
        if (menuItemSelected == R.id.mi_fav) {
            Context context = DetailActivity.this;
            Toast.makeText(context, "Clicked FAV btn", Toast.LENGTH_SHORT).show();

            this.moviePoster.isFavorited = !this.moviePoster.isFavorited;

            if (this.moviePoster.isFavorited) {
                item.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                insertNewFavoriteMovie(this.moviePoster);
            } else {
                item.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_off));
                deleteFavoriteMovie(this.moviePoster);
            }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        updateFavStar();    // I have a timing issue, so I currently have to attempt to update the state here as well, if this came in later than the other updateFavStar()
        return super.onPrepareOptionsMenu(menu);
    }

    public void insertNewFavoriteMovie(MoviePoster moviePoster) {
        moviePoster.isFavorited = true;
        final MovieEntry task = new MovieEntry(moviePoster.id, moviePoster);
        AppExecutors.getInstance().diskIO().execute(() -> {
            // insert new task
            mDb.taskDao().insertTask(task);
        });
    }

    public void deleteFavoriteMovie(MoviePoster moviePoster){
        AppExecutors.getInstance().diskIO().execute(() -> {
            
            // I need to find the MoviePoster in the DB list before deleting it
            MovieEntry movieEntry = mDb.taskDao().getMovieById(moviePoster.id);
            if (movieEntry != null){
                mDb.taskDao().deleteTask( movieEntry );
            } else {
                Timber.d("movie poster entry NOT FOUND. Can't delete it from the database!");
            }
        });
    }
}
