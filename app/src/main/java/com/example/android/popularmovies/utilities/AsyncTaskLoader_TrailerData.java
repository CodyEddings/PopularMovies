package com.example.android.popularmovies.utilities;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by Cody on 7/6/2017.
 */

public class AsyncTaskLoader_TrailerData extends AsyncTaskLoader {

    public AsyncTaskLoader_TrailerData(Context context) {
        super(context);
    }

    @Override
    public String[] loadInBackground() {
        return null;
    }
}
