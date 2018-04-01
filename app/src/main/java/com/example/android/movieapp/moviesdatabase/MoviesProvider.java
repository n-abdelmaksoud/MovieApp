package com.example.android.movieapp.moviesdatabase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.movieapp.moviesdatabase.MoviesContract.FavoritesEntry;

/**
 * Created by Little Princess on 3/17/2018.
 */

public class MoviesProvider extends ContentProvider {

    private static final int MOVIES_CODE = 1;
    private static final int MOVIES_ITEM_CODE = 2;
    private static final String TAG = MoviesProvider.class.getSimpleName();
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY,FavoritesEntry.FAVOURITES_PATH, MOVIES_CODE);
    }

    DBHelper dbHelper;


    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        Cursor cursor;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        switch (match) {
            case MOVIES_CODE:
                cursor = database.query(FavoritesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("uri is not valid: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIES_CODE:
                return FavoritesEntry.PRODUCTS_TYPE;
            case MOVIES_ITEM_CODE:
                return FavoritesEntry.PRODUCT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("uri is invalid; " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.i(TAG, "the content values" + contentValues.toString());
        int match = uriMatcher.match(uri);
        Log.i(TAG,"insert method >> match= "+match);
        switch (match) {
            case MOVIES_CODE:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("uri is not valid: " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        if (values.size() == 0) {
            return null;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Integer id= values.getAsInteger(FavoritesEntry.COLUMN_ID);
        if(id==null){
            throw new IllegalArgumentException("The movie must have an ID");
        }

        String title = values.getAsString(FavoritesEntry.COLUMN_TITLE);
        if (title == null || TextUtils.isEmpty(title))
            throw new IllegalArgumentException("The movie must have a name");

        String overview = values.getAsString(FavoritesEntry.COLUMN_OVERVIEW);
        if (overview == null || TextUtils.isEmpty(overview))
            throw new IllegalArgumentException("The movie must have an overview");

        String backdropImage = values.getAsString(FavoritesEntry.COLUMN_BACKDROP_IMAGE);
        if (backdropImage == null || TextUtils.isEmpty(backdropImage))
            throw new IllegalArgumentException("The movie must have a backdrop image");

        String posterImage = values.getAsString(FavoritesEntry.COLUMN_POSTER_IMAGE);
        if (posterImage == null || TextUtils.isEmpty(posterImage))
            throw new IllegalArgumentException("The movie must have a poster image");

        if (values.containsKey(FavoritesEntry.COLUMN_RATE)) {
            int rate = values.getAsInteger(FavoritesEntry.COLUMN_RATE);
            if (rate < 0 || rate > 10)
                throw new IllegalArgumentException("movie rating cannot not be negative");
        }

        long insertedMovieRow = database.insert(FavoritesEntry.TABLE_NAME, null, values);

        Log.i(TAG, "no. of inserted rows : "+insertedMovieRow);
        if (insertedMovieRow > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, insertedMovieRow);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (match) {
            case MOVIES_CODE:
                rowsDeleted = database.delete(FavoritesEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("uri is invalid; " + uri);
        }
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }



    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
