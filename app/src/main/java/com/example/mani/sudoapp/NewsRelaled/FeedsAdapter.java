package com.example.mani.sudoapp.NewsRelaled;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mani.sudoapp.R;

import java.util.List;

/**
 * Created by mani on 2/13/18.
 */

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.FeedsViewHolder>  {


    private Context mCtx;
    private List<Feeds> mFeedsList;

    public FeedsAdapter(Context mCtx, List<Feeds> feeds) {
        this.mCtx = mCtx;
        this.mFeedsList = feeds;
    }


    @Override
    public FeedsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.news_feed_recycler_view_layout,parent,false);
        return new FeedsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FeedsViewHolder holder, int position) {

        final Feeds feeds = mFeedsList.get(position);

        holder.recycler_title.setText(feeds.getTitle());
        holder.recycler_desc.setText(feeds.getDesc());

        //holder.recycler_imageView.setImageDrawable(mCtx.getResources().getDrawable(feeds.getImage()));
        Glide.with(mCtx).load(feeds.getImagePath()).into(holder.recycler_imageView);

        holder.cardView.setCardBackgroundColor(feeds.isSelected()? Color.CYAN :Color.WHITE);

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                feeds.setSelected(!feeds.isSelected());
                holder.cardView.setCardBackgroundColor(feeds.isSelected()? Color.CYAN : Color.WHITE);
                return true;
            }
        });



    }

    @Override
    public int getItemCount() {
        return mFeedsList.size();
    }

    class FeedsViewHolder extends RecyclerView.ViewHolder{

        ImageView recycler_imageView;
        TextView recycler_title,recycler_desc;
        CardView cardView;

        public FeedsViewHolder(View itemView) {
            super(itemView);

            recycler_imageView = itemView.findViewById(R.id.news_feed_recycyler_view_imageview);
            recycler_title     = itemView.findViewById(R.id.news_feed_recycyler_view_title);
            recycler_desc      = itemView.findViewById(R.id.news_feed_recycyler_view_desc);
            cardView           = itemView.findViewById(R.id.news_feed_cardView);
        }
    }

    public void clear() {
        mFeedsList.clear();
        notifyDataSetChanged();

    }

    public void addAll(List<Feeds> list) {
        mFeedsList.addAll(list);
        notifyDataSetChanged();

    }
}
