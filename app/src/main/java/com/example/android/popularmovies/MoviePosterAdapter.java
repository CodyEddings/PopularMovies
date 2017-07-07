package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Cody on 3/15/2017.
 */


public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.MoviePosterAdapterViewHolder> {
    private List<Movie> mMovieData;
    private String basePosterURL = "http://image.tmdb.org/t/p/";
    private String size = "w780";

    private Context context;
        /*
        * An on-click handler that we've defined to make it easy for an Activity to interface with
        * our RecyclerView
        */
    private final MoviePosterAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviePosterAdapterOnClickHandler {
        void onClick(Movie movieData);

        boolean onOptionsItemsSelected(MenuItem item);
    }

    /**
     * Creates a MoviePostAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MoviePosterAdapter(MoviePosterAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a movie item.
     */
    public class MoviePosterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView posterImageView;

        public MoviePosterAdapterViewHolder(View view) {
            super(view);
            posterImageView = (ImageView) view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movieData = mMovieData.get(adapterPosition);
            mClickHandler.onClick(movieData);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MoviePosterAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviePosterAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MoviePosterAdapterViewHolder moviePosterAdapterViewHolder, int position) {
        String pathToPoster = mMovieData.get(position).posterPath;
        String posterPath = basePosterURL+size+pathToPoster;

        Picasso.with(context).load(posterPath).into(moviePosterAdapterViewHolder.posterImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null ) {
            return 0;
        }
        return mMovieData.size();    //return total number of movies we have data for
    }

    /**
     * This method is used to set the movie details on a MoviePosterAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MoviePosterAdapter to display it.
     *
     * @param data The new movie data to be displayed.
     */
    public void setmMovieData(List<Movie> data) {
        mMovieData = data;
        notifyDataSetChanged();
    }
}
