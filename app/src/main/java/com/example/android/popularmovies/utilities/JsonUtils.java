package com.example.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cody on 3/21/2017.
 */

public class JsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Movies
     * describing the details of queried movies, with fields such as plot, rating, title, etc.
     * <p/>
     *
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Movie> getMoviesFromJSON(Context context, String movieJsonStr)
            throws JSONException {

        /* Movie information. Each movie's collective detail info is an element of the parsedMovieData array */
        final String OWM_RESULTS = "results";
        final String OWM_TITLE = "title";
        final String OWM_SYNOPSIS = "overview";
        final String OWM_RELEASE = "release_date";
        final String OWM_POSTER = "poster_path";
        final String OWM_RATING = "vote_average";
        final String OWM_ID = "id";

        final String OWM_MESSAGE_CODE = "cod";

        /* Array of Movie objects to hold each movie's detail information */
        List<Movie> parsedMovieData = null;


        JSONObject movieJSON = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJSON.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJSON.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    /* Successful connection to server */
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        //parent array for movie results
        JSONArray movieArray = movieJSON.getJSONArray(OWM_RESULTS);

        parsedMovieData = new ArrayList<>();

        for (int i = 0; i < movieArray.length(); i++) {
            String releaseDate;
            String plot;
            String posterPath;
            String title;
            String rating;
            String id;

            /* Get the JSON object representing a movie's data */
            JSONObject movieData = movieArray.getJSONObject(i);

            posterPath = movieData.getString(OWM_POSTER);
            plot = movieData.getString(OWM_SYNOPSIS);
            releaseDate = movieData.getString(OWM_RELEASE);
            title = movieData.getString(OWM_TITLE);
            rating = movieData.getString(OWM_RATING);
            id = movieData.getString(OWM_ID);

            parsedMovieData.add(new Movie(releaseDate, plot, posterPath, title, rating, id));
        }

        return parsedMovieData;
    }

    public static List<String> getTrailersFromJSON(Context context, String trailerJsonStr){
        return null; //TODO: finish
    }
}
