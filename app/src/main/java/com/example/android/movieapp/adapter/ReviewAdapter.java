package com.example.android.movieapp.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.movieapp.R;
import com.example.android.movieapp.databinding.ReviewItemBinding;
import com.example.android.movieapp.models.ReviewResponse.Review;

import java.util.List;



/**
 * Created by Little Princess on 3/27/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    public interface ReviewClickListener{
        void onReviewClickListener(int position);
    }

    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private ReviewClickListener listener;
    private List<Review> list;
    ReviewItemBinding mBinding;

    public ReviewAdapter(ReviewClickListener listener, List<Review> list) {
        this.listener = listener;
        this.list = list;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        mBinding= DataBindingUtil.inflate(inflater,R.layout.review_item,parent,false);
        return new ReviewViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bindView(position);
        holder.mBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ReviewItemBinding mBinding;
        ReviewViewHolder(ReviewItemBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding=mBinding;
            mBinding.getRoot().setOnClickListener(this);
        }

        void bindView(int position){

            Review currentReview= list.get(position);
            mBinding.author.setText(currentReview.getAuthor());
            mBinding.content.setText(currentReview.getContent());

            Log.i(TAG ," review author : "+ currentReview.getAuthor());
            Log.i(TAG ," review content : "+ currentReview.getContent());
        }

        @Override
        public void onClick(View view) {
            listener.onReviewClickListener(getAdapterPosition());
        }
    }
}
