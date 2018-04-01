package com.example.android.movieapp.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.android.movieapp.R;
import com.example.android.movieapp.databinding.MovieItemBinding;
import com.example.android.movieapp.models.VideoTrailersResponse.Video;
import com.squareup.picasso.Picasso;

import java.util.List;



/**
 * Created by Little Princess on 3/27/2018.
 */

public class VideoTrailersAdapter extends RecyclerView.Adapter<VideoTrailersAdapter.VideoTrailersViewHolder> {

    public interface VideoTrailersClickListener{
        void onVideoTrailersClickListener(int position);
    }

    private static final String YOUTUBE_PATH="https://www.youtube.com/watch";
    private static final Uri BASE_PHOTO_PATH= Uri.parse(YOUTUBE_PATH);
    private VideoTrailersClickListener listener;
    private List<Video> list;
    private MovieItemBinding mBinding;

    public VideoTrailersAdapter(VideoTrailersClickListener listener, List<Video> list) {
        this.listener = listener;
        this.list = list;
    }

    @Override
    public VideoTrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        mBinding= DataBindingUtil.inflate(inflater, R.layout.movie_item,parent,false);
        return new VideoTrailersViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(VideoTrailersViewHolder holder, int position) {
        holder.bindView(position);
        holder.mBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VideoTrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private MovieItemBinding mBinding;

       VideoTrailersViewHolder(MovieItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mBinding=itemBinding;
            mBinding.getRoot().setOnClickListener(this);
        }

        void bindView(int position){

            Video video = list.get(position);
            String key=video.getKey();
            String path = String.format(mBinding.getRoot().getContext().getString(R.string.video_thumbnail_url),key);

            Picasso.with((Activity)listener).load(R.drawable.youtube_logo).error(R.drawable.error).placeholder(R.drawable.placeholder)
                    .resizeDimen(R.dimen.youtube_logo_width,R.dimen.youtube_logo_height)
                    .onlyScaleDown().centerCrop().into(mBinding.youtubeLogo);

          //  String imgUrl = "http://img.youtube.com/vi/"+key + "/0.jpg";
            Picasso.with((Activity)listener).load(path).error(R.drawable.error).placeholder(R.drawable.placeholder)
                  .resizeDimen(R.dimen.video_thumbnail_width,R.dimen.video_thumbnail_height)
                    .onlyScaleDown().centerCrop().into(mBinding.posterImage);

            mBinding.title.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            listener.onVideoTrailersClickListener(getAdapterPosition());
        }
    }
}
