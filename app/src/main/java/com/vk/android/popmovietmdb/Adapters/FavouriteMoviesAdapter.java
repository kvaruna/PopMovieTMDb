package com.vk.android.popmovietmdb.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vk.android.popmovietmdb.LocalData.MoviesContractLocalData;
import com.vk.android.popmovietmdb.MainActivity;
import com.vk.android.popmovietmdb.Pojo.MovieResult;
import com.vk.android.popmovietmdb.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteMoviesAdapter extends RecyclerView.Adapter<FavouriteMoviesAdapter.ViewHolder> {

    int layout = R.layout.layout_movie_poster_item;
    Context context;
    String poster_url;
    private List<MovieResult> favouriteMovies;

    public FavouriteMoviesAdapter(MainActivity mainActivity, Cursor data) {
        this.context = mainActivity;

        favouriteMovies = new ArrayList<>();
        loadMoviesFromCursor(data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        poster_url = context.getResources().getString(R.string.base_url_for_poster);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Glide.with(context).load(poster_url + favouriteMovies.get(holder.getAdapterPosition()).getPosterPath()).into(holder.iv_moviePoster);
        holder.tv_voteAvg.setText(String.valueOf(favouriteMovies.get(holder.getAdapterPosition()).getVoteAverage()));
        holder.tv_voteCount.setText("Total Votes : " + favouriteMovies.get(holder.getAdapterPosition()).getVoteCount().toString());
    }

    @Override
    public int getItemCount() {
        return favouriteMovies.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private void loadMoviesFromCursor(Cursor cursor) {
        for (int i = 0; i < cursor.getCount(); i++) {
            int movieIdIndex = cursor.getColumnIndex(MoviesContractLocalData.MoviesDetails.COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(MoviesContractLocalData.MoviesDetails.COLUMN_TITLE);
            int relDateIndex = cursor.getColumnIndex(MoviesContractLocalData.MoviesDetails.COLUMN_RELEASE_DATE);
            int posterPathIndex = cursor.getColumnIndex(MoviesContractLocalData.MoviesDetails.COLUMN_PATH_POSTER);
            int overviewIndex = cursor.getColumnIndex(MoviesContractLocalData.MoviesDetails.COLUMN_OVERVIEW);
            int ratingIndex = cursor.getColumnIndex(MoviesContractLocalData.MoviesDetails.COLUMN_RATING);
            int totalVotesIndex = cursor.getColumnIndex(MoviesContractLocalData.MoviesDetails.COLUMN_TOTAL_VOTES);


            cursor.moveToPosition(i);

            favouriteMovies.add(new MovieResult(
                    cursor.getInt(movieIdIndex),
                    cursor.getString(titleIndex),
                    cursor.getString(overviewIndex),
                    cursor.getString(posterPathIndex),
                    cursor.getDouble(ratingIndex),
                    cursor.getInt(totalVotesIndex),
                    cursor.getString(relDateIndex)));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_moviePoster)
        ImageView iv_moviePoster;
        @BindView(R.id.tv_voteAvg)
        TextView tv_voteAvg;
        @BindView(R.id.tv_voteCount)
        TextView tv_voteCount;
        @BindView(R.id.card)
        CardView card;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
