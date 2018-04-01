package com.example.android.movieapp.moviesdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.movieapp.moviesdatabase.MoviesContract.FavoritesEntry;

/**
 * Created by Little Princess on 3/17/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favourites.db";
    private static final int DATABASE_VERSION = 1;

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTableStatement = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " ( "
                + FavoritesEntry.COLUMN_ID + " INTEGER NOT NULL, "
                + FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT, "
                + FavoritesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
                + FavoritesEntry.COLUMN_RATE + " INTEGER, "
                +FavoritesEntry.COLUMN_BACKDROP_IMAGE + " TEXT NOT NULL, "
                +FavoritesEntry.COLUMN_POSTER_IMAGE+ " TEXT NOT NULL"
                +");";

        sqLiteDatabase.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
