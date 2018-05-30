package com.touchlogic.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.touchlogic.udacity.popularmovies.DataModels.MoviePoster;
import com.touchlogic.udacity.popularmovies.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ItemClickListener, NetworkUtils.MovieJSONCallback {

    MovieRecyclerViewAdapter adapter;

    MoviePoster.Sorting sorting = MoviePoster.Sorting.mostPopular;

    private void changeSorting(){
        switch (sorting) {
            case unsorted:
                sorting = MoviePoster.Sorting.highestRated;
                break;
            case highestRated:
                sorting = MoviePoster.Sorting.mostPopular;
                break;
            case mostPopular:
                sorting = MoviePoster.Sorting.highestRated; // I don't want it to go back to unsorted
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.rv_movie_list);
        int columns = 3;
        recyclerView.setLayoutManager( new GridLayoutManager(this, columns));
        adapter = new MovieRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        NetworkUtils.getPopularMovies(this);
    }

    @Override
    public void launchActivity(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        MoviePoster posterToSend = adapter.GetMoviePoster(position);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, posterToSend);
        startActivity(intent);
    }


    @Override
    public void onMoviesReturned(JSONArray moviesArray) {

        if (moviesArray == null){
            Log.e("TAG", "moviesArray was null");
            return;
        }

//        Log.i("TAG","returned movies to Main: " + moviesArray.toString());

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

        // update the list immediately with the texts found, while waiting for the images to download
        if(adapter != null){
            adapter.SetContentList(moviesFound);
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
        if (menuItemSelected == R.id.mi_sort){
            Context context = MainActivity.this;
            changeSorting();
            adapter.SortMovies(sorting);
            Toast.makeText(context, "Sorting by " + sorting.toString(), Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

}
