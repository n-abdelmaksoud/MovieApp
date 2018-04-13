package com.example.android.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.android.movieapp.adapter.ReviewAdapter;
import com.example.android.movieapp.adapter.VideoTrailersAdapter;
import com.example.android.movieapp.connection.CheckingConnection;
import com.example.android.movieapp.databinding.ActivityDetailsBinding;
import com.example.android.movieapp.fetchingdata.ApiClient;
import com.example.android.movieapp.fetchingdata.MovieImagesFullPath;
import com.example.android.movieapp.fetchingdata.MovieService;
import com.example.android.movieapp.models.MovieResponse;
import com.example.android.movieapp.models.ReviewResponse;
import com.example.android.movieapp.models.ReviewResponse.Review;
import com.example.android.movieapp.models.VideoTrailersResponse;
import com.example.android.movieapp.models.VideoTrailersResponse.Video;
import com.example.android.movieapp.moviesdatabase.MoviesContract.FavoritesEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.movieapp.fetchingdata.MovieAPIKey.API_KEY;

/**
 * Created by Little Princess on 2/26/2018.
 */

public class DetailsActivity extends AppCompatActivity implements ReviewAdapter.ReviewClickListener, VideoTrailersAdapter.VideoTrailersClickListener{


    private static final String TAG= DetailsActivity.class.getSimpleName();
    private static final String BUNDLE_IS_FAVOURITE_MOVIE= "is favourite movie";
    private static final String VIDEOS_RECYCLER_VIEW_STATE_KEY= "save video recycler view state";
    private static final String REVIEWS_RECYCLER_VIEW_STATE_KEY= "save reviews recycler view state";
    private static final String IS_DIALOG_DISPLAYED= "is dialog displayed";
    private static final String DIALOG_AUTHOR= "dialog title key";
    private static final String DIALOG_MESSAGE="dialog message";
    private static final String NESTED_SCROLL_VIEW_STATE = " nestedScrollViewState";

    private MovieResponse.MovieModel movie;
    private ActivityDetailsBinding mBinding;
    private MovieService movieService;
    private ReviewAdapter reviewAdapter;
    private VideoTrailersAdapter videoTrailersAdapter;
    private List<Review> reviewList;
    private List<Video> videoList;
    private String posterImageFullPath;

    private Parcelable nestedScrollViewState;
    private boolean isFavouriteMovie;
    private Parcelable videoRecyclerViewState;
    private Parcelable reviewRecyclerViewState;
    private boolean isDialogDisplayed;
    private String dialogAuthor;
    private String dialogMessage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_details);


        movie =getMovieObject();
        isFavouriteMovie = getIntent().getBooleanExtra(MainActivity.INTENT_IS_FAVOURITE_MOVIE,false);

        if(savedInstanceState!=null) {
            restoreActivityMembers(savedInstanceState);
        }

        setFavoriteButtonIcon(isFavouriteMovie);
        setFavouriteButtonListener();
        setShareButtonListener();
        if(movie !=null){
            populateMovieDetails();
        } else {
            displaySnackBarMessage(R.string.toast_error_loading_movie_details);
            onBackPressed();
        }

        buildReviewsRecyclerView();
        buildVideoTrailersRecyclerView();

        if(!CheckingConnection.isNetworkConnected(this)){
            displayVideosEmptyView(R.string.message_offline);
            displayReviewsEmptyView(R.string.message_offline);
        } else {
            displayReviewsLoadingProgressBar(true);
            displayVideosLoadingProgressBar(true);
            movieService= ApiClient.getClient().create(MovieService.class);
            startLoadingVideoTrailers();
            startLoadingReviews();
        }

    }

    private void restoreActivityMembers(Bundle savedInstanceState) {
        isFavouriteMovie = savedInstanceState.getBoolean(BUNDLE_IS_FAVOURITE_MOVIE);
        videoRecyclerViewState= savedInstanceState.getParcelable(VIDEOS_RECYCLER_VIEW_STATE_KEY);
        reviewRecyclerViewState = savedInstanceState.getParcelable(REVIEWS_RECYCLER_VIEW_STATE_KEY);


         /* Restoring ScrollView position code is copied from this link:
        https://eliasbland.wordpress.com/2011/07/28/how-to-save-the-position-of-a-scrollview-when-the-orientation-changes-in-android/*/
        final int[] position = savedInstanceState.getIntArray(NESTED_SCROLL_VIEW_STATE);
        if(position != null)
            mBinding.nestedScrollView.post(new Runnable() {
                public void run() {
                   mBinding.nestedScrollView.scrollTo(position[0], position[1]);
                }
            });
        if(savedInstanceState.containsKey(IS_DIALOG_DISPLAYED)){
            isDialogDisplayed= savedInstanceState. getBoolean(IS_DIALOG_DISPLAYED);
            dialogMessage= savedInstanceState.getString(DIALOG_MESSAGE);
            dialogAuthor= savedInstanceState.getString(DIALOG_AUTHOR);
            displayContentAlertDialog(dialogAuthor,dialogMessage);
        }
    }

    private void setShareButtonListener() {
        mBinding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startShareMovieIntent();
            }
        });
    }

    private void startShareMovieIntent() {
        if(posterImageFullPath!=null && !TextUtils.isEmpty(posterImageFullPath)) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,posterImageFullPath);
            startActivity(intent);
        }

    }


    private void setFavouriteButtonListener() {
        mBinding.favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "favouriteBtnOnClick isFavouriteMovie : "+isFavouriteMovie);
                if(isFavouriteMovie){
                    deleteFavouriteMovieFromDB();
                } else {
                    addFavouriteMovieToDB();
                }

            }
        });
    }

    private void addFavouriteMovieToDB() {
        new AsyncTask<Void, Void,Uri>() {

            @Override
            protected Uri doInBackground(Void... params) {
                ContentValues values= new ContentValues();
                values.put(FavoritesEntry.COLUMN_ID,movie.getId());
                values.put(FavoritesEntry.COLUMN_TITLE,movie.getTitle());
                values.put(FavoritesEntry.COLUMN_RELEASE_DATE,movie.getDate());
                values.put(FavoritesEntry.COLUMN_RATE,movie.getVoteAverage());
                values.put(FavoritesEntry.COLUMN_OVERVIEW,movie.getOverview());
                values.put(FavoritesEntry.COLUMN_POSTER_IMAGE,movie.getPosterImage());
                values.put(FavoritesEntry.COLUMN_BACKDROP_IMAGE,movie.getBackdropImage());
                return getContentResolver().insert(FavoritesEntry.FAVOURITES_CONTENT_URI,values);
            }

            @Override
            protected void onPostExecute(Uri uri) {
                int id= Integer.valueOf(uri.getLastPathSegment());
                Log.i(TAG,"add movie to DB uri: )"+uri);
               if(id>0){
                   isFavouriteMovie=true;
                   displaySnackBarMessage(R.string.snack_bar_add_movie_successfully);
               } else {
                   displaySnackBarMessage(R.string.snack_bar_add_movie_failed);
               }
                setFavoriteButtonIcon(isFavouriteMovie);
            }
        }.execute();
    }

    private void deleteFavouriteMovieFromDB() {
        new AsyncTask<Void, Void,Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                String selection = FavoritesEntry.COLUMN_ID + " = ?";
                String[] selectionArg = new String[]{String.valueOf(movie.getId())};
                return getContentResolver().delete(FavoritesEntry.FAVOURITES_CONTENT_URI, selection,selectionArg);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                Log.i(TAG,"delete movie from DB, rowsDeleted = "+integer);
                if(integer>0){
                    isFavouriteMovie=false;
                    displaySnackBarMessage(R.string.snackbar_delete_movie_successfully);
                } else {
                    displaySnackBarMessage(R.string.snackbar_delete_movie_failed);
                }
                setFavoriteButtonIcon(isFavouriteMovie);
            }
        }.execute();
    }

    private void displaySnackBarMessage(int resId) {
        Snackbar.make(mBinding.parentView,resId,Snackbar.LENGTH_LONG).show();
    }

    private void setFavoriteButtonIcon(boolean isFavouriteMovie) {
        if(isFavouriteMovie){
            mBinding.favouriteBtn.setImageResource(R.drawable.ic_favourite_on);
        } else {
            mBinding.favouriteBtn.setImageResource(R.drawable.ic_favourite_off);
        }
    }

    private MovieResponse.MovieModel getMovieObject(){
        Intent intent=getIntent();
        return intent.getParcelableExtra(MainActivity.INTENT_CLICKED_MOVIE);
    }

    private void populateMovieDetails(){
        posterImageFullPath= MovieImagesFullPath.getPosterImageFullPath(movie.getPosterImage());
        String backdropImageFullPath=MovieImagesFullPath.getBackdropImageFullPath(movie.getBackdropImage());
        populateUI(posterImageFullPath,backdropImageFullPath);
    }

    private void populateUI(String posterPath, String backdropPath){
        mBinding.title.setText(movie.getTitle());
        mBinding.dateValue.setText(movie.getDate());
        mBinding.rateValue.setText(String.valueOf(movie.getVoteAverage()));
        mBinding.overviewValue.setText(movie.getOverview());
        Picasso.with(this).load(posterPath).error(R.drawable.error).placeholder(R.drawable.placeholder)
                .resizeDimen(R.dimen.poster_image_width,R.dimen.poster_image_height).onlyScaleDown()
                .centerCrop().into(mBinding.posterImage);
        Picasso.with(this).load(backdropPath).error(R.drawable.error).placeholder(R.drawable.placeholder)
                .centerCrop().fit().centerCrop().into(mBinding.backdropImage);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setRecyclerViewStates();
        outState.putBoolean(BUNDLE_IS_FAVOURITE_MOVIE,isFavouriteMovie);
        outState.putParcelable(VIDEOS_RECYCLER_VIEW_STATE_KEY,videoRecyclerViewState);
        outState.putParcelable(REVIEWS_RECYCLER_VIEW_STATE_KEY,reviewRecyclerViewState);

        /* saving ScrollView position code is copied from this link:
        https://eliasbland.wordpress.com/2011/07/28/how-to-save-the-position-of-a-scrollview-when-the-orientation-changes-in-android/*/
        outState.putIntArray(NESTED_SCROLL_VIEW_STATE,
                new int[]{ mBinding.nestedScrollView.getScrollX()
                        , mBinding.nestedScrollView.getScrollY()});
        if(isDialogDisplayed){
            outState.putBoolean(IS_DIALOG_DISPLAYED,isDialogDisplayed);
            outState.putString(DIALOG_AUTHOR,dialogAuthor);
            outState.putString(DIALOG_MESSAGE, dialogMessage);
        }
    }

    private void setRecyclerViewStates() {
        videoRecyclerViewState= mBinding.rvTrailerVideos.getLayoutManager().onSaveInstanceState();
        reviewRecyclerViewState= mBinding.rvReviews.getLayoutManager().onSaveInstanceState();
    }

    private void displayVideosEmptyView(int resID){
        mBinding.placeHolderTrailers.progressBar.setVisibility(View.INVISIBLE);
        mBinding.placeHolderTrailers.tvEmptyView.setText(resID);
        mBinding.placeHolderTrailers.tvEmptyView.setVisibility(View.VISIBLE);
        mBinding.rvTrailerVideos.setVisibility(View.INVISIBLE);
    }


    private void displayReviewsEmptyView(int resID) {
        mBinding.placeHolderReviews.progressBar.setVisibility(View.INVISIBLE);
        mBinding.placeHolderReviews.tvEmptyView.setText(resID);
        mBinding.placeHolderReviews.tvEmptyView.setVisibility(View.VISIBLE);
        mBinding.rvReviews.setVisibility(View.INVISIBLE);
    }

    private void displayReviewsLoadingProgressBar(boolean isLoading) {
        mBinding.placeHolderReviews.tvEmptyView.setVisibility(View.INVISIBLE);

        if(isLoading){
            mBinding.rvReviews.setVisibility(View.INVISIBLE);
            mBinding.placeHolderReviews.progressBar.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvReviews.setVisibility(View.VISIBLE);
            mBinding.placeHolderReviews.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void displayVideosLoadingProgressBar(boolean isLoading){
        mBinding.placeHolderTrailers.tvEmptyView.setVisibility(View.INVISIBLE);
        if(isLoading){
            mBinding.rvTrailerVideos.setVisibility(View.INVISIBLE);
            mBinding.placeHolderTrailers.progressBar.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvTrailerVideos.setVisibility(View.VISIBLE);
            mBinding.placeHolderTrailers.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void buildReviewsRecyclerView() {
        GridLayoutManager layoutManager= new GridLayoutManager(this,1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.rvReviews.setLayoutManager(layoutManager);
        mBinding.rvReviews.setHasFixedSize(true);
    }

    private void buildVideoTrailersRecyclerView() {
        GridLayoutManager layoutManager= new GridLayoutManager(this,1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.rvTrailerVideos.setLayoutManager(layoutManager);
        mBinding.rvTrailerVideos.setHasFixedSize(true);
    }

    private void startLoadingReviews() {
        Call<ReviewResponse> call = movieService.getMovieReviews(movie.getId(),API_KEY);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if(response.isSuccessful()){
                    displayReviewsLoadingProgressBar(false);
                    reviewList= response.body().getResults();
                    Log.i(TAG, "reviewList loaded :"+reviewList.size());
                    if(reviewList.size()>0){
                        mBinding.nestedScrollView.scrollTo(0,0);
                      populateReviews();

                    } else {
                        displayReviewsEmptyView(R.string.no_reviews);
                    }
                } else {
                    Log.i(TAG,"Loading Reviews onResponse : NotSuccessful "+response.code());
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                displayReviewsEmptyView(R.string.loading_error);

            }
        });

    }


    private void startLoadingVideoTrailers() {
        Call<VideoTrailersResponse> call = movieService.getMovieVideoTrailers(movie.getId(),API_KEY);
        call.enqueue(new Callback<VideoTrailersResponse>() {
            @Override
            public void onResponse(Call<VideoTrailersResponse> call, Response<VideoTrailersResponse> response) {
                if(response.isSuccessful()){
                    displayVideosLoadingProgressBar(false);
                    videoList= response.body().getResults();
                    Log.i(TAG," videoList loaded : "+ videoList.size());
                    if(videoList.size()>0){
                        mBinding.nestedScrollView.scrollTo(0,0);
                        populateVideoTrailers();

                    } else {
                        displayVideosEmptyView(R.string.no_video_trailers);
                    }
                } else {
                    Log.i(TAG,"Loading VideoTrailers onResponse : NotSuccessful "+response.code());
                }
            }

            @Override
            public void onFailure(Call<VideoTrailersResponse> call, Throwable t) {
                displayVideosEmptyView(R.string.loading_error);
            }
        });
    }

    private void populateReviews() {
        reviewAdapter = new ReviewAdapter(this,reviewList);
        mBinding.rvReviews.setAdapter(reviewAdapter);
        if(reviewRecyclerViewState!=null){
            mBinding.rvReviews.getLayoutManager().onRestoreInstanceState(reviewRecyclerViewState);
        }
    }

    private void populateVideoTrailers() {
        videoTrailersAdapter= new VideoTrailersAdapter(this, videoList);
        mBinding.rvTrailerVideos.setAdapter(videoTrailersAdapter);
        if(videoRecyclerViewState!=null){
            mBinding.rvTrailerVideos.getLayoutManager().onRestoreInstanceState(videoRecyclerViewState);
        }
    }


    @Override
    public void onReviewClickListener(int position) {
        String content = reviewList.get(position).getContent();
        String author = reviewList.get(position).getAuthor();
        displayContentAlertDialog(author, content);
    }
    @Override
    public void onVideoTrailersClickListener(int position) {
        String key = videoList.get(position).getKey();
        StartVideoTrailersChooserIntent(key);

    }



    private void displayContentAlertDialog(String author,String content) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this)
                .setMessage(content)
                .setCancelable(true)
                .setTitle(String.format(getString(R.string.dialog_title),author))
                .setPositiveButton(R.string.dialog_btn_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(dialogInterface!=null) {
                            dialogInterface.dismiss();
                        }

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        isDialogDisplayed=false;
                    }
                });

        dialogAuthor=author;
        dialogMessage= content;
        isDialogDisplayed= true;
        AlertDialog dialog= builder.create();
        dialog.show();
    }

    private void StartVideoTrailersChooserIntent(String key) {
        String path = String.format(getString(R.string.video_url),key);
        Uri videoUri= Uri.parse(path);
        Intent videoIntent = new Intent(Intent.ACTION_VIEW,videoUri);
        startActivity(videoIntent);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
