package com.vk.android.popmovietmdb.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.android.popmovietmdb.Pojo.Review;
import com.vk.android.popmovietmdb.R;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    Context context;
    List<Review> rl;
    int layout;

    public ReviewsAdapter(Context context, int layout_review_item, List<Review> reviewList) {
            this.context =context;
            this.rl =reviewList;
            this.layout = layout_review_item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.authorName.setText(rl.get(holder.getAdapterPosition()).getAuthor());
            holder.content.setText(rl.get(holder.getAdapterPosition()).getContent());
            holder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.content.isExpanded())
                    {
                        holder.content.collapse();
                    }
                    else
                    {
                        holder.content.expand();
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return rl.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_author_name)
        TextView authorName;
        @BindView(R.id.tv_content)
        ExpandableTextView content;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
