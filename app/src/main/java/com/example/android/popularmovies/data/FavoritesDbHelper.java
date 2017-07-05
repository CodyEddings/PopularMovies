package com.example.android.popularmovies.data;

/**
 * Created by Cody on 7/2/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.FavoritesContract.FavoritesEntry;

/**
 * Manages a local database for a user's favorite movies
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "favorites.db";

    private static final int DATABASE_VERSION = 3;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
         /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_FAVORITES_TABLE =

                "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +

                /*
                 * FavoritesEntry did not explicitly declare a column called "_ID". However,
                 * FavoritesEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        FavoritesEntry._ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoritesEntry.MOVIE_TITLE      + " STRING NOT NULL, "                   +
                        FavoritesEntry.MOVIE_ID         + " INTEGER NOT NULL, "                  +
                        FavoritesEntry.MOVIE_DATA       + " INTEGER NOT NULL, "                  +

                /*
                 * To ensure this table can only contain one weather entry per date, we declare
                 * the date column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                 * SQLite that if we have a weather entry for a certain date and we attempt to
                 * insert another weather entry with that date, we replace the old weather entry.
                 */
                        " UNIQUE (" + FavoritesEntry.MOVIE_TITLE + ") ON CONFLICT REPLACE);";
        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
