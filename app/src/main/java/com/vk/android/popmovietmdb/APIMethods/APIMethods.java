package com.vk.android.popmovietmdb.APIMethods;

import com.vk.android.popmovietmdb.Pojo.PopularMovies;
import com.vk.android.popmovietmdb.R;

import butterknife.BindString;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIMethods {

    @GET("movie/{sort_by}")
    Call<PopularMovies> getMovies(@Path("sort_by") String sortby, @Query("api_key") String apiKey);

}
