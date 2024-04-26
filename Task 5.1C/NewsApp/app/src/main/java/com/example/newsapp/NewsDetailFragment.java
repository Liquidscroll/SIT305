package com.example.newsapp;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class NewsDetailFragment extends Fragment implements NewsAdapter.OnItemClickListener
{
    private ImageView newsImage;
    private TextView newsTitle, newsDesc, newsSource;
    private RecyclerView relatedNewsView;
    private NewsAdapter relatedNewsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);
        initViews(view);
        loadArticleDetails();
        return view;

    }
    private void initViews(View view)
    {
        newsImage = view.findViewById(R.id.news_detail_image);
        newsTitle = view.findViewById(R.id.news_detail_title);
        newsDesc = view.findViewById(R.id.news_detail_description);
        newsSource = view.findViewById(R.id.news_detail_source);
        relatedNewsView = view.findViewById(R.id.related_news_recycler_view);
        relatedNewsView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
    private void loadArticleDetails()
    {
        Bundle args = getArguments();
        if(args != null)
        {
            Gson gson = new Gson();
            Article article = gson.fromJson(args.getString("article"), Article.class);
            Type articleListType = new TypeToken<List<Article>>() {}.getType();
            List<Article> relatedArticles = gson.fromJson(args.getString("relatedArticles"), articleListType);
            if(relatedArticles != null) { Collections.shuffle(relatedArticles); }

            displayArticleDetails(article);
            setupRelatedNewsAdapter(relatedArticles);
        }
    }
    private void displayArticleDetails(Article article)
    {
        newsTitle.setText(Html.fromHtml(String.format("<a href=\"%s\">%s</a>", article.getUrl(), article.getTitle()), Html.FROM_HTML_MODE_LEGACY));
        newsTitle.setMovementMethod(LinkMovementMethod.getInstance());
        newsSource.setText(article.getSource().getName());
        String desc = article.getDesc();
        String content = article.getContent();
        if(desc != null && !desc.isEmpty()) { newsDesc.setText(desc); }
        else if(content != null && !content.isEmpty()) { newsDesc.setText(content); }
        else
        {
            newsDesc.setText(R.string.news_no_desc);
            newsDesc.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        if(article.getImgURL() != null && !article.getImgURL().isEmpty())
        {
            Picasso.get().load(article.getImgURL()).into(newsImage);
        }
    }
    private void setupRelatedNewsAdapter(List<Article> relatedArticles)
    {
        relatedNewsAdapter = new NewsAdapter(getContext(), relatedArticles, true, this, 0);
        relatedNewsView.setAdapter(relatedNewsAdapter);
    }
    @Override
    public void onItemClick(Article article)
    {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        NewsDetailFragment newFragment = NewsDetailFragment.newInstance(article, relatedNewsAdapter.getArticles());
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.main, newFragment, "detail_fragment");
        transaction.addToBackStack("detail");
        transaction.commit();
    }

    public static NewsDetailFragment newInstance(Article article, List<Article> relatedArticles) {
        Gson gson = new Gson();
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putString("article", gson.toJson(article));
        args.putString("relatedArticles", gson.toJson(relatedArticles));
        fragment.setArguments(args);
        return fragment;
    }
}
