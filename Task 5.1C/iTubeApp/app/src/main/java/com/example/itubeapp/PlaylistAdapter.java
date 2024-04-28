package com.example.itubeapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    public interface OnItemClickListener { void onItemClick(String url, int position); }
    private final List<String> urls;
    private final OnItemClickListener listener;

    public PlaylistAdapter(List<String> urls, OnItemClickListener listener)
    {
        this.urls = urls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url = urls.get(position);
        holder.urlTextView.setText(url);
    }

    @Override
    public int getItemCount() {
        return (urls == null) ? 0 : urls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView urlTextView;

        public ViewHolder(View view, OnItemClickListener listener) {
            super(view);
            urlTextView = view.findViewById(R.id.urlTextView);
            view.setOnClickListener(v -> {
                if(listener != null)
                {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        listener.onItemClick(urlTextView.getText().toString(), pos);
                    }
                }
            });
        }
    }
}
