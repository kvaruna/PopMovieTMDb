package com.vk.android.popmovietmdb.LocalData;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesLocalData {
    public static final String CONTENT_AUTH = "com.vk.android.popmovietmdb";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTH);
    public static final String MOVIES_PATH = "movies";

    public static final class MoviesDetails implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendPath(MOVIES_PATH)
                        .build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_PATH_POSTER = "path_poster";
        public static final String COLUMN_RELEASE_DATE = "release_date";

    }
}
