package com.example.android.movieapp.moviesdatabase;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Little Princess on 3/17/2018.
 */

public class MoviesContract {

    static final String CONTENT_AUTHORITY="com.example.android.movieapp";
    static final String BASE_CONTENT = "content://com.example.android.movieapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse(BASE_CONTENT);

    private MoviesContract() {

    }

    public static class FavoritesEntry implements BaseColumns {
        public static final String COLUMN_ID= "id";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_RATE= "rate";
        public static final String COLUMN_RELEASE_DATE= "date";
        public static final String COLUMN_OVERVIEW="overview";
        public static final String COLUMN_BACKDROP_IMAGE= "backdrop_image_url";
        public static final String COLUMN_POSTER_IMAGE="poster_image_url";

        static final String TABLE_NAME = "favourites";

        static final String FAVOURITES_PATH = "favourites";
        static final String MOVIE_ITEM_PATH = "favourites/#";

        public static final Uri FAVOURITES_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, FAVOURITES_PATH);



        static final String PRODUCTS_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + FAVOURITES_PATH;
        static final String PRODUCT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_ITEM_PATH;

    }
}
