package com.example.newsapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient
{
    private static Retrofit retroFit;
    private static final String BASE_URL = "https://newsapi.org";
    public static Retrofit getRetroFit()
    {
        if(retroFit == null)
        {
            retroFit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retroFit;
    }
}
