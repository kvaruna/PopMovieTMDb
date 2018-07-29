package com.vk.android.popmovietmdb.LocalData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "movie.db";
    private static final int DB_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + MoviesContractLocalData.MoviesDetails.TABLE_NAME + " (" +
                MoviesContractLocalData.MoviesDetails._ID + " INTEGER PRIMARY KEY, " +
                MoviesContractLocalData.MoviesDetails.COLUMN_ID + " TEXT NOT NULL, " +
                MoviesContractLocalData.MoviesDetails.COLUMN_TITLE + " TEXT NOT NULL," +
                MoviesContractLocalData.MoviesDetails.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MoviesContractLocalData.MoviesDetails.COLUMN_PATH_POSTER + " TEXT NOT NULL," +
                MoviesContractLocalData.MoviesDetails.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MoviesContractLocalData.MoviesDetails.COLUMN_RATING + " INTEGER NOT NULL)" +"; ";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContractLocalData.MoviesDetails.TABLE_NAME);
        onCreate(db);
    }
}
