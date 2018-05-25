package com.touchlogic.udacity.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.touchlogic.udacity.popularmovies.util.NetworkUtils;

import org.json.JSONArray;

import java.io.IOException;
import java.net.URL;

import static com.touchlogic.udacity.popularmovies.util.NetworkUtils.parsePopularMoviesJSON;

public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ItemClickListener, NetworkUtils.MovieJSONCallback {

    MovieRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] testStrings = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};

        RecyclerView recyclerView = findViewById(R.id.rv_movie_list);
        int columns = 3;
        recyclerView.setLayoutManager( new GridLayoutManager(this, columns));
        adapter = new MovieRecyclerViewAdapter(this, null, testStrings);
//        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        NetworkUtils.getPopularMovies(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG", "onItemClick -- " + position);
    }


    @Override
    public void onMoviesReturned(JSONArray moviesArray) {
        Log.i("TAG","returned movies to Main: " + moviesArray.toString());
    }

    @Override
    public void onMovieImageReturned(URL imageURL) {
        Log.i("TAG","imageURL returned: " + imageURL);
    }
}
