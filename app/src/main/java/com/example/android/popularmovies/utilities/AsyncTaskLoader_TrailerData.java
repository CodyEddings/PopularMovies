package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.net.URL;
import java.util.List;

/**
 * Created by Cody on 7/6/2017.
 */

public class AsyncTaskLoader_TrailerData extends AsyncTaskLoader<List<String>> {
    private String mApiTrailerEndpoint, mMovieID;
    private List<String> mYoutubeTrailerIDs;

    public AsyncTaskLoader_TrailerData(Context context, String apiTrailerEndpoint,
                                       String trailerID) {
        super(context);
        mApiTrailerEndpoint = apiTrailerEndpoint;
        mMovieID = trailerID;
    }

    @Override
    public List<String> loadInBackground() {
        URL trailerRequestUrl = NetworkUtils.buildTrailerUrl(mApiTrailerEndpoint, mMovieID);
        try {
            String jsonTrailerResponse = NetworkUtils
                    .getResponseFromHttpUrl(trailerRequestUrl);
            List<String> jsonYoutubeTrailerKeys = JsonUtils
                    .getTrailersFromJSON(jsonTrailerResponse);
            return jsonYoutubeTrailerKeys;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(List<String> trailerData){
        if (isReset()){
            // The Loader has been reset; ignore the result and invalidate the data.
            if (trailerData != null){
                return;
            }
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // It must be protected the new data has been delivered.
        List<String> previousTrailers = mYoutubeTrailerIDs;
        mYoutubeTrailerIDs = trailerData;

        if (isStarted()){
            // If the Loader is in a started state, have the superclass deliver the
            // results to the client.
            super.deliverResult(trailerData);
        }
     }

    @Override
    protected void onStartLoading(){
        if (mYoutubeTrailerIDs != null){
            deliverResult(mYoutubeTrailerIDs);
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

        if (mYoutubeTrailerIDs != null){
            mYoutubeTrailerIDs = null;
        }
    }

    @Override
    public void onCanceled(List<String> trailers){
        super.onCanceled(trailers);
    }

    @Override
    public void forceLoad(){
        super.forceLoad();
    }
}
