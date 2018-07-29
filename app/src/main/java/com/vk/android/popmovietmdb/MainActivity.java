package com.vk.android.popmovietmdb;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.ybq.android.spinkit.SpinKitView;
import com.vk.android.popmovietmdb.APIClient.APIClient;
import com.vk.android.popmovietmdb.APIMethods.APIMethods;
import com.vk.android.popmovietmdb.Adapters.FavouriteMoviesAdapter;
import com.vk.android.popmovietmdb.Adapters.MoviesAdapter;
import com.vk.android.popmovietmdb.LocalData.MoviesContractLocalData;
import com.vk.android.popmovietmdb.Pojo.MovieResult;
import com.vk.android.popmovietmdb.Pojo.PopularMovies;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    @BindString(R.string.popular)
    String popular;
    @BindString(R.string.top_rating)
    String rated;
    @BindString(R.string.loading)
    String loading;
    String apiKey = BuildConfig.API_KEY;
    @BindView(R.id.recyclerView)
    RecyclerView rv_Movies;
    @BindView(R.id.menu)
    FloatingActionMenu fabFilter;
    @BindView(R.id.fab1)
    FloatingActionButton fab_byRating;
    @BindView(R.id.fab2)
    FloatingActionButton fab_byPopularity;
    @BindView(R.id.fab3)
    FloatingActionButton fab_byFavourite;
    @BindView(R.id.spin_kit)
    SpinKitView loader;
    private APIMethods apiMethods;
    private Context context;
    private int numOfColumns = 2;
    private MoviesAdapter adapter_mv;
    public static final int FAVOURITES_MOVIE_LOADER_ID = 89;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        apiMethods = APIClient.getClient().create(APIMethods.class);
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            getMovies(popular);
        }
        else Toast.makeText(context, "May Be Something is wrong with your internet.", Toast.LENGTH_SHORT).show();

        fab_byRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    getMovies(rated);
                }
                else Toast.makeText(context, "May Be Something is wrong with your internet.", Toast.LENGTH_SHORT).show();

                fabFilter.close(true);
            }
        });
        fab_byPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    getMovies(popular);
                }
                else Toast.makeText(context, "May Be Something is wrong with your internet.", Toast.LENGTH_SHORT).show();

                fabFilter.close(true);
            }
        });
        fab_byFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_Movies.setAdapter(null);
                getSupportLoaderManager().initLoader(FAVOURITES_MOVIE_LOADER_ID,null, (LoaderManager.LoaderCallbacks<Object>) context);
                fabFilter.close(true);
            }
        });
    }


    private void getMovies(String getBy) {
        loader.setVisibility(View.VISIBLE);
        rv_Movies.setAdapter(null);
        Call<PopularMovies> getPopularMovies = apiMethods.getMovies(getBy, apiKey);

        getPopularMovies.enqueue(new Callback<PopularMovies>() {
            @Override
            public void onResponse(Call<PopularMovies> call, Response<PopularMovies> response) {
                setUpRecyclerView(response.body().getResults());
            }

            @Override
            public void onFailure(Call<PopularMovies> call, Throwable t) {
                Log.d("This is error", "" + t.getMessage());
            }
        });
    }

    public void showLoader(){
        loader.setVisibility(View.VISIBLE);
    }

    public void hideLoader(){
        loader.setVisibility(View.GONE);
    }

    private void setUpRecyclerView(List<MovieResult> results) {
        GridLayoutManager layoutManager = new GridLayoutManager(context, numOfColumns);
        rv_Movies.setLayoutManager(layoutManager);
        rv_Movies.setHasFixedSize(true);
        adapter_mv = new MoviesAdapter(R.layout.layout_movie_poster_item, context, results);
        rv_Movies.setAdapter(adapter_mv);
        loader.setVisibility(View.GONE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(context) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MoviesContractLocalData.MoviesDetails.CONTENT_URI,
                            null,
                            null,
                            null,
                            MoviesContractLocalData.MoviesDetails._ID);

                } catch (Exception e) {

                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            FavouriteMoviesAdapter mAdapter;
            mAdapter = new FavouriteMoviesAdapter(this, data);
            rv_Movies.setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
