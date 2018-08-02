package com.touchlogic.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.touchlogic.udacity.popularmovies.DataModels.MoviePoster;
import com.touchlogic.udacity.popularmovies.util.NetworkUtils;

import java.util.List;

import butterknife.BindView;


public class MovieRecyclerViewAdapter extends RecyclerView.Adapter {

    private MoviePoster[] moviesToShow;
    private LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;
    private final int numberOfPosters = 9;

    MovieRecyclerViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public MoviePoster getMoviePoster(int index) {
        if (moviesToShow != null && moviesToShow.length > index) {
            return moviesToShow[index];
        } else {
            return null;
        }
    }

    public void sortMovies(MoviePoster.Sorting sortingToUse) {
        MoviePoster.sortMovies(moviesToShow, sortingToUse);
        notifyDataSetChanged();
    }

    public void setContentList(MoviePoster[] moviePosters, MoviePoster.Sorting sorting) {
        moviesToShow = moviePosters;
        // update local sorting before showing the new list of movies
        sortMovies(sorting);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            return null;
        } else {
            View view = layoutInflater.inflate(R.layout.rv_movie_item, parent, false);

            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        MoviePoster thisPoster = moviesToShow[viewHolder.getAdapterPosition()];
        viewHolder.setContents(thisPoster);
    }

    @Override
    public int getItemCount() {
        return moviesToShow != null ? moviesToShow.length : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ib_rv_item);
            imageView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.launchActivity(getAdapterPosition());
                }
            });
        }


        public void setContents(MoviePoster poster) {
            String posterURL = NetworkUtils.getMovieImageURL(poster.poster_path, 3);

            if (posterURL != null && !posterURL.equals("")) {
                Picasso.with(itemView.getContext())
                        .load(posterURL)
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(android.R.drawable.stat_notify_error)
                        .into(imageView);

            } else {
                imageView.setImageResource(R.drawable.no_internet);
            }
        }

    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void launchActivity(int position);
    }

}
