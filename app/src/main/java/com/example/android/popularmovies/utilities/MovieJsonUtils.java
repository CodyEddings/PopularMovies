package com.example.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by Cody on 3/21/2017.
 */

public class MovieJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static String[] getSimpleMovieStringsFromJson(Context context, String movieJsonStr)
            throws JSONException {

        /* Movie information. Each day's forecast info is an element of the "list" array */
        final String OWM_RESULTS = "results";

        final String OWM_MOVIE = "movie";

        final String OWM_TITLE = "title";
        final String OWM_SYNOPSIS = "overview";
        final String OWM_POPULARITY = "popularity";
        final String OWM_RELEASE = "release_date";
        final String OWM_POSTER = "poster_path";
        final String OWM_RATING = "vote_average";

        final String OWM_MESSAGE_CODE = "cod";

        /* String array to hold each day's weather String */
        String[] parsedMovieData = null;

        JSONObject movieJSON = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJSON.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJSON.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
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

        parsedMovieData = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            String releaseDate;
            String plot;
            String posterPath;
            String title;
            String rating;

            /* Get the JSON object representing the day */
            JSONObject movieData = movieArray.getJSONObject(i);

            posterPath = movieData.getString(OWM_POSTER);
            plot = movieData.getString(OWM_SYNOPSIS);
            releaseDate = movieData.getString(OWM_RELEASE);
            title = movieData.getString(OWM_TITLE);
            rating = movieData.getString(OWM_RATING);

            parsedMovieData[i] = posterPath + " - " + plot + " - " + releaseDate
                    + " - " + title + " - " + rating;
        }

        return parsedMovieData;
    }

    /**
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     *
     * @param context         An application context, such as a service or activity context.
     * @param forecastJsonStr The JSON to parse into ContentValues.
     *
     * @return An array of ContentValues parsed from the JSON.
     */
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** This will be implemented in a future lesson **/
        return null;
    }
}
