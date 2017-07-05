package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Cody on 7/2/2017.
 */

public class FavoritesContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for PopularMovies.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that PopularMovies
     * can handle. For instance,
     *
     *     content://com.example.android.popularmovies/favorites
     *     [           BASE_CONTENT_URI         ][ PATH_FAVORITES]
     *
     * is a valid path for looking at favorites data.
     */
    public static final String PATH_FAVORITES = "favorites";

    /* Inner class that defines the table contents of the favorites table */
    public static final class FavoritesEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the favorite movie table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        /* Used internally as the name of our weather table. */
        public static final String TABLE_NAME = "favorite_movies";

        /* Title of the movie that has been favorited*/
        public static final String MOVIE_TITLE = "title";

        /* ID of the movie that has been favorited*/
        public static final String MOVIE_ID = "id";
    }
}
