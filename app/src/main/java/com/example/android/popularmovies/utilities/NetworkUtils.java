package com.example.android.popularmovies.utilities;

/**
 * Created by Cody on 3/21/2017.
 */

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String THEMOVIEDB_API_KEY = "22d22a9d610ad3f3ccf423152f7dc18d";
    private static final String YOUTUBE_API_KEY = "AIzaSyCVVEBfnvvFd4QmF2TieDGW7J9yIQHKY_4";
    private static final String QUERY_BASE_URL = "https://api.themoviedb.org/3/movie/";

    /* The format we want our API to return */
    private static final String format = "json";

    final static String ADULT_PARAM = "include_adult";
    final static String LANGUAGE_PARAM = "language";

    /**
     * Builds the URL used to talk to the movie server
     * @return The URL to use to query the movie server.
     */
    public static URL buildMovieUrl(String endPoint) {
        String QUERY_RAW_URL;
        QUERY_RAW_URL = QUERY_BASE_URL + endPoint + "?api_key=" + THEMOVIEDB_API_KEY;
        Uri builtUri = Uri.parse(QUERY_RAW_URL).buildUpon()
                .appendQueryParameter(ADULT_PARAM, "false")
                .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildTrailerUrl(String endPoint, String id){
        String QUERY_RAW_URL;
        QUERY_RAW_URL = QUERY_BASE_URL + id + "/" + endPoint + "?api_key=" + THEMOVIEDB_API_KEY;
        Uri builtUri = Uri.parse(QUERY_RAW_URL).buildUpon()
                .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}