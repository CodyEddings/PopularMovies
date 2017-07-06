package com.example.android.popularmovies;

/**
 * Created by Cody on 7/5/2017.
 */

public class Movie {

    String releaseDate;
    String plot;
    String posterPath;
    String title;
    String rating;
    String id;

    public Movie(String inputReleaseDate, String inputPlot, String inputPosterPath,
                 String inputTitle, String inputRating, String inputID) {

        releaseDate = inputReleaseDate;
        plot = inputPlot;
        posterPath = inputPosterPath;
        title = inputTitle;
        rating = inputRating;
        id = inputID;
    }
}
