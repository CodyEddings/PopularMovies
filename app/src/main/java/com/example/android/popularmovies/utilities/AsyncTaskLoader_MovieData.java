package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.Movie;

import java.net.URL;
import java.util.List;

/**
 * Created by Cody on 7/7/2017.
 */

public class AsyncTaskLoader_MovieData extends AsyncTaskLoader<List<Movie>> {
    private String mSortMode;
    private List<Movie> mMovies;

    /* Never hold a reference to context directly from AsyncTaskLoader or you will
    leak the activity. Use getContext() instead when needed. */

    public AsyncTaskLoader_MovieData(Context context, String sortMode){
        super(context);
        mSortMode = sortMode;
    }

    @Override
    public List<Movie> loadInBackground() {
        URL movieRequestUrl = NetworkUtils.buildMovieUrl(mSortMode);
        try {
            String jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(movieRequestUrl);
            List<Movie> simpleJsonMovieData = MovieJsonUtils
                    .getMoviesFromJSON(getContext(), jsonMovieResponse);
            return simpleJsonMovieData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(List<Movie> movieData){
        if (isReset()){
            // The Loader has been reset; ignore the result and invalidate the data.
            if (movieData != null){
                return;
            }
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // It must be protected the new data has been delivered.
        List<Movie> previousMovies = mMovies;
        mMovies = movieData;

        if (isStarted()){
            // If the Loader is in a started state, have the superclass deliver the
            // results to the client.
            super.deliverResult(movieData);
        }
    }

    @Override
    protected void onStartLoading(){
        if (mMovies != null){
            deliverResult(mMovies);
        }
        else{
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading(){
        cancelLoad();
    }

    @Override
    protected void onReset(){
        onStopLoading();

        if (mMovies != null){
            mMovies = null;
        }
    }

    @Override
    public void onCanceled(List<Movie> movies){
        super.onCanceled(movies);
    }

    @Override
    public void forceLoad(){
        super.forceLoad();
    }
}
