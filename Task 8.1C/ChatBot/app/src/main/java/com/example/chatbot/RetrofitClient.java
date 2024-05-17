package com.example.chatbot;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static Retrofit retrofit = null;
    public static ChatService getChatService()
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:5000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder().addInterceptor(logging).readTimeout(10, java.util.concurrent.TimeUnit.MINUTES).build()) // this will set the read timeout for 10mins (IMPORTANT: If not your request will exceed the default read timeout)
                    .build();
        }

        return retrofit.create(ChatService.class);
    }
}
