package com.udacity.myappportfolio.utility;

import android.content.ContentValues;
import android.content.Context;

import com.udacity.myappportfolio.data.CustomAsyncQueryHandler;
import com.udacity.myappportfolio.data.MovieContract;

/**
 * Created by Amardeep on 15/4/16.
 */
public class DatabaseUtils {

    public static void setFavourite(Context context, String movieId, int favoriteValue) {
        CustomAsyncQueryHandler queryHandler = new CustomAsyncQueryHandler(context.getContentResolver());
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, favoriteValue);

        queryHandler.startUpdate(1, null, MovieContract.MovieEntry.CONTENT_URI,
                values, MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{movieId});
    }
}
