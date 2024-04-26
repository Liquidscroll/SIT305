package com.example.newsapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NewsApiInterface
{
    @GET("v2/top-headlines?country=us&apiKey=YOUR_API_KEY")
    Call<NewsResponse> getTopStories();
    @GET("v2/everything?q=technology&language=en&apiKey=YOUR_API_KEY")
    Call<NewsResponse> getStories();
}
