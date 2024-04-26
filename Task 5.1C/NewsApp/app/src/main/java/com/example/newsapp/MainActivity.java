package com.example.newsapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener {
    private NewsAdapter newsAdapter, topAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRecyclerViews();
        fetchNews();
    }
    public void setupRecyclerViews()
    {
        RecyclerView topStories = findViewById(R.id.rv_top_news);
        RecyclerView news = findViewById(R.id.rv_news);

        topStories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        news.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));

        newsAdapter = new NewsAdapter(this, new ArrayList<>(), false, this, 0);
        topAdapter = new NewsAdapter(this, new ArrayList<>(), true, this, 1);
        topStories.setAdapter(topAdapter);
        news.setAdapter(newsAdapter);
    }
    public void fetchNews()
    {
        new Thread(() -> {
            try {
                NewsApiInterface newsInterface = RetrofitClient.getRetroFit().create(NewsApiInterface.class);

                final List<Article> stories = newsInterface.getStories().execute().body().getArticles();
                final List<Article> topStories = newsInterface.getTopStories().execute().body().getArticles();

                runOnUiThread(() -> {
                    if(stories != null) { newsAdapter.updateArticles(stories); }
                    if(topStories != null) { topAdapter.updateArticles(topStories); }
                });
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }).start();
    }
    @Override
    public void onItemClick(Article article)
    {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Gson gson = new Gson();
        String articleJson = gson.toJson(article);
        String relatedArticlesJson = gson.toJson(newsAdapter.getArticles());

        Bundle args = new Bundle();
        args.putSerializable("article", articleJson);
        args.putSerializable("relatedArticles", relatedArticlesJson);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                     android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main, fragment, "detail_fragment")
                .addToBackStack("detail")
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().popBackStackImmediate();
    }
}