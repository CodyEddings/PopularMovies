//package com.example.android.popularmovies.utilities;
//
//import android.content.AsyncTaskLoader;
//import android.content.Context;
//
//import com.example.android.popularmovies.Movie;
//
//import java.net.URL;
//import java.util.List;
//
///**
// * Created by Cody on 7/6/2017.
// */
//
//public class AsyncTaskLoader_TrailerData extends AsyncTaskLoader<List<String>> {
//    private String mApiTrailerEndpoint, mTrailerID;
//    private List<String> mStrings;
//
//    public AsyncTaskLoader_TrailerData(Context context, String apiTrailerEndpoint,
//                                       String trailerID) {
//        super(context);
//        mApiTrailerEndpoint = apiTrailerEndpoint;
//        mTrailerID = trailerID;
//    }
//
//    @Override
//    public List<String> loadInBackground() {
//        URL trailerRequestUrl = NetworkUtils.buildTrailerUrl(mApiTrailerEndpoint, mTrailerID);
//        try {
//            String jsonMovieResponse = NetworkUtils
//                    .getResponseFromHttpUrl(trailerRequestUrl);
//            List<Movie> simpleJsonMovieData = MovieJsonUtils
//                    .getMoviesFromJSON(getContext(), jsonMovieResponse);
//            //return simpleJsonMovieData;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//}
