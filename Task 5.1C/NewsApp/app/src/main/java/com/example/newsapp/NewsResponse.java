package com.example.newsapp;

import java.util.List;

public class NewsResponse {
    private List<Article> articles;
    public NewsResponse() {}
    public List<Article> getArticles() {
        return articles;
    }
}