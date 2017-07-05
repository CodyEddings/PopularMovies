package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MoviePosterAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private TextView mTextViewErrorDisplay;
    private ProgressBar mLoadingIndicator;
    private MoviePosterAdapter mPosterAdapter;

    private String prefSortMode;

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
        //prefSortMode = sharedPref.getString("sort_mode","popularity.desc");

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
        showMovieDataView();
        new FetchMovieDataTask().execute(sortMode);
    }

    //Show grid of movie posters
    private void showMovieDataView(){
        mTextViewErrorDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    //Show network connection error message
    private void showErrorMessage(){
        mTextViewErrorDisplay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    //TODO: change to AsyncTask Loader
    public class FetchMovieDataTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            /*
             *   If there's no network connectivity then show the error message and exit task
             */
            if (isOnline() == false){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showErrorMessage();
                    }
                });
                return null;
            }

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
              showErrorMessage();
            }
        }
    }

    //checks if connection to internet is active
    //referenced from stackoverflow answer:
    //http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
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
            loadMovieData("popular");
        }
        else if (id == R.id.sort_highest_rated){
            loadMovieData("top_rated");
        }
        else if (id == R.id.sort_favorites){
            //TODO: Sort by favorites
        }
        return super.onOptionsItemSelected(item);
    }
}
