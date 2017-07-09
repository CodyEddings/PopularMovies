package com.example.android.popularmovies.utilities;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.Review;

import java.net.URL;
import java.util.List;

/**
 * Created by Cody on 7/9/2017.
 */

public class AsyncTaskLoader_ReviewData extends AsyncTaskLoader<List<Review>> {
    private String mReviewEndpoint, mMovieID;
    private List<Review> mReviews;

    public AsyncTaskLoader_ReviewData(Context context, String apiTrailerEndpoint,
            String trailerID) {
        super(context);
        mReviewEndpoint = apiTrailerEndpoint;
        mMovieID = trailerID;
    }

    @Override
    public List<Review> loadInBackground() {
        URL reviewRequestURL = NetworkUtils.buildReviewUrl(mReviewEndpoint, mMovieID);
        try {
            String jsonReviewResponse = NetworkUtils
                    .getResponseFromHttpUrl(reviewRequestURL);
            List<Review> jsonReviews = JsonUtils
                    .getReviewsFromJSON(jsonReviewResponse);
            return jsonReviews;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(List<Review> reviewData){
        if (isReset()){
            // The Loader has been reset; ignore the result and invalidate the data.
            if (reviewData != null){
                return;
            }
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // It must be protected the new data has been delivered.
        List<Review> previousReviews = mReviews;
        mReviews = reviewData;

        if (isStarted()){
            // If the Loader is in a started state, have the superclass deliver the
            // results to the client.
            super.deliverResult(reviewData);
        }
    }

    @Override
    protected void onStartLoading(){
        if (mReviews != null){
            deliverResult(mReviews);
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

        if (mReviews != null){
            mReviews = null;
        }
    }

    @Override
    public void onCanceled(List<Review> reviews){
        super.onCanceled(reviews);
    }

    @Override
    public void forceLoad(){
        super.forceLoad();
    }
}
