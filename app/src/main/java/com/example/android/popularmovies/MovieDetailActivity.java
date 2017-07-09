package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.utilities.AsyncTaskLoader_ReviewData;
import com.example.android.popularmovies.utilities.AsyncTaskLoader_TrailerData;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by Cody on 3/26/2017.
 */

public class MovieDetailActivity extends AppCompatActivity {
    private String basePosterURL = "http://image.tmdb.org/t/p/";
    private String size = "w500";
    private String posterPath, plot, title, rating, releaseDate, movieId;;
    private Movie movie;

    private ProgressBar mTrailerLoadingProgress, mReviewLoadingProgress;
    private TextView mTitle, mPlot, mRating, mRelease, mTrailerLoadFailure, mReviewLoadFailure;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private RecyclerView mTrailerRecyclerView, mReviewRecyclerView;
    private ImageButton mFavorited;
    private ImageView mPoster;
    private boolean favorited = false;

    private static final int TRAILER_LOADER_ID = 8008;
    private static final int REVIEW_LOADER_ID = 7008;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mTitle = (TextView) findViewById(R.id.tv_movie_title);
        mRating = (TextView) findViewById(R.id.tv_user_rating);
        mRelease = (TextView) findViewById(R.id.tv_release_date);
        mPlot = (TextView) findViewById(R.id.tv_plot_summary);
        mPoster = (ImageView) findViewById(R.id.iv_movie_poster);
        mFavorited = (ImageButton) findViewById(R.id.ib_favorite);

        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        mTrailerLoadingProgress = (ProgressBar) findViewById(R.id.pb_trailer_loading_indicator);
        mTrailerLoadFailure = (TextView) findViewById(R.id.tv_trailer_error_message_display);

        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        mReviewLoadingProgress = (ProgressBar) findViewById(R.id.pb_review_loading_indicator);
        mReviewLoadFailure = (TextView) findViewById(R.id.tv_review_error_message_display);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);
        mTrailerRecyclerView.setLayoutManager(trailerLayoutManager);
        mTrailerAdapter = new TrailerAdapter();
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewAdapter = new ReviewAdapter();
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));

            plot = movie.plot;
            rating = movie.rating + "/10";
            title = movie.title;
            releaseDate = movie.releaseDate;
            movieId = movie.id;
            posterPath = basePosterURL + size + movie.posterPath;

            //load views with their respective movie data
            Picasso.with(getBaseContext()).load(posterPath).into(mPoster);
            mPlot.setText(plot);
            mRating.setText(rating);
            mTitle.setText(title);
            mRelease.setText(releaseDate);

            checkFavorited();

            mFavorited.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favorited) {
                        clearFavorite();
                    } else {
                        setFavorite(false);
                    }
                }
            });

            loadTrailers(movieId);
            loadReviews(movieId);
        }
    }

    private void loadTrailers(String id){
        mTrailerLoadingProgress.setVisibility(View.VISIBLE);

        Bundle trailerLoaderParams = new Bundle();
        trailerLoaderParams.putString("movieID", id);

        showTrailerView();
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, trailerLoaderParams, trailerLoaderListener);
    }

    private void loadReviews(String id){
        mReviewLoadingProgress.setVisibility(View.VISIBLE);

        Bundle reviewLoaderParams = new Bundle();
        reviewLoaderParams.putString("movieID", id);

        showReviewView();
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, reviewLoaderParams, reviewLoaderListener);
    }

    private void checkFavorited(){
        ContentResolver mContentResolver = this.getContentResolver();
        String selection = FavoritesContract.FavoritesEntry.MOVIE_TITLE + " =?";
        String[] projection = {
                FavoritesContract.FavoritesEntry._ID,
                FavoritesContract.FavoritesEntry.MOVIE_TITLE,
                FavoritesContract.FavoritesEntry.MOVIE_ID,
                FavoritesContract.FavoritesEntry.MOVIE_PLOT,
                FavoritesContract.FavoritesEntry.MOVIE_POSTER_PATH,
                FavoritesContract.FavoritesEntry.MOVIE_RELEASE_DATE,
                FavoritesContract.FavoritesEntry.MOVIE_RATING,
        };
        String[] args = {title};

        Cursor cursor = mContentResolver.query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                projection, selection, args, null);

        if (cursor.moveToFirst()){
            setFavorite(true);
        }
        else{
            clearFavorite();
        }
    }

    /**
     * Save new favorite to SQL via our Content Provider. Movie ID and Title are saved.
     */
    private void setFavorite(boolean alreadySet){
        favorited = true;
        mFavorited.setColorFilter(fetchAccentColor());

        if (!alreadySet) {
            /*call insert method for content provider to add a new row to SQL table for current
            favorited movie. ContentValues object stores all movie details for insertion into database */

            //Only save the endpoint of the poster path. Allows greater flexibility in changing other
            //parts of poster URL if desired, such as poster size.
            String[] posterPathParts = posterPath.split("/");
            String posterPathEndPoint = "/" + posterPathParts[posterPathParts.length-1];

            ContentValues mNewValues = new ContentValues();
            mNewValues.put(FavoritesContract.FavoritesEntry.MOVIE_TITLE, title);
            mNewValues.put(FavoritesContract.FavoritesEntry.MOVIE_ID, movieId);
            mNewValues.put(FavoritesContract.FavoritesEntry.MOVIE_PLOT, plot);
            mNewValues.put(FavoritesContract.FavoritesEntry.MOVIE_POSTER_PATH, posterPathEndPoint);
            mNewValues.put(FavoritesContract.FavoritesEntry.MOVIE_RELEASE_DATE, releaseDate);
            mNewValues.put(FavoritesContract.FavoritesEntry.MOVIE_RATING, rating);

            mUri = getContentResolver().insert(
                    FavoritesContract.FavoritesEntry.CONTENT_URI,
                    mNewValues
            );
        }
    }

    private void clearFavorite(){
        String mSelectionClause = FavoritesContract.FavoritesEntry.MOVIE_TITLE + " LIKE ?";
        String [] mSelectionArgs = {title};
        int rowsDeleted;

        favorited = false;
        mFavorited.setColorFilter(Color.parseColor("#616161"));

        ContentValues mNewValues = new ContentValues();
        mNewValues.put(FavoritesContract.FavoritesEntry.MOVIE_TITLE, title);
         rowsDeleted = getContentResolver().delete(
                FavoritesContract.FavoritesEntry.CONTENT_URI,
                mSelectionClause,
                mSelectionArgs
        );
    }

    //Display movie's trailers
    private void showTrailerView(){
        mTrailerLoadFailure.setVisibility(View.INVISIBLE);
        mTrailerRecyclerView.setVisibility(View.VISIBLE);
    }

    //Trailers didn't load. Display error message
    private void showTrailerErrorView(){
        mTrailerLoadFailure.setVisibility(View.VISIBLE);
        mTrailerRecyclerView.setVisibility(View.INVISIBLE);
    }

    //Display movie's reviews
    private void showReviewView(){
        mReviewLoadFailure.setVisibility(View.INVISIBLE);
        mReviewRecyclerView.setVisibility(View.VISIBLE);
    }

    //Reviews didn't load. Display error message
    private void showReviewErrorView(){
        mReviewLoadFailure.setVisibility(View.VISIBLE);
        mReviewRecyclerView.setVisibility(View.INVISIBLE);
    }

    /**
     * Returns the accent color for the app's current theme
     *
     * @return int code for app's accent color
     */
    public int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = this.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }


    /**
     *          Loader Callback methods for TRAILER data AsyncTask Loader. The loader is implemented
     *          as an anonymous inner class to allow MovieDetailActivity access to more than one
     *          loader.
     */
    private LoaderManager.LoaderCallbacks<List<String>> trailerLoaderListener
            = new LoaderManager.LoaderCallbacks<List<String>>() {
        @Override
        public Loader<List<String>> onCreateLoader(int id, Bundle args) {
            String trailersEndpoint = "videos";
            String filmID = args.getString("movieID");
            return new AsyncTaskLoader_TrailerData(getBaseContext(), trailersEndpoint, filmID);
        }

        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            mTrailerLoadingProgress.setVisibility(View.INVISIBLE);
            if (data != null){
                showTrailerView();
                mTrailerAdapter.setTrailerData(data);
            }else {
                showTrailerErrorView();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
        }
    };

    /**
     *          Loader Callback methods for REVIEW data AsyncTask Loader. The loader is implemented
     *          as an anonymous inner class to allow MovieDetailActivity access to more than one
     *          loader.
     */
    private LoaderManager.LoaderCallbacks<List<Review>> reviewLoaderListener
            = new LoaderManager.LoaderCallbacks<List<Review>>() {
        @Override
        public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
            String reviewsEndpoint = "reviews";
            String filmID = args.getString("movieID");
            return new AsyncTaskLoader_ReviewData(getBaseContext(), reviewsEndpoint, filmID);
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
            mReviewLoadingProgress.setVisibility(View.INVISIBLE);
            if (data != null){
                showReviewView();
                mReviewAdapter.setReviewData(data);
            }else {
                showReviewErrorView();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {
        }
    };
}
