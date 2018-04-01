package com.example.android.movieapp.fetchingdata;

import com.example.android.movieapp.models.MovieResponse;
import com.example.android.movieapp.models.ReviewResponse;
import com.example.android.movieapp.models.VideoTrailersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Little Princess on 2/22/2018.
 */

public interface MovieService {

    @GET ("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String key);

    @GET ("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String key);

    @GET ("movie/{movie_id}/videos")
    Call<VideoTrailersResponse> getMovieVideoTrailers( @Path("movie_id") int id, @Query("api_key") String key);

    @GET ("movie/{movie_id}/reviews")
    Call<ReviewResponse> getMovieReviews( @Path("movie_id") int id, @Query("api_key") String key);

}
