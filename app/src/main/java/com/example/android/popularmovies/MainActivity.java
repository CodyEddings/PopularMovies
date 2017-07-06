package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.MoviePosterAdapter.MoviePosterAdapterOnClickHandler;
import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[]> {

    private RecyclerView mRecyclerView;
    private TextView mTextViewErrorDisplay;
    private ProgressBar mLoadingIndicator;
    private MoviePosterAdapter mPosterAdapter;

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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        prefSortMode = sharedPref.getString("sort_mode","popular");

        loadMovieData(prefSortMode);
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
    public void onClick(String singleMovieData) {
        Class destinationClass = MovieDetailActivity.class;
        Context context = this;
        Intent movieDetailIntent = new Intent(context, destinationClass);
        movieDetailIntent.putExtra(Intent.EXTRA_TEXT, singleMovieData);
        startActivity(movieDetailIntent);
    }

    @Override
    public boolean onOptionsItemsSelected(MenuItem item) {
        return false;
    }

    /**
     * Launches AsyncTask to load movie data on a background thread
     */
    private void loadMovieData(String sortMode){
        if (isOnline()){
            showMovieDataView();
            //new FetchMovieDataTask().execute(sortMode);

            Bundle loaderParams = new Bundle();
            loaderParams.putString("sortBy", sortMode);

            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, loaderParams, this);
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
        List<String> movieData = new ArrayList<>();

        ContentResolver mContentResolver = this.getContentResolver();
        String[] projection = {
                FavoritesContract.FavoritesEntry.MOVIE_DATA
        };

        Cursor cursor = mContentResolver.query(
                FavoritesContract.FavoritesEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor != null){
            int columnIndex = cursor.getColumnIndex(FavoritesContract.FavoritesEntry.MOVIE_DATA);
            while (cursor.moveToNext()){
                movieData.add(cursor.getString(columnIndex));
            }
            String[] mMovieData = movieData.toArray(new String[0]);
            mPosterAdapter.setmMovieData(mMovieData);
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
     * AsyncTask Loader for loading network JSON movie data
     * @param id
     * @param loaderArgs
     * @return
     */
    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle loaderArgs) {
        return new android.support.v4.content.AsyncTaskLoader<String[]>(this) {

            /* This String array will hold and help cache our movie data */
            String[] mMovieData = null;

            /* This String holds the sort type parameter passed in by the caller */
            String mSortMode = loaderArgs.getString("sortBy");

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                Log.d("AsyncTaskLoader: ", "start loading");
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from themoviedb.org in the background.
             *
             * @return Movie data from themoviedb.org as an array of Strings.
             *         null if an error occurs
             */
            @Override
            public String[] loadInBackground() {
                Log.d("AsyncTaskLoader: ", "load in background");
                URL movieRequestUrl = NetworkUtils.buildUrl(mSortMode);
                Log.d("AsyncTaskLoader: ", "mSortMode = " + mSortMode);
                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieRequestUrl);
                    String[] simpleJsonMovieData = MovieJsonUtils
                            .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);
                    Log.d("AsyncTaskLoader: ", "simpleJSON = " + simpleJsonMovieData);
                    return simpleJsonMovieData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(String[] data) {
                mMovieData = data;
                Log.d("AsyncTaskLoader: ", "result delivered");
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        Log.d("AsyncTaskLoader: ", "made it here");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null){
            Log.d("AsyncTaskLoader: ", "and here");
            showMovieDataView();
            mPosterAdapter.setmMovieData(data);

        } else{
            showErrorMessageView();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    /*//TODO: change to AsyncTask Loader
    public class FetchMovieDataTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }
            String sortBy = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sortBy);
            try{
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);
                String[] simpleJsonMovieData = MovieJsonUtils
                        .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);
                return simpleJsonMovieData;

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null){
                showMovieDataView();
                mPosterAdapter.setmMovieData(movieData);
            } else{
              showErrorMessageView();
            }
        }
    }*/

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
            //TODO: save preference
            loadMovieData("popular");
        }
        else if (id == R.id.sort_highest_rated){
            //TODO: save preference
            loadMovieData("top_rated");
        }
        else if (id == R.id.sort_favorites){
            sortByFavorites();
        }
        return super.onOptionsItemSelected(item);
    }
}
