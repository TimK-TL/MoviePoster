package com.touchlogic.udacity.popularmovies;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.touchlogic.udacity.popularmovies.DataModels.MoviePoster;
import com.touchlogic.udacity.popularmovies.database.AppDatabase;
import com.touchlogic.udacity.popularmovies.database.MovieEntry;
import com.touchlogic.udacity.popularmovies.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ItemClickListener, NetworkUtils.MovieJSONCallback {

    private MovieRecyclerViewAdapter adapter;

    private MoviePoster.Sorting sorting = MoviePoster.Sorting.mostPopular;
    private NetworkUtils.Sorting sortingApi = NetworkUtils.Sorting.popularMovies;

    private AppDatabase mDb;
    private LiveData<List<MovieEntry>> moviesFav;

    private void changeSortingFromAPITo(NetworkUtils.Sorting sortingApiOverride) {
        sortingApi = sortingApiOverride;
    }

    private void getFavoriteMovies(){
        mDb = AppDatabase.getInstance(getApplicationContext());
        moviesFav = mDb.taskDao().loadAllTasks();
        moviesFav.observe(this, movieEntries -> {
            Log.d("DBG", "movie entries changed! count: ("+movieEntries+")");

            if (movieEntries != null) {
                MoviePoster[] movies = new MoviePoster[movieEntries.size()];

                for (int i = 0; i < movieEntries.size(); i++) {
                    movies[i] = new MoviePoster(movieEntries.get(i));
                }
                presentMoviesInUI(movies);

            } else {
                Log.d("DBG", "FAILED; no movies were found in favorites to show!");
            }
        });
    }

    /*
    Your request process is right! But you are redoing it everytime the activity is recreated (device is rotated, for example). It is inefficient to recall the webservice.
    It would be awesome if you implement a way to save your results to bundle at onSaveInstanceState() and retrieve it at onCreate() or onRestoreInstanceState() for your next stage
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.rv_movie_list);
        int columns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        adapter = new MovieRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        NetworkUtils.getMoviesBasedOnSorting(sortingApi, this);
    }

    @Override
    public void launchActivity(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        MoviePoster posterToSend = adapter.getMoviePoster(position);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, posterToSend);
        startActivity(intent);
    }

    @Override
    public void onMoviesReturned(JSONArray moviesArray) {

        if (moviesArray == null) {
            Log.e("TAG", "moviesArray was null");
            return;
        }

        MoviePoster[] moviesFound = new MoviePoster[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {

            try {
                JSONObject movieItem = moviesArray.getJSONObject(i);
                moviesFound[i] = new MoviePoster(movieItem);

            } catch (JSONException e) {
                e.printStackTrace();
                moviesFound[i] = null;
            }
        }

        presentMoviesInUI(moviesFound);
    }

    private void presentMoviesInUI(MoviePoster[] moviesFound){
        // update the list immediately with the texts found, while waiting for the images to download
        if (adapter != null) {
            adapter.setContentList(moviesFound, sorting);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sorting_option, menu);
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

        if (menuItemSelected == R.id.mi_sort_api_favorites) {
            // Access the local DB of the stored movies instead of the Network service
            getFavoriteMovies();
            Toast.makeText(MainActivity.this, "Fetching movies by " + sortingApi.toString(), Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        } else {
            if (menuItemSelected == R.id.mi_sort_api_popular) {
                changeSortingFromAPITo(NetworkUtils.Sorting.popularMovies);
            } else if (menuItemSelected == R.id.mi_sort_api_highest_rated) {
                changeSortingFromAPITo(NetworkUtils.Sorting.topRated);
            }

            Context context = MainActivity.this;
            NetworkUtils.getMoviesBasedOnSorting(sortingApi, this);
            Toast.makeText(context, "Fetching movies by " + sortingApi.toString(), Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);

        }



    }

}
