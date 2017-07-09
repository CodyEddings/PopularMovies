package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Cody on 7/9/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> mReviews;
    private Context mContext;

    /**
     * Cache of the children views for a trailer item.
     */
    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView mReviewAuthor;
        private TextView mReviewContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mReviewContent = (TextView) itemView.findViewById(R.id.tv_review_content);
            mReviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
        }
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean attachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, attachToParentImmediately);
        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {
        String content = mReviews.get(position).content;
        holder.mReviewContent.setText(content);

        String author = mReviews.get(position).author;
        String formattedAuthor = "-" + author;
        holder.mReviewAuthor.setText(formattedAuthor);
    }

    @Override
    public int getItemCount() {
        if (mReviews == null ) {
            return 0;
        }
        return mReviews.size();    //return total number of trailers we have data for
    }

    public void setReviewData(List<Review> data) {
        mReviews = data;
        notifyDataSetChanged();
    }
}

