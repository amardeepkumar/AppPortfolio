package com.udacity.myappportfolio.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for Movie data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL ON CONFLICT REPLACE, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL DEFAULT 0.0, " +
                MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL DEFAULT 0.0, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_FAVOURITE + " INTEGER DEFAULT 0, " +
                MovieContract.MovieEntry.COLUMN_IS_SELECTED + " INTEGER DEFAULT 0 " +
                " );";


        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieContract.VideoEntry.TABLE_NAME + " (" +
                MovieContract.VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.VideoEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL, " +
                MovieContract.VideoEntry.COLUMN_VIDEO_KEY + " TEXT NOT NULL, " +
                MovieContract.VideoEntry.COLUMN_VIDEO_NAME + " TEXT NOT NULL, " +
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                // Set up the video column as a foreign key to movie table.
                " FOREIGN KEY (" + MovieContract.VideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") " +
                " );";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT, " +
                MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT, " +
                MovieContract.ReviewEntry.COLUMN_URL + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                // Set up the review column as a foreign key to movie table.
                " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") " +
                " );";


        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
