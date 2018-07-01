package com.vk.android.popmovietmdb.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
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
import com.vk.android.popmovietmdb.Pojo.MovieResult;
import com.vk.android.popmovietmdb.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    Context context;
    List<MovieResult> movieResults;
    String poster_url;
    int item_layout;

    public MoviesAdapter(int layout_movie_poster_item, Context context, List<MovieResult> results) {
        this.context = context;
        this.movieResults = results;
        this.item_layout = layout_movie_poster_item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(item_layout, parent, false);
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

    private void openDetailsInBottomSheet(MovieResult movieResult) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_details_content, null);
//        view.setBackgroundResource(R.color.white);
        final BottomSheetDialog bottomSheetSkills;
        bottomSheetSkills = new BottomSheetDialog(context);
        bottomSheetSkills.setContentView(view);
        ((View) view.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

        FrameLayout bottomSheet = (FrameLayout) bottomSheetSkills.findViewById(android.support.design.R.id.design_bottom_sheet);
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        ScreenUtils screenUtils = new ScreenUtils(this);
//        Log.d("this is height", "In Add user" + screenUtils.getHeight());
        TextView tv_title = view.findViewById(R.id.tv_movieTitle);
        TextView tv_desc = view.findViewById(R.id.tv_desc);
        TextView tv_relDate = view.findViewById(R.id.tv_relDate);
        ImageView iv_moviePoster = view.findViewById(R.id.iv_moviePoster);
        RatingBar rb_rating = view.findViewById(R.id.ratingBar);

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
