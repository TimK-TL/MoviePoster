package com.touchlogic.udacity.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM movie ORDER BY title")
    LiveData<List<MovieEntry>> loadAllTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(MovieEntry movieEntry);

    @Update
    void updateTask(MovieEntry movieEntry);

    @Delete
    void deleteTask(MovieEntry movieEntry);

    @Query("SELECT * FROM movie WHERE id = :id")
    LiveData<MovieEntry> loadTaskById(int id);

    @Query("SELECT * FROM movie WHERE id = :id")
    MovieEntry getMovieById(int id);

    @Query("SELECT * FROM movie")
    MovieEntry[] loadAllMoviesSimple();
}
