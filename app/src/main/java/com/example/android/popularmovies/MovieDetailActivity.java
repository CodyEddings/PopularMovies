package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
    private String basePosterURL = "http://image.tmdb.org/t/p/";
    private String size = "w500";
    private String rawMovieData, plot, title, rating, releaseDate;

    private TextView mTitle, mPlot, mRating, mRelease;
    private ImageButton mFavorited;
    private ImageView mPoster;
    private boolean favorited = false;

    /*
     * The columns of data that we are interested in displaying within our DetailActivity's
     * weather display.
     */
    public static final String[] MOVIE_FAVORITE_PROJECTION = {
            FavoritesContract.FavoritesEntry.COLUMN_TITLE
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

        mUri = FavoritesContract.FavoritesEntry.CONTENT_URI;
        if (mUri == null) throw new NullPointerException("URI for MovieDetailActivity can't be null");
        getSupportLoaderManager().initLoader(ID_FAVORITE_LOADER, null, this);

        //TODO: checkFavorited() //read from SQL table and set favorited star

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                rawMovieData = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                String parts[] = rawMovieData.split("-");

                String posterPath = basePosterURL+size+parts[0];
                Picasso.with(getBaseContext()).load(posterPath).into(mPoster);
                for (int i =1; i<parts.length-5;i++){
                    plot = plot + parts[i];
                }
                rating = parts[parts.length-1]+"/10";
                title = parts[parts.length-2];
                releaseDate = parts[parts.length-5] + "-" + parts[parts.length-4] + "-" + parts[parts.length-3];

                mPlot.setText(plot);
                mRating.setText(rating);
                mTitle.setText(title);
                mRelease.setText(releaseDate);
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

    /**
     * Creates and returns a CursorLoader that loads the data for our URI and stores it in a Cursor.
     *
     * @param id The loader ID for which we need to create a loader
     * @param args Any arguments supplied by the caller
     *
     * @return A new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case ID_FAVORITE_LOADER:
                return  new CursorLoader(this,
                        mUri,
                        MOVIE_FAVORITE_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    /**
     * Runs on the main thread when a load is complete. If initLoader is called (we call it from
     * onCreate in DetailActivity) and the LoaderManager already has completed a previous load
     * for this Loader, onLoadFinished will be called immediately. Within onLoadFinished, we bind
     * the data to our views so the user can see the if they've favorited the current movie.
     *
     * @param loader The cursor loader that finished.
     * @param data   The cursor that is being returned.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
         /*
         * Before we bind the data to the UI that will display that data, we need to check the
         * cursor to make sure we have the results that we are expecting. In order to do that, we
         * check to make sure the cursor is not null and then we call moveToFirst on the cursor.
         * Although it may not seem obvious at first, moveToFirst will return true if it contains
         * a valid first row of data.
         *
         * If we have valid data, we want to continue on to bind that data to the UI. If we don't
         * have any data to bind, we just return from this method.
         */
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()){
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }
        String favoritedTitle = data.getString(INDEX_MOVIE_TITLE);
        setFavorite();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Save new favorite to SQL via our Content Provider. Also, update
     */
    private void setFavorite(){
        favorited = true;
        mFavorited.setColorFilter(fetchAccentColor());

        //call insert method for content provider to add a new row to SQL table for current
        // favorited movie
        ContentValues mNewValues = new ContentValues();
        mNewValues.put(FavoritesContract.FavoritesEntry.COLUMN_TITLE, title);
        mUri = getContentResolver().insert(
                FavoritesContract.FavoritesEntry.CONTENT_URI,
                mNewValues
        );
    }

    private void clearFavorite(){
        String mSelectionClause = FavoritesContract.FavoritesEntry.COLUMN_TITLE + " LIKE ?";
        String [] mSelectionArgs = {title};
        int rowsDeleted;

        favorited = false;
        mFavorited.setColorFilter(Color.parseColor("#616161"));

        ContentValues mNewValues = new ContentValues();
        mNewValues.put(FavoritesContract.FavoritesEntry.COLUMN_TITLE, title);
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
