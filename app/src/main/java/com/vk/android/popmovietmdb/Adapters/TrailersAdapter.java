package com.vk.android.popmovietmdb.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vk.android.popmovietmdb.APIClient.APIClient;
import com.vk.android.popmovietmdb.APIMethods.APIMethods;
import com.vk.android.popmovietmdb.Pojo.Trailers;
import com.vk.android.popmovietmdb.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.http.Query;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    List<Trailers> tl;
    Context context;
    int layout;

    public TrailersAdapter(Context context, int watch_trailer_item, List<Trailers> trailerList) {
        this.context = context;
        this.layout = watch_trailer_item;
        this.tl = trailerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=".concat(tl.get(holder.getAdapterPosition()).getKey())));
                context.startActivity(intent);
            }
        });
        Glide.with(context).load(buildThumUrl(tl.get(holder.getAdapterPosition()).getKey())).into(holder.iv_moviePoster);
    }

    private String buildThumUrl(String key) {
        return "https://img.youtube.com/vi/" + key + "/hqdefault.jpg";
    }

    @Override
    public int getItemCount() {
        return tl.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_trailer_thumb)
        ImageView iv_moviePoster;
        @BindView(R.id.rootView)
        CardView rootView;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
