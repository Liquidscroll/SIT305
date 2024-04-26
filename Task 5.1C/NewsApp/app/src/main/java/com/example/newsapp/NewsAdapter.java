package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
// This class is used to adapt the news article to various view layouts.
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {


    public interface OnItemClickListener { void onItemClick(Article article); }
    private final List<Article> articles;
    private final LayoutInflater inflater;
    private final int itemWidth;
    private final OnItemClickListener listener;
    private int layoutId;

    public NewsAdapter(Context context, List<Article> articles, boolean entireWidth, OnItemClickListener listener, int layoutId) {
        this.inflater = LayoutInflater.from(context);
        this.articles = articles;
        this.listener = listener;
        this.layoutId = layoutId;

        // Calculate view width based on device width.
        int scrWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.itemWidth = entireWidth ? scrWidth - 20 : (scrWidth / 2) - 20;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        // Inflate layout based on provided layoutId.
        if(layoutId == 1) { itemView = inflater.inflate(R.layout.tops_stories_item, parent, false); }
        else { itemView = inflater.inflate(R.layout.news_item, parent, false); }

        // Set view width based on device width.
        ViewGroup.LayoutParams lPar = itemView.getLayoutParams();
        if (lPar != null) {
            lPar.width = itemWidth;
            itemView.setLayoutParams(lPar);
        }
        return new NewsViewHolder(itemView, this.listener, this, layoutId);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(article));
        holder.title.setText(article.getTitle());
        holder.author.setText(article.getSource().getName());
        if (article.getImgURL() != null && !article.getImgURL().isEmpty()) {
            Picasso.get().load(article.getImgURL()).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public List<Article> getArticles()
    {
        return articles;
    }
    public Article getArticleByPos(int pos)
    {
        return articles.get(pos);
    }
    public void updateArticles(List<Article> newArticles) {
        this.articles.clear();
        this.articles.addAll(newArticles);
        notifyDataSetChanged();
    }
    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView author;
        public ImageView image;

        public NewsViewHolder(View itemView, OnItemClickListener listener, NewsAdapter adapter, int layoutId) {
            super(itemView);
            author = itemView.findViewById(R.id.textview_source);
            title = itemView.findViewById(R.id.textview_headline);
            image = itemView.findViewById(R.id.imageview_news);

            itemView.setOnClickListener(v -> {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION && listener != null) {
                            listener.onItemClick(adapter.getArticleByPos(pos));
                        }
                    });
        }
    }
}