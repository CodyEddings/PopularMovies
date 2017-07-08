package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Cody on 7/6/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private TextView mTrailerName;
    private List<String> mTrailerYoutubeID;
    private Context mContext;

    /**
     * Cache of the children views for a trailer item.
     */
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TrailerAdapterViewHolder(View view) {
            super(view);
            mTrailerName = (TextView) view.findViewById(R.id.tv_trailer_name);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            //TODO: launch youtube intent
           /* Intent playVideoIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(watchYoutubeURl));
            mContext.startActivity(playVideoIntent);*/
        }
    }

    @Override
    public TrailerAdapter.TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean attachToParentImmediatly = false;

        View view = inflater.inflate(layoutIdForListItem, parent, attachToParentImmediatly);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerAdapterViewHolder holder, int position) {
        String trailerNum = Integer.toString(position);
        mTrailerName.setText("Trailer " + trailerNum);
    }

    @Override
    public int getItemCount() {
        if (mTrailerYoutubeID == null ) {
            return 0;
        }
        return mTrailerYoutubeID.size();    //return total number of trailers we have data for
    }

    public void setTrailerData(List<String> data) {
        mTrailerYoutubeID = data;
        notifyDataSetChanged();
    }
}
