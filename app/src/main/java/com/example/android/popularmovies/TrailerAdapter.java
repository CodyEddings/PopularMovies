package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.List;

/**
 * Created by Cody on 7/6/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private List<String> mYoutubeTrailerIDs;
    private Context mContext;

    /**
     * Cache of the children views for a trailer item.
     */
    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTrailerName;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            mTrailerName = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            itemView.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            final String youtubeVideoID = mYoutubeTrailerIDs.get(adapterPosition);
            final String youtubeURL = NetworkUtils.buildYoutubeURL(youtubeVideoID);

            Intent playVideoIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL));
            mContext.startActivity(playVideoIntent);
        }
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean attachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, attachToParentImmediately);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.mTrailerName.setText("Trailer " + Integer.toString(position));
    }

    @Override
    public int getItemCount() {
        if (mYoutubeTrailerIDs == null ) {
            return 0;
        }
        return mYoutubeTrailerIDs.size();    //return total number of trailers we have data for
    }

    public void setTrailerData(List<String> data) {
        mYoutubeTrailerIDs = data;
        notifyDataSetChanged();
    }
}
