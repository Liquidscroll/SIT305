package com.example.lostandfoundmapapp;

import android.app.Application;

import com.google.android.libraries.places.api.Places;

public class LostAndFoundMapApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
    }
}
