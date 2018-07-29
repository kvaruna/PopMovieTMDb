package com.vk.android.popmovietmdb;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.ybq.android.spinkit.SpinKitView;
import com.vk.android.popmovietmdb.APIClient.APIClient;
import com.vk.android.popmovietmdb.APIMethods.APIMethods;
import com.vk.android.popmovietmdb.Adapters.MoviesAdapter;
import com.vk.android.popmovietmdb.Pojo.MovieResult;
import com.vk.android.popmovietmdb.Pojo.PopularMovies;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
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
    @BindView(R.id.spin_kit)
    SpinKitView loader;
    private APIMethods apiMethods;
    private Context context;
    private int numOfColumns = 2;
    private MoviesAdapter adapter_mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        apiMethods = APIClient.getClient().create(APIMethods.class);
        ConnectivityManager cm =
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
}
