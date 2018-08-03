package com.touchlogic.udacity.popularmovies;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.touchlogic.udacity.popularmovies.DataModels.MoviePoster;
import com.touchlogic.udacity.popularmovies.database.AppDatabase;
import com.touchlogic.udacity.popularmovies.database.MovieEntry;
import com.touchlogic.udacity.popularmovies.util.NetworkUtils;
import com.touchlogic.udacity.popularmovies.util.RetrofitController;

import java.util.List;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ItemClickListener, RetrofitController.MoviePostersReturned {


    private MovieRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private MoviePoster.Sorting sorting = MoviePoster.Sorting.mostPopular;

    private AppDatabase mDb;
    private LiveData<List<MovieEntry>> moviesFav;

    private GridLayoutManager layoutManager;
    private static final String LIST_STATE_KEY = "layoutBundleState";
    private Parcelable layoutState;
    private static final String SORTING_CHOICE = "currentSortingChoice";
    private static final String SCROLL_INDEX_KEY = "mainScrollIndex";
    private int scrollPosition;

    private void changeSortingFromAPITo(MoviePoster.Sorting sortingApiOverride) {
        sorting = sortingApiOverride;
        scrollPosition = 0; // reset scroll position
    }

    private void getFavoriteMovies(){

        if (mDb == null){
            mDb = AppDatabase.getInstance(getApplicationContext());
        }

        if (moviesFav == null){
            moviesFav = mDb.taskDao().loadAllTasks();
        }

        // Detect changes to the local list of favorited movies
        moviesFav.observe(this, movieEntries -> {
            Timber.d("movie entries changed! count: (" + movieEntries.size() + ")");

            if(sorting == MoviePoster.Sorting.favorites){
                if (movieEntries != null) {
                    MoviePoster[] movies = new MoviePoster[movieEntries.size()];

                    for (int i = 0; i < movieEntries.size(); i++) {
                        movies[i] = new MoviePoster(movieEntries.get(i));
                    }
                    presentMoviesInUI(movies);

                } else {
                    Timber.d("FAILED; no movies were found in favorites to show!");
                }
            }

        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save list state
        layoutState = layoutManager.onSaveInstanceState();

        outState.putParcelable(LIST_STATE_KEY, layoutState);
        outState.putInt(SORTING_CHOICE, sorting.getIndex());

        scrollPosition = layoutManager.findFirstVisibleItemPosition();
        outState.putInt(SCROLL_INDEX_KEY, scrollPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Retrieve list state and list/item positions
        if(savedInstanceState != null) {
            layoutState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            int val = savedInstanceState.getInt(SORTING_CHOICE, 0);
            sorting = MoviePoster.Sorting.makeFromIndex(val);
            applySorting(sorting);
            scrollPosition = savedInstanceState.getInt(SCROLL_INDEX_KEY, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (layoutState != null) {
            layoutManager.onRestoreInstanceState(layoutState);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.plant();
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_movie_list);
        int columns = 3;

        if (layoutManager == null) {
            layoutManager = new GridLayoutManager(this, columns);
        }

        recyclerView.setLayoutManager(layoutManager);
        adapter = new MovieRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        if (savedInstanceState != null){
            layoutState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        } else {
            applySorting(sorting);
        }

    }

    @Override
    public void launchActivity(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        MoviePoster posterToSend = adapter.getMoviePoster(position);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, posterToSend);
        startActivity(intent);
    }


    private void presentMoviesInUI(MoviePoster[] moviesFound){
        // update the list immediately with the texts found, while waiting for the images to download
        if (adapter != null) {
            adapter.setContentList(moviesFound, sorting);
            layoutManager.scrollToPosition(scrollPosition);
            Timber.d("moviesFound: %s", moviesFound.length);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sorting_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();

        if (menuItemSelected == R.id.mi_sort_api_favorites) {
            // Access the local DB of the stored movies instead of the Network service
            changeSortingFromAPITo(MoviePoster.Sorting.favorites);
            Toast.makeText(MainActivity.this, "Fetching movies by " + sorting.toString(), Toast.LENGTH_SHORT).show();
            applySorting(sorting);
            return super.onOptionsItemSelected(item);
        } else {
            if (menuItemSelected == R.id.mi_sort_api_popular) {
                changeSortingFromAPITo(MoviePoster.Sorting.mostPopular);
            } else if (menuItemSelected == R.id.mi_sort_api_highest_rated) {
                changeSortingFromAPITo(MoviePoster.Sorting.highestRated);
            }

            Context context = MainActivity.this;
            Toast.makeText(context, "Fetching movies by " + sorting.toString(), Toast.LENGTH_SHORT).show();
            applySorting(sorting);
            return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onMoviesReturned(List<MoviePoster> moviePosters) {
        presentMoviesInUI(moviePosters.toArray(new MoviePoster[moviePosters.size()]));
    }


    private void applySorting(MoviePoster.Sorting sortingSelected){

        switch (sortingSelected){
            case mostPopular:
            case highestRated:
                NetworkUtils.getMoviesBasedOnSorting(sortingSelected, this);
                break;
            case favorites:
                getFavoriteMovies();
                break;
            default:
                break;
        }
    }
}
