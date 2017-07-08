package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoritesContract;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

/**
 * Created by Cody on 3/26/2017.
 */

public class MovieDetailActivity extends AppCompatActivity {
    private String basePosterURL = "http://image.tmdb.org/t/p/";
    private String size = "w500";
    private String posterPath, plot, title, rating, releaseDate, movieId;;
    private Movie movie;

    private ProgressBar mPBTrailerLoad;
    private TextView mTitle, mPlot, mRating, mRelease;
    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mRecyclerView;
    private ImageButton mFavorited;
    private ImageView mPoster;
    private boolean favorited = false;

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
        mPBTrailerLoad = (ProgressBar) findViewById(R.id.pb_trailer_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);

        int numColumns = 1;
        GridLayoutManager layoutManager = new GridLayoutManager(this,numColumns);
        mRecyclerView.setLayoutManager(layoutManager);

        mTrailerAdapter = new TrailerAdapter();
        mRecyclerView.setAdapter(mTrailerAdapter);

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
        }
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

    private void fetchTrailers(){

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
}
