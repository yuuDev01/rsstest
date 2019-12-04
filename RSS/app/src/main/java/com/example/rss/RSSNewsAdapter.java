package com.example.rss;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RSSNewsAdapter extends RecyclerView.Adapter<RSSNewsAdapter.ViewHolder> implements OnRssNewsItemClickListener {

    private List<RSSNewsItem> items = new ArrayList<RSSNewsItem>();
    OnRssNewsItemClickListener listener;

    @NonNull
    @Override
    public RSSNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item, parent, true);

        return new RSSNewsAdapter.ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RSSNewsAdapter.ViewHolder holder, int position) {
        RSSNewsItem item = items.get(position);
        holder.setItem(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem(RSSNewsItem item) {
        items.add(item);
    }
    public void setItems(ArrayList<RSSNewsItem> items) {
        this.items = items;
    }
    public RSSNewsItem getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, RSSNewsItem item) {
        items.set(position, item);
    }
    public void setOnItemClickListener(OnRssNewsItemClickListener listener) {
        this.listener = listener;
    }
    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mText01;
        private TextView mText02;


        public ViewHolder(View itemView, final OnRssNewsItemClickListener listener) {
            super(itemView);


            mText01 = itemView.findViewById(R.id.news_title);
            mText02 = itemView.findViewById(R.id.news_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(RSSNewsItem item) {
            mText01.setText(item.getTitle());
            mText02.setText(item.getDate());

        }


    }
}
