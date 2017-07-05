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
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoritesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Cody on 3/26/2017.
 */

public class MovieDetailActivity extends AppCompatActivity {
    private String basePosterURL = "http://image.tmdb.org/t/p/";
    private String size = "w500";
    private String rawMovieData, plot, title, rating, releaseDate;

    private TextView mTitle, mPlot, mRating, mRelease;
    private ImageButton mFavorited;
    private ImageView mPoster;
    private String movieId;
    private boolean favorited = false;

    public static final String[] MOVIE_FAVORITE_PROJECTION = {
            FavoritesContract.FavoritesEntry.MOVIE_TITLE
    };

    public static final int INDEX_MOVIE_TITLE = 0;

    public static final int ID_FAVORITE_LOADER = 3548;

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

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                rawMovieData = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                String parts[] = rawMovieData.split("-");

                String posterPath = basePosterURL+size+parts[0];
                Picasso.with(getBaseContext()).load(posterPath).into(mPoster);

                /*plot = "";
                for (int i =1; i<parts.length-6;i++){
                    plot = plot + parts[i];
                }*/
                plot = parts[parts.length-7];
                rating = parts[parts.length-2]+"/10";
                title = parts[parts.length-3];
                releaseDate = parts[parts.length-6];
                movieId = parts[parts.length-1];

                mPlot.setText(plot);
                mRating.setText(rating);
                mTitle.setText(title);
                mRelease.setText(releaseDate);

                checkFavorited();
            }

            mFavorited.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favorited){
                        clearFavorite();
                    }
                    else {
                        setFavorite();
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
                FavoritesContract.FavoritesEntry.MOVIE_TITLE
        };
        String[] args = {title};

        Cursor cursor = mContentResolver.query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                projection, selection, args, null);

        if (cursor.moveToFirst()){
            setFavorite();
        }
        else{
            clearFavorite();
        }
    }

    /**
     * Save new favorite to SQL via our Content Provider. Movie ID and Title are saved.
     */
    private void setFavorite(){
        favorited = true;
        mFavorited.setColorFilter(fetchAccentColor());

        //call insert method for content provider to add a new row to SQL table for current
        // favorited movie
        ContentValues mNewValues = new ContentValues();
        mNewValues.put(FavoritesContract.FavoritesEntry.MOVIE_TITLE, title);    //save movie title
        mNewValues.put(FavoritesContract.FavoritesEntry.MOVIE_ID, movieId);     //save movie id
        mUri = getContentResolver().insert(
                FavoritesContract.FavoritesEntry.CONTENT_URI,
                mNewValues
        );
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
