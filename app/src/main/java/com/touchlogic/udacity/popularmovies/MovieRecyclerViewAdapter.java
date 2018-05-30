package com.touchlogic.udacity.popularmovies;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.touchlogic.udacity.popularmovies.DataModels.MoviePoster;
import com.touchlogic.udacity.popularmovies.util.NetworkUtils;

import java.util.ArrayList;


public class MovieRecyclerViewAdapter extends RecyclerView.Adapter {

    private MoviePoster[] moviesToShow;
    private String[] testStrings;
    private LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;
    private final int numberOfPosters = 9;

    MovieRecyclerViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public MoviePoster GetMoviePoster(int index) {
        if (moviesToShow != null && moviesToShow.length > index) {
            return moviesToShow[index];
        } else {
            return null;
        }
    }

    public void SortMovies(MoviePoster.Sorting sortingToUse) {
        MoviePoster.SortMovies(moviesToShow, sortingToUse);
        notifyDataSetChanged();
    }

    public void SetContentList(MoviePoster[] moviePosters) {
        moviesToShow = moviePosters;
        notifyDataSetChanged();
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

        MoviePoster thisPoster = moviesToShow[position];
        viewHolder.SetContents(thisPoster);
    }

    /// This was an attempt at dynamically making colours to use in the bind for the various cells (NOT USED)
    private int getColourForIndex(int indexToCheck) {
        float currentPercentage = ((float) indexToCheck / (float) numberOfPosters);
//        int currentAlpha = (int)(currentPercentage * 0xFF);
        int colourVariation = (int) (((float) indexToCheck / (float) numberOfPosters) * (float) 0xFF);
        String hexString = Integer.toString(colourVariation, 16);
        String hexStringTwoChars = (hexString.equals("0")) ? "00" : hexString;
        String hexStringCompleted = "#FF" + hexStringTwoChars + "FF";

//        int colourIncrement = (int)((float)indexToCheck * (float)0xFFFFFF);
//        int baseColour = 0x00FFFFFF;
//        int currentColour = baseColour + colourIncrement;

        int color = Integer.parseInt(hexStringCompleted.replaceFirst("^#", ""), 16);

        return color;
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


        public void SetContents(MoviePoster poster) {
            String posterURL = NetworkUtils.getMovieImageURL(poster.poster_path);

            if (posterURL != null && !posterURL.equals("")) {
                Picasso.with(itemView.getContext())
                        .load(posterURL)
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
