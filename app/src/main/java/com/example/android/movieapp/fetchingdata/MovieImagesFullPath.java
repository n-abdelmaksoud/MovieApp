package com.example.android.movieapp.fetchingdata;

/**
 * Created by Little Princess on 2/26/2018.
 */

public class MovieImagesFullPath {

    private static final String POSTER_IMAGE_PATH ="https://image.tmdb.org/t/p/w185";
    private static final String BACKDROP_IMAGE_PATH ="https://image.tmdb.org/t/p/w780";

    private MovieImagesFullPath(){

    }

    public static String getPosterImageFullPath(String name) {
        String photoURL= POSTER_IMAGE_PATH + name;
        return photoURL;
    }


    public static String getBackdropImageFullPath(String name) {
        String photoURL= BACKDROP_IMAGE_PATH + name;
        return photoURL;
    }


}
