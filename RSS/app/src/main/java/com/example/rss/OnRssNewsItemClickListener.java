package com.example.rss;

import android.view.View;

public interface OnRssNewsItemClickListener {
    public void onItemClick(RSSNewsAdapter.ViewHolder holder, View view, int position);
}
