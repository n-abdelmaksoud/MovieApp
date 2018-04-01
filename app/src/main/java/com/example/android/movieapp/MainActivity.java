package com.example.android.movieapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.movieapp.adapter.MovieAdapter;
import com.example.android.movieapp.connection.CheckingConnection;
import com.example.android.movieapp.databinding.ActivityMainBinding;
import com.example.android.movieapp.fetchingdata.ApiClient;
import com.example.android.movieapp.models.ModifyMovieList;
import com.example.android.movieapp.fetchingdata.MovieService;
import com.example.android.movieapp.models.MovieResponse;
import com.example.android.movieapp.moviesdatabase.MoviesContract;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG=MainActivity.class.getSimpleName();
    private static final String API_KEY="21373ce0af385ce9f981de39a8f5f617";
    private static final String SELECTION = "selection";
    private static final String SELECTION_ARG = "selection args";
    private static final int TOP_RATED_MOVIES=1;
    private static final int POPULAR_MOVIES=2;
    private static final int FAVOURITES=3;
    private static final int FAVOURITE_MOVIES_CURSOR_LOADER = 7;
    private static final String SELECTION_PREFERENCE_KEY="selection preference key";
    private static final String RECYCLER_VIEW_STATE_KEY= "save recycler view state";
    static final String INTENT_CLICKED_MOVIE="intent clicked movie";
    static final String INTENT_IS_FAVOURITE_MOVIE= "intent is favourite movie";
    private static final String PROJECTION = "projection" ;
    private Parcelable recyclerViewState;


    ActivityMainBinding mBinding;
    private MovieService movieService;
    private List<MovieResponse.MovieModel> movieList=null;
    private MovieAdapter adapter;
    private Cursor cursor=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        movieService=ApiClient.getClient().create(MovieService.class);
        mBinding.toolbar.setTitle(R.string.app_name);
        setSupportActionBar(mBinding.toolbar);

        GridLayoutManager layoutManager= new GridLayoutManager(this,2);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setHasFixedSize(true);

        Log.i(TAG,"onCreate startLoadingSelectedMovies");
        startLoadingSelectedMovies();

    }


    private void setRecyclerViewVisibility(boolean isVisible){
        if(isVisible){
            mBinding.placeHolderLayout.progressBar.setVisibility(View.INVISIBLE);
            mBinding.placeHolderLayout.tvEmptyView.setVisibility(View.INVISIBLE);
            mBinding.recyclerView.setVisibility(View.VISIBLE);
        } else {
            mBinding.recyclerView.setVisibility(View.INVISIBLE);
            mBinding.placeHolderLayout.progressBar.setVisibility(View.VISIBLE);
            mBinding.placeHolderLayout.tvEmptyView.setVisibility(View.INVISIBLE);
        }

    }

    private void displayOfflineEmptyView(){
        mBinding.placeHolderLayout.progressBar.setVisibility(View.INVISIBLE);
        mBinding.placeHolderLayout.tvEmptyView.setText(R.string.message_offline);
        mBinding.placeHolderLayout.tvEmptyView.setVisibility(View.VISIBLE);
        mBinding.recyclerView.setVisibility(View.INVISIBLE);
    }

    private void startLoadingSelectedMovies(){
        int selectedMoviesType= getMovieSelection();
        Log.i(TAG,"selected movie type is :"+selectedMoviesType);
        switch (selectedMoviesType){
            case POPULAR_MOVIES:
                startLoadingPopularMovies();
                break;
            case TOP_RATED_MOVIES:
                startLoadingTopRatedMovies();
                break;
            case FAVOURITES:
                startLoadingFavouritesMovies();
            default:
                Log.i(TAG,"Invalid Movie Selection type"+selectedMoviesType);
        }
    }


    private int getMovieSelection(){
        return getPreferences(Context.MODE_PRIVATE).getInt(SELECTION_PREFERENCE_KEY,POPULAR_MOVIES);
    }

    private void saveMovieSelection(int selection){
        SharedPreferences.Editor editor=getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(SELECTION_PREFERENCE_KEY,selection).apply();
    }


    private void startLoadingPopularMovies() {


        if(!CheckingConnection.isNetworkConnected(this)) {
            displayOfflineEmptyView();
            Log.i(TAG,"startLoadingPopularMovies method failed: no network");
            return;
        }

        Log.i(TAG,"startLoadingPopularMovies success");

        setRecyclerViewVisibility(false);

        Call<MovieResponse> call =movieService.getPopularMovies(API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if(response.isSuccessful()) {
                    movieList =response.body().getResults();
                    if(movieList.size()>0) {
                        setRecyclerViewVisibility(true);
                        populateUI(movieList);
                    }

                } else {
                    Log.i(TAG,"Loading Popular Movies onResponse : NotSuccessful "+response.code());
                    showLoadingErrorToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showLoadingErrorToast(t.getMessage());
            }
        });
    }

    private void startLoadingTopRatedMovies(){

        if(!CheckingConnection.isNetworkConnected(this)) {
            displayOfflineEmptyView();
            Log.i(TAG,"startLoadingTopRatedMovies method failed: no network");
            return;
        }
        Log.i(TAG,"startLoadingTopRatedMovies method success");
        setRecyclerViewVisibility(false);

        Call<MovieResponse> call =movieService.getTopRatedMovies(API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if(response.isSuccessful()) {
                    movieList =response.body().getResults();
                    if(movieList.size()>0) {
                        setRecyclerViewVisibility(true);
                        populateUI(movieList);
                    }
                } else {
                    Log.i(TAG,"Loading TopRated Movies onResponse : NotSuccessful "+response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                showLoadingErrorToast(t.getMessage());
            }
        });
    }


    private void startLoadingFavouritesMovies() {
        Log.i(TAG,"startLoadingFavouriteMovies method");
        String[] projection= null;
        String selection= null;
        String[] selectionArgs = null;
        Bundle bundle=new Bundle();
        bundle.putStringArray(PROJECTION, projection);
        bundle.putString(SELECTION,selection);
        bundle.putStringArray(SELECTION_ARG,selectionArgs);
        getLoaderManager().restartLoader(FAVOURITE_MOVIES_CURSOR_LOADER, bundle, this);
    }

    private void populateUI(List<MovieResponse.MovieModel> list){
        if(list.size()>0) {
            Log.i(TAG,"started populateUI . list size = "+list.size());
            modifyMovieList(list);
            Log.i(TAG,"modified movieList populateUI . list size = "+list.size());
            adapter= new MovieAdapter(this,list);
            adapter.notifyDataSetChanged();
            mBinding.recyclerView.setAdapter(adapter);
        }
    }

   /* delete some movies from my list.
    * I can't display the images of these movies because they are against
    * Islamic Laws  */
    private void modifyMovieList(List<MovieResponse.MovieModel> list){
        int[] deletedMoviesID= getResources().getIntArray(R.array.deletedMovies);
        ModifyMovieList.modifyList(list,deletedMoviesID);
    }


    private void showLoadingErrorToast(String errorMSG){
        Log.e(TAG,"error while calling Movie API: "+errorMSG);
        Toast.makeText(MainActivity.this,getText(R.string.loading_error),Toast.LENGTH_LONG).show();
        mBinding.placeHolderLayout.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onMovieClickListener(int position) {
        Log.i(TAG,"item position is:"+position);
        MovieResponse.MovieModel clickedMovie =null;
        boolean isFavouriteMovie=false;
        switch(getMovieSelection()){
            case POPULAR_MOVIES:
            case TOP_RATED_MOVIES:
               clickedMovie=movieList.get(position);
               isFavouriteMovie= isMovieInFavouriteMoviesCursor(clickedMovie.getId());
               break;

            case FAVOURITES:
                clickedMovie= getMovieObjectFromCursor(position);
                isFavouriteMovie=true;
                break;

            default:
        }
        if(clickedMovie!=null) {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(INTENT_CLICKED_MOVIE, clickedMovie);
            intent.putExtra(INTENT_IS_FAVOURITE_MOVIE, isFavouriteMovie);
            startActivity(intent);
        }
    }

   /* check if the movie clicked is a favourite movie or not
   * to set Favourite button state on the DetailsActivity */

    private boolean isMovieInFavouriteMoviesCursor(int id) {
        startLoadingFavouritesMovies();
        if(cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                int columnId = cursor.getColumnIndex(MoviesContract.FavoritesEntry.COLUMN_ID);
                if (cursor.getInt(columnId) == id)
                    return true;
            }
        }
        return false;
    }


    private MovieResponse.MovieModel getMovieObjectFromCursor(int position) {
        cursor.moveToPosition(position);
        int columnTitle= cursor.getColumnIndex(MoviesContract.FavoritesEntry.COLUMN_TITLE);
        int columnId= cursor.getColumnIndex(MoviesContract.FavoritesEntry.COLUMN_ID);
        int columnDate= cursor.getColumnIndex(MoviesContract.FavoritesEntry.COLUMN_RELEASE_DATE);
        int columnRate= cursor.getColumnIndex(MoviesContract.FavoritesEntry.COLUMN_RATE);
        int columnPosterImage= cursor.getColumnIndex(MoviesContract.FavoritesEntry.COLUMN_POSTER_IMAGE);
        int columnBackdropImage= cursor.getColumnIndex(MoviesContract.FavoritesEntry.COLUMN_BACKDROP_IMAGE);
        int columnOverView= cursor.getColumnIndex(MoviesContract.FavoritesEntry.COLUMN_OVERVIEW);

        return new MovieResponse.MovieModel(cursor.getString(columnPosterImage),
                cursor.getString(columnBackdropImage),
                cursor.getString(columnOverView),
                cursor.getString(columnTitle),
                cursor.getString(columnDate),
                cursor.getInt(columnId),
                cursor.getDouble(columnRate));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop save RecyclerView state");
        recyclerViewState= mBinding.recyclerView.getLayoutManager().onSaveInstanceState();
        getIntent().putExtra(RECYCLER_VIEW_STATE_KEY,recyclerViewState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume startLoadingSelectedMovies");
        startLoadingSelectedMovies();
    }


    private void displayEmptyFavouriteView() {
        mBinding.placeHolderLayout.progressBar.setVisibility(View.INVISIBLE);
        mBinding.placeHolderLayout.tvEmptyView.setText(R.string.empty_favourite_list);
        mBinding.placeHolderLayout.tvEmptyView.setVisibility(View.VISIBLE);
        mBinding.recyclerView.setVisibility(View.INVISIBLE);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        switch (id){
            case R.id.menu_popular:
                saveMovieSelection(POPULAR_MOVIES);
                if(CheckingConnection.isNetworkConnected(this)){
                    startLoadingPopularMovies();
                } else{
                    displayOfflineEmptyView();
                }
                return true;

            case R.id.menu_top_rated:
                saveMovieSelection(TOP_RATED_MOVIES);
                if(CheckingConnection.isNetworkConnected(this)){
                    startLoadingTopRatedMovies();
                } else {
                    displayOfflineEmptyView();
                }
               return true;

            case R.id.menu_favourites:
                saveMovieSelection(FAVOURITES);
                startLoadingFavouritesMovies();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection= bundle.getStringArray(PROJECTION);
        String selection= bundle.getString(SELECTION);
        String[] selectionArg= bundle.getStringArray(SELECTION_ARG);

        Uri uri= MoviesContract.FavoritesEntry.FAVOURITES_CONTENT_URI;
        return new CursorLoader(this,uri,projection,selection,selectionArg,null);

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        this.cursor=cursor;

        if(getMovieSelection()==FAVOURITES) {
            if (cursor != null && cursor.getCount() > 0) {
                    setRecyclerViewVisibility(true);
                    adapter = new MovieAdapter(this, cursor);
                    adapter.notifyDataSetChanged();
                    mBinding.recyclerView.setAdapter(adapter);
                    if(recyclerViewState!=null){
                        mBinding.recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                    }
            } else {
                    displayEmptyFavouriteView();

            }
        }


    }


    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}
