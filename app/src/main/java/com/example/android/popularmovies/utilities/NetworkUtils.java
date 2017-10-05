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

    private static final String THEMOVIEDB_API_KEY = ""; //TODO: INSERT YOUR PRIVATE API KEY FROM THEMOVIEDB.ORG 
    private static final String THEMOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    /* The format we want our API to return */
    private static final String format = "json";

    final static String ADULT_PARAM = "include_adult";
    final static String LANGUAGE_PARAM = "language";

    /**
     * Builds URL used to get details for a movie from themoviedb.org
     *
     * @return The URL to use to query the movie server.
     */
    public static URL buildMovieUrl(String endPoint) {
        String QUERY_RAW_URL;
        QUERY_RAW_URL = THEMOVIEDB_BASE_URL + endPoint + "?api_key=" + THEMOVIEDB_API_KEY;
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

    /**
     * Builds URL used to get trailers for a movie from themoviedb.org
     *
     * @param endPoint
     * @param id
     * @return
     */
    public static URL buildTrailerUrl(String endPoint, String id){
        String QUERY_RAW_URL;
        QUERY_RAW_URL = THEMOVIEDB_BASE_URL + id + "/" + endPoint + "?api_key=" + THEMOVIEDB_API_KEY;
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
     * Builds URL used to get reviews for a movie from themoviedb.org
     *
     * @param endPoint
     * @param id
     * @return
     */
    public static URL buildReviewUrl(String endPoint, String id){
        String QUERY_RAW_URL;
        QUERY_RAW_URL = THEMOVIEDB_BASE_URL + id + "/" + endPoint + "?api_key=" + THEMOVIEDB_API_KEY;
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

    public static String buildYoutubeURL(String youtubeID) {
        return YOUTUBE_BASE_URL + youtubeID;
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
