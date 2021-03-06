package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.MoviePosterAdapter.MoviePosterAdapterOnClickHandler;
import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.utilities.AsyncTaskLoader_MovieData;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>> {

    private RecyclerView mRecyclerView;
    private TextView mTextViewErrorDisplay;
    private ProgressBar mLoadingIndicator;
    private MoviePosterAdapter mPosterAdapter;
    private SharedPreferences sharedPref;

    private String prefSortMode;

    private static final int MOVIE_LOADER_ID = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mTextViewErrorDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager layoutManager =
                new GridLayoutManager(this,numberOfColumns());  //calls numberOfColumns to dynamically fit to individual device width
        mRecyclerView.setLayoutManager(layoutManager);

        mPosterAdapter = new MoviePosterAdapter(this);
        mRecyclerView.setAdapter(mPosterAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //Restore preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        prefSortMode = sharedPref.getString(getString(R.string.pref_key_sort),"popular");

        if (prefSortMode.equals("popular") || prefSortMode.equals("top_rated")){
            boolean firstRun = true;
            loadMovieData(prefSortMode, firstRun);
        }
        else if (prefSortMode.equals("favorite")){
            sortByFavorites();
        }
    }


    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 800;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    @Override
    public void onClick(Movie singleMovieData) {
        Class destinationClass = MovieDetailActivity.class;
        Context context = this;
        Intent movieDetailIntent = new Intent(context, destinationClass);
        movieDetailIntent.putExtra("movie", Parcels.wrap(singleMovieData));
        startActivity(movieDetailIntent);
    }

    @Override
    public boolean onOptionsItemsSelected(MenuItem item) {
        return false;
    }

    /**
     * Launches AsyncTask to load movie data on a background thread
     */
    private void loadMovieData(String sortMode, boolean firstRun){
        if (isOnline()){
            mLoadingIndicator.setVisibility(View.VISIBLE);

            showMovieDataView();
            //new FetchMovieDataTask().execute(sortMode);

            Bundle loaderParams = new Bundle();
            loaderParams.putString("sortBy", sortMode);

            if (firstRun) {
                getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, loaderParams, this);
            }
            else {
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, loaderParams, this);
            }
        }
        else {
            showErrorMessageView();
        }
    }

    /**
     * Makes the user's favorited movies visible by reading JSON strings for all favorited movies
     * from SQL table.
     */
    private void sortByFavorites(){
        List<Movie> mMovieData = new ArrayList<>();

        ContentResolver mContentResolver = this.getContentResolver();
        String[] projection = {
                FavoritesContract.FavoritesEntry.MOVIE_TITLE,
                FavoritesContract.FavoritesEntry.MOVIE_ID,
                FavoritesContract.FavoritesEntry.MOVIE_PLOT,
                FavoritesContract.FavoritesEntry.MOVIE_RATING,
                FavoritesContract.FavoritesEntry.MOVIE_RELEASE_DATE,
                FavoritesContract.FavoritesEntry.MOVIE_POSTER_PATH,
        };

        Cursor cursor = mContentResolver.query(
                FavoritesContract.FavoritesEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor != null){
            int titleColumn = cursor.getColumnIndex(FavoritesContract.FavoritesEntry.MOVIE_TITLE);
            int idColumn = cursor.getColumnIndex(FavoritesContract.FavoritesEntry.MOVIE_ID);
            int plotColumn= cursor.getColumnIndex(FavoritesContract.FavoritesEntry.MOVIE_PLOT);
            int posterColumn = cursor.getColumnIndex(FavoritesContract.FavoritesEntry.MOVIE_POSTER_PATH);
            int ratingColumn = cursor.getColumnIndex(FavoritesContract.FavoritesEntry.MOVIE_RATING);
            int releaseColumn = cursor.getColumnIndex(FavoritesContract.FavoritesEntry.MOVIE_RELEASE_DATE);

            while (cursor.moveToNext()){
                String release = cursor.getString(releaseColumn);
                String plot = cursor.getString(plotColumn);
                String posterPath = cursor.getString(posterColumn);
                String title = cursor.getString(titleColumn);
                String rating = cursor.getString(ratingColumn);
                String id = cursor.getString(idColumn);

                mMovieData.add(new Movie(release, plot, posterPath, title, rating, id));
            }

            mPosterAdapter.setMovieData(mMovieData);
        }
    }

    //Show grid of movie posters
    private void showMovieDataView(){
        mTextViewErrorDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    //Show network connection error message
    private void showErrorMessageView(){
        mTextViewErrorDisplay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    /**
     * Check if device has an active connection to the internet
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movies_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.sort_popularity) {
            setSharedPref("popular");               //save sorting preference
            boolean firstRun = false;
            loadMovieData("popular", firstRun);     //load movie data (popular)
        }
        else if (id == R.id.sort_highest_rated){
            setSharedPref("top_rated");             //save sorting preference
            boolean firstRun = false;
            loadMovieData("top_rated", firstRun);   //load movie data (top rated)
        }
        else if (id == R.id.sort_favorites){
            setSharedPref("favorite");              //save sorting preference
            sortByFavorites();                      //load movie data (favorites)
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSharedPref(String sortMode){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_key_sort), sortMode);
        editor.commit();
    }

    /**
     *
     *
     *          Loader Callback methods for MovieData AsyncTask Loader
     *
     */
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        String mSortMode = args.getString("sortBy");
        return new AsyncTaskLoader_MovieData(this, mSortMode);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null){
            showMovieDataView();
            mPosterAdapter.setMovieData(data);

        } else{
            showErrorMessageView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }
}
