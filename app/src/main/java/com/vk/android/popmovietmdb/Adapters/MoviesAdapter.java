package com.vk.android.popmovietmdb.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vk.android.popmovietmdb.APIClient.APIClient;
import com.vk.android.popmovietmdb.APIMethods.APIMethods;
import com.vk.android.popmovietmdb.BuildConfig;
import com.vk.android.popmovietmdb.LocalData.MoviesContractLocalData;
import com.vk.android.popmovietmdb.MainActivity;
import com.vk.android.popmovietmdb.Pojo.MovieResult;
import com.vk.android.popmovietmdb.Pojo.MovieTrailers;
import com.vk.android.popmovietmdb.Pojo.Review;
import com.vk.android.popmovietmdb.Pojo.Reviews;
import com.vk.android.popmovietmdb.Pojo.Trailers;
import com.vk.android.popmovietmdb.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    Context context;
    List<MovieResult> movieResults;
    String poster_url;
    int item_layout;
    List<Trailers> trailerList = new ArrayList<>();
    List<Review> reviewList = new ArrayList<>();
    TrailersAdapter adapter;
    ReviewsAdapter reviewsAdapter;
    String apiKey = BuildConfig.API_KEY;
    private boolean isFavourite;
    private APIMethods apiMethods;
    public MoviesAdapter(int layout_movie_poster_item, Context context, List<MovieResult> results) {
        this.context = context;
        this.movieResults = results;
        this.item_layout = layout_movie_poster_item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(item_layout, parent, false);
        apiMethods = APIClient.getClient().create(APIMethods.class);
        poster_url = context.getResources().getString(R.string.base_url_for_poster);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Glide.with(context).load(poster_url + movieResults.get(holder.getAdapterPosition()).getPosterPath()).into(holder.iv_moviePoster);
        holder.tv_voteAvg.setText(movieResults.get(holder.getAdapterPosition()).getVoteAverage().toString());
        holder.tv_voteCount.setText("Total Votes : " + movieResults.get(holder.getAdapterPosition()).getVoteCount().toString());
        holder.iv_moviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(holder.card, "scaleX", 1f, 0f);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(holder.card, "scaleX", 0f, 1f);
                oa1.setInterpolator(new DecelerateInterpolator());
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        openDetailsInBottomSheet(movieResults.get(holder.getAdapterPosition()));
                        oa2.start();
                    }
                });
                oa1.start();
            }
        });
    }

    private void openDetailsInBottomSheet(final MovieResult movieResult) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_details_content, null);
//        view.setBackgroundResource(R.color.white);
        final BottomSheetDialog bottomSheetSkills;
        bottomSheetSkills = new BottomSheetDialog(context);
        bottomSheetSkills.setContentView(view);
        ((View) view.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

        final FrameLayout bottomSheet = (FrameLayout) bottomSheetSkills.findViewById(android.support.design.R.id.design_bottom_sheet);
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        TextView tv_title = view.findViewById(R.id.tv_movieTitle);
        TextView tv_desc = view.findViewById(R.id.tv_desc);
        TextView tv_relDate = view.findViewById(R.id.tv_relDate);
        ImageView iv_moviePoster = view.findViewById(R.id.iv_moviePoster);
        RatingBar rb_rating = view.findViewById(R.id.ratingBar);
        final ImageView iv_fav = view.findViewById(R.id.fav);
        final TextView tv_fav_text = view.findViewById(R.id.fav_text);
        ImageView iv_trailer = view.findViewById(R.id.trailer);
        ImageView iv_share = view.findViewById(R.id.share);
        ImageView iv_readReviews = view.findViewById(R.id.reviews);
        isFavourite = isAddedToFav(movieResult.getId());
        if (isFavourite) {
            iv_fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fav));
            tv_fav_text.setText("Liked");
        } else {
            iv_fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.nfav));
            tv_fav_text.setText("Favourite ?");
        }


        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavStatus(iv_fav,movieResult,tv_fav_text);
            }
        });
        iv_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTrailer(movieResult.getId());
            }
        });
        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(movieResult.getId());
            }
        });
        iv_readReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readReviews(movieResult.getId(),bottomSheetSkills);
            }
        });


        tv_title.setText(movieResult.getTitle());
        tv_desc.setText(movieResult.getOverview());
        tv_relDate.setText(movieResult.getReleaseDate());
        rb_rating.setRating(Float.valueOf(String.valueOf(movieResult.getVoteAverage())));
        Glide.with(context).load(poster_url + movieResult.getPosterPath()).into(iv_moviePoster);


        behavior.setPeekHeight(1200);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        params.setMargins(30, 0, 30, 0);
        bottomSheetSkills.setCancelable(true);
        bottomSheetSkills.setCanceledOnTouchOutside(true);

        bottomSheetSkills.show();



    }

    private boolean isAddedToFav(Integer id) {
        final Cursor cursor;
        cursor = context.getContentResolver().query(MoviesContractLocalData.MoviesDetails.CONTENT_URI, null, "movie_id=?", new String[]{String.valueOf(id)}, null);

        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;

    }

    private void readReviews(Integer id, final BottomSheetDialog bottomSheet) {
        Call<Reviews> getReviews = apiMethods.getMovieReviews(id,apiKey);
        getReviews.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                reviewList = response.body().getResults();
                //setUpReviewsList();
                bottomSheet.dismiss();
                setUpReviewsInBottomSheet();
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {

            }
        });
    }

    private void setUpReviewsInBottomSheet() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_rv_reviews, null);
//        view.setBackgroundResource(R.color.white);
        final BottomSheetDialog bottomSheetreviews;
        bottomSheetreviews = new BottomSheetDialog(context);
        bottomSheetreviews.setContentView(view);
        ((View) view.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

        final FrameLayout bottomSheet = (FrameLayout) bottomSheetreviews.findViewById(android.support.design.R.id.design_bottom_sheet);
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        TextView tv_heading = view.findViewById(R.id.tv_heading);
        RecyclerView rv_reviews = view.findViewById(R.id.rv_trailers);
        tv_heading.setText("Reviews");
        LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rv_reviews.setLayoutManager(llm);
        rv_reviews.setHasFixedSize(true);
        reviewsAdapter = new ReviewsAdapter(context, R.layout.layout_review_item, reviewList);
        rv_reviews.setAdapter(reviewsAdapter);

        behavior.setPeekHeight(1200);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        params.setMargins(30, 0, 30, 0);
        bottomSheetreviews.setCancelable(true);
        bottomSheetreviews.setCanceledOnTouchOutside(true);

        bottomSheetreviews.show();
    }

    private void share(Integer id) {
        Call<MovieTrailers> getPopularMovies = apiMethods.getMovieTrailers(id, apiKey);
        getPopularMovies.enqueue(new Callback<MovieTrailers>() {
            @Override
            public void onResponse(Call<MovieTrailers> call, Response<MovieTrailers> response) {
                trailerList = response.body().getResults();
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, trailerList.get(0).getName());
                intent.putExtra(android.content.Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" +trailerList.get(0).getKey());
                context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share_trailer)));
            }

            @Override
            public void onFailure(Call<MovieTrailers> call, Throwable t) {

            }
        });

    }

    private void playTrailer(Integer id) {
        ((MainActivity) context).showLoader();
        Call<MovieTrailers> getPopularMovies = apiMethods.getMovieTrailers(id, apiKey);
        getPopularMovies.enqueue(new Callback<MovieTrailers>() {
            @Override
            public void onResponse(Call<MovieTrailers> call, Response<MovieTrailers> response) {
                    trailerList = response.body().getResults();
                    setUpTrailerView();
            }

            @Override
            public void onFailure(Call<MovieTrailers> call, Throwable t) {

            }
        });
    }

    private void setUpTrailerView() {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rv_trailers = inflater.inflate(R.layout.layout_rv_trailers, null);
        TextView tv_heading = rv_trailers.findViewById(R.id.tv_heading);
        TextView tv_dismiss = rv_trailers.findViewById(R.id.tv_cancel);
        RecyclerView rv_trailer = rv_trailers.findViewById(R.id.rv_trailers);
        tv_heading.setText("Watch Trailers");

        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        rv_trailer.setLayoutManager(llm);
        rv_trailer.setHasFixedSize(true);
        adapter = new TrailersAdapter(context,R.layout.watch_trailer_item,trailerList);
        rv_trailer.setAdapter(adapter);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(rv_trailers);
        alert.setCancelable(true);

        final AlertDialog dialog = alert.create();
        tv_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        ((MainActivity) context).hideLoader();
    }

    private void setFavStatus(ImageView iv_fav, MovieResult movieResult, TextView tv_fav_text) {
        if (isFavourite) {
            Uri uri = MoviesContractLocalData.MoviesDetails.CONTENT_URI;
            uri = uri.buildUpon().appendPath(String.valueOf(movieResult.getId())).build();
            int returnUri = context.getContentResolver().delete(uri, null, null);
            context.getContentResolver().notifyChange(uri, null);

            isFavourite = !isFavourite;
            if (isFavourite) {
                iv_fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fav));

            } else {
                iv_fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.nfav));

            }
            Toast.makeText(context.getApplicationContext(), movieResult.getTitle() + " Removed From Favourites.", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesContractLocalData.MoviesDetails.COLUMN_ID, movieResult.getId());
            contentValues.put(MoviesContractLocalData.MoviesDetails.COLUMN_TITLE, movieResult.getTitle());
            contentValues.put(MoviesContractLocalData.MoviesDetails.COLUMN_OVERVIEW, movieResult.getOverview());
            contentValues.put(MoviesContractLocalData.MoviesDetails.COLUMN_PATH_POSTER, movieResult.getPosterPath());
            contentValues.put(MoviesContractLocalData.MoviesDetails.COLUMN_RELEASE_DATE, movieResult.getReleaseDate());
            contentValues.put(MoviesContractLocalData.MoviesDetails.COLUMN_RATING,movieResult.getVoteAverage());
            contentValues.put(MoviesContractLocalData.MoviesDetails.COLUMN_TOTAL_VOTES,movieResult.getVoteCount());


            Uri uri = context.getContentResolver().insert(MoviesContractLocalData.MoviesDetails.CONTENT_URI, contentValues);
            if (uri != null) {
                isFavourite = !isFavourite;
                if (isFavourite) {
                    iv_fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fav));
                    tv_fav_text.setText("Liked");

                } else {
                    iv_fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.nfav));
                    tv_fav_text.setText("Favourite ?");

                }
                Toast.makeText(context.getApplicationContext(), movieResult.getTitle() + " Added to Favourites." , Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }



    @Override
    public int getItemCount() {
        return movieResults.size();
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
