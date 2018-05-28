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
    //    private Uri[] imageLinks;
    private String[] testStrings;
    private LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;
    //    private int[] colorsToUse;
    private final int numberOfPosters = 9;

    private int badCounter = 0;

    MovieRecyclerViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);

//        colorsToUse = new int[numberOfPosters];
//        for (int i = 0; i < numberOfPosters; i++) {
//            colorsToUse[i] = 0x00FFFF;
//        }
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


            // I can't figure out how to change the !"#"%€#&% color in the bind function, so I'm doing this here
//            int colorFromRes = ColorUtils.getViewHolderBackgroundColorFromInstance(parent.getContext(), badCounter++);
//            ViewHolder viewHolder = new ViewHolder(view);

//            viewHolder.SetContents("OnCreate", null, 3);
//            viewHolder.imageButton.setBackgroundColor(colorFromRes);
//            viewHolder.itemView.setBackgroundColor(colorFromRes);

            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
//        Uri uri = Uri.parse("http://www.joblo.com/posters/images/full/the-last-jedi-poster-new.jpg");
//        Uri uri = Uri.fromParts("http", "www.joblo.com/posters/images/full/the-last-jedi-poster-new.jpg", "");

        MoviePoster thisPoster = moviesToShow[position];
        viewHolder.SetContents(thisPoster);
//        viewHolder.SetContents("TEST!", "http://www.joblo.com/posters/images/full/the-last-jedi-poster-new.jpg", position);
//        viewHolder.SetContents(null, null, position);
    }

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
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_image_test);
            imageView = itemView.findViewById(R.id.ib_rv_item);
            imageView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.launchActivity(getAdapterPosition());
                }
            });
        }


        public void SetContents(MoviePoster poster) {
//            textView.setText(String.format("Pop: %s", poster.popularity));
            String posterURL = NetworkUtils.getMovieImageURL(poster.poster_path);

            if (posterURL != null && !posterURL.equals("")) {
                Picasso.with(itemView.getContext())
                        .load(posterURL)
                        .into(imageView);

            } else {
                imageView.setImageResource(R.drawable.no_internet);
            }
        }

        public void SetContents(String debugText, String imagePath, int position) {

            if (debugText != null) {
                textView.setText(String.format("%s%d", debugText, position));
            }

            if (imagePath != null) {
                Picasso.with(itemView.getContext())
                        .load("http://www.joblo.com/posters/images/full/the-last-jedi-poster-new.jpg")
                        .into(imageView);

            } else {
                // I just CAN'T get the colour to appear... *sigh*
//                Context context = itemView.getContext().getDrawable(R.raw.noInternet);
//                imageView.setImageDrawable(R.raw.noInternet);
                imageView.setImageResource(R.drawable.no_internet);

//                float currentPercentage = ((float)getAdapterPosition() / (float)testStrings.length);
//                int currentAlpha = (int)(currentPercentage * 0xFF);
//                int colourVariation = (int)(((float)getAdapterPosition() / (float)testStrings.length) * (float)0xFF);
//                String hexString = Integer.toString(colourVariation, 16);
//                String string = "#"+hexString+"FFFFFF";
//
//                int colourIncrement = (int)((float)getAdapterPosition() * (float)0xFFFFFF);
//                int baseColour = 0x00FFFFFF;
//                int currentColour = baseColour + colourIncrement;
//
//                int color = Integer.parseInt(string.replaceFirst("^#",""), 16);

//                this.itemView.setBackgroundColor(0xFFFFFFFF);
//                imageButton.setBackgroundColor(0xFFFFFFFF);

//                ColorDrawable cd = new ColorDrawable(0xab0000);
//                cd.setAlpha(currentAlpha);
//                imageButton.setBackground(cd);
//                getAdapterPosition();


//                int thisItemColor = getColourForIndex(position);
//        Color color = (Color) thisItemColor;
//                this.imageView.setBackgroundColor(thisItemColor);
//                this.imageView.setBackgroundColor(ContextCompat.getColor(this.itemView.getContext(), R.color.material350Green));
//        viewHolder.imageButton.setBackgroundColor(colorsToUse[position]);
//        viewHolder.imageButton.setBackgroundColor(ContextCompat.getColor(holder, R.color.material350Green));
            }


        }

    }

    String getItem(int id) {
        return testStrings[id];
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
