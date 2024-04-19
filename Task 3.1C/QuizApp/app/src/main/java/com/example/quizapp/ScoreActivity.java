package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ScoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve user name and score details from intent
        String userName = getIntent().getStringExtra("UserName");
        int totalQ = getIntent().getIntExtra("TotalQuestions", -1);
        int currUserScore = getIntent().getIntExtra("UserScore", -1);

        TextView cgText = findViewById(R.id.congratulations_text);
        cgText.setText(getString(R.string.congratulations, userName)); // Set congratulations text

        TextView score = findViewById(R.id.final_score_text);
        score.setText(getString(R.string.percentage_replacement, currUserScore, totalQ)); // Set score text

        // Set up click listener for starting a new quiz
        findViewById(R.id.new_quiz_button).setOnClickListener(v -> {
            Intent newQuizIntent = new Intent(ScoreActivity.this, MainActivity.class);
            newQuizIntent.putExtra("UserName", userName); // Pass user name back to main activity
            startActivity(newQuizIntent);
        });
        // Set up click listener to close all activities and exit the app
        findViewById(R.id.finish_button).setOnClickListener(v -> finishAffinity());
    }
}