package com.example.android.popularmovies;

import org.parceler.Parcel;

/**
 * Created by Cody on 7/7/2017.
 */

@Parcel
public class Movie {
    public String releaseDate;
    public String plot;
    public String posterPath;
    public String title;
    public String rating;
    public String id;


    // empty constructor needed by the Parceler library
    public Movie(){

    }

    public Movie(String releaseDate, String plot, String posterPath, String title, String rating, String id) {
        this.releaseDate = releaseDate;
        this.plot = plot;
        this.posterPath = posterPath;
        this.title = title;
        this.rating = rating;
        this.id = id;
    }
}
