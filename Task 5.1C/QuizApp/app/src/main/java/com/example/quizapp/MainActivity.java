package com.example.quizapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText userNameET;
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

        userNameET = findViewById(R.id.nameEntryText);
        handleEntryWithIntent(); // Handle any intent that started this activity
        QuizDataLoader.loadQuestions(this); // Load quiz questions

        findViewById(R.id.startButton).setOnClickListener(v -> {
            Intent startIntent = new Intent(MainActivity.this, QuestionActivity.class);
            startIntent.putExtra("QuestionNumber", 1);
            startIntent.putExtra("UserName", getUserName());
            startIntent.putExtra("TotalQuestions", 5);
            startIntent.putExtra("UserScore", 0);
            startActivity (startIntent);
        });
    }
    // Retrieve user name if entered from intent
    private void handleEntryWithIntent()
    {
        Intent resultIntent = getIntent();
        if(resultIntent != null && resultIntent.hasExtra("UserName"))
        {
            String userName = resultIntent.getStringExtra("UserName");
            if(userName != null)
            {
                userNameET.setText(userName);
            }
        }
    }
    private String getUserName()
    {
        return userNameET.getText().toString();
    }
}

class Option {
    private final String text;
    private final boolean isCorrect;
    public Option(String text, boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
    }
    public String getText()
    {
        return text;
    }
    public boolean isCorrect()
    {
        return isCorrect;
    }

}

class Question {
    private final String title, text;
    private final List<Option> options;

    public Question(String title, String text, List<Option> options) {
        this.title = title;
        this.text = text;
        this.options = options;
    }
    public String getTitle() { return title; }
    public String getText() {
        return text;
    }
    public List<Option> getOptions() {
        return options;
    }
    public void shuffleOptions() { Collections.shuffle(options); }
}
class QuizDataLoader
{
    private static List<Question> qBank;
    public static void loadQuestions(Context context)
    {
        String json;
        try
        {
            InputStream is = context.getAssets().open("questions.json");
            int size = is.available();
            byte[] buf = new byte[size];
            is.read(buf);
            is.close();
            json = new String(buf, StandardCharsets.UTF_8);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            return;
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Question>>() {}.getType();
        qBank = gson.fromJson(json, listType);
        Collections.shuffle(qBank);
        for(Question q : qBank)
        {
            q.shuffleOptions();
        }
    }
    public static Question getQuestion(int pos)
    {
        return qBank.get(pos);
    }
    public static List<Question> getQuestions()
    {
        return qBank;
    }

}
