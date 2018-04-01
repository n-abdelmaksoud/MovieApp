
package com.example.android.movieapp.adapter;

import android.app.Activity;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.movieapp.R;
import com.example.android.movieapp.databinding.MovieItemBinding;
import com.example.android.movieapp.fetchingdata.MovieImagesFullPath;

import com.example.android.movieapp.models.MovieResponse;
import com.example.android.movieapp.moviesdatabase.MoviesContract.FavoritesEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Little Princess on 2/22/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.CustomViewHolder> {


    private static final String TAG = MovieAdapter.class.getSimpleName();
    private MovieClickListener movieClickListener;
    private List<MovieResponse.MovieModel> list;
    private Cursor cursor;

    public interface MovieClickListener {
        void onMovieClickListener(int position);
    }

    public MovieAdapter(MovieClickListener listener, List<MovieResponse.MovieModel> list) {
        this.movieClickListener = listener;
        this.list = list;
    }

    public MovieAdapter(MovieClickListener listener, Cursor cursor) {
        this.movieClickListener = listener;
        this.cursor=cursor;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
           MovieItemBinding mBinding =DataBindingUtil.inflate(layoutInflater,R.layout.movie_item,parent,false);
            return new CustomViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bindView(position);
        holder.mBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if(list!=null) return list.size();
        else if (cursor!=null)  return cursor.getCount();
        else  return 0;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MovieItemBinding mBinding;

            CustomViewHolder(MovieItemBinding mBinding) {
                super(mBinding.getRoot());
                this.mBinding=mBinding;
                mBinding.getRoot().setOnClickListener(this);
            }


        void bindView(int position) {
                String title=null;
                String posterImage=null;
                String posterImageFullPath=null;

                if(list!=null ) {
                    MovieResponse.MovieModel movie = list.get(position);
                    Log.i(TAG,"movie title: "+movie.getTitle() +"  movie id: "+movie.getId());
                    title= movie.getTitle();
                    posterImage= movie.getPosterImage();

                } else if(cursor!= null) {
                    cursor.moveToPosition(position);
                    int columnTitleIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE);
                    int columnPosterImage = cursor.getColumnIndex(FavoritesEntry.COLUMN_POSTER_IMAGE);
                    title = cursor.getString(columnTitleIndex);
                    posterImage = cursor.getString(columnPosterImage);
                }

            mBinding.youtubeLogo.setVisibility(View.GONE);

            posterImageFullPath = MovieImagesFullPath.getPosterImageFullPath(posterImage);

            mBinding.title.setText(title);

            Picasso.with((Activity) movieClickListener).load(posterImageFullPath).error(R.drawable.error).placeholder(R.drawable.placeholder)
                    .resizeDimen(R.dimen.poster_image_width, R.dimen.poster_image_height).onlyScaleDown().centerCrop().into(mBinding.posterImage);

        }


        @Override
        public void onClick(View v) {
            Log.i(TAG,"clicked item position = "+getAdapterPosition());
            movieClickListener.onMovieClickListener(getAdapterPosition());
        }
    }


}


