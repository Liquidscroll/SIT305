package com.example.lostandfoundmapapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
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

        findViewById(R.id.btn_new_ad).setOnClickListener(v -> {
            Intent newAdvertIntent = new Intent(MainActivity.this, NewAdvertActivity.class);
            startActivity(newAdvertIntent);
        });

        findViewById(R.id.btn_show_ads).setOnClickListener(v -> {
            Intent showAdsIntent = new Intent(MainActivity.this, ShowItemsActivity.class);
            startActivity(showAdsIntent);
        });

        findViewById(R.id.btn_show_map).setOnClickListener(v -> {
            Intent showMapIntent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(showMapIntent);
        });
    }
}