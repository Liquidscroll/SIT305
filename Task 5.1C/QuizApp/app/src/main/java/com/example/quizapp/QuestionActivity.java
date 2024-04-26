package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class QuestionActivity extends AppCompatActivity {
    private boolean isSubmitted = false, correctAnswer = false;
    private Button submitButton, selectedButton = null;
    private Button[] answerButtons;
    private int questionNumber, totalQuestions;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_question_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadData(); // Load question data
        setupSubmitButton(); // Set up UI interaction handlers
        handleFirstQuestionWelcome(); // Special handling for the first question
    }
    private void loadData()
    {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView progressText = findViewById(R.id.progressText);
        TextView questionTitle = findViewById(R.id.questionTitle);
        TextView questionDesc = findViewById(R.id.questionDescription);

        questionNumber = getIntent().getIntExtra("QuestionNumber", -1);
        totalQuestions = getIntent().getIntExtra("TotalQuestions", -1);
        userName = getIntent().getStringExtra("UserName");

        progressBar.setMax(totalQuestions);
        progressBar.setProgress(questionNumber);
        progressText.setText(getString(R.string.percentage_replacement, questionNumber, totalQuestions));

        Question question = QuizDataLoader.getQuestion(questionNumber - 1);
        questionTitle.setText(question.getTitle());
        questionDesc.setText(question.getText());

        loadAnswerButtons(question.getOptions());
    }

    private void loadAnswerButtons(List<Option> options)
    {
        answerButtons = new Button[] {
                findViewById(R.id.answerButton1),
                findViewById(R.id.answerButton2),
                findViewById(R.id.answerButton3)
        };

        for(int i = 0; i < answerButtons.length; i++)
        {
            answerButtons[i].setText(options.get(i).getText());
            answerButtons[i].setTag(options.get(i).isCorrect());
            answerButtons[i].setOnClickListener(this::onAnswerButtonClick);
        }
    }
    // Handle answer button click events
    private void onAnswerButtonClick(View view)
    {
        if(!isSubmitted)
        {
            for(Button btn : answerButtons)
            {
                btn.setBackgroundColor(ContextCompat.getColor(this, R.color.darker_grey));
            }
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.bluish));
            selectedButton = (Button) view;
        }
    }

    private void setupSubmitButton()
    {
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this::onSubmitButtonClick);
        submitButton.setText(R.string.submit_question);
    }
    private void onSubmitButtonClick(View view)
    {
        if(!isSubmitted)
        {
            isSubmitted = true;
            submitButton.setText(R.string.next_question);
            highlightAnswers();
        } else
        {
            nextQuestion();
        }
    }
    void highlightAnswers()
    {
        for(Button btn : answerButtons)
        {
            if(btn.getTag() != null && (boolean) btn.getTag())
            {
                btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                if(btn == selectedButton) { correctAnswer = true; }
            }
        }
        if (selectedButton != null && !(boolean) selectedButton.getTag())
        {
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }
    }

    void nextQuestion()
    {
        int currentUserScore = getIntent().getIntExtra("UserScore", 0) + (correctAnswer ? 1 : 0);

        if (questionNumber < totalQuestions)
        {
            Intent nextIntent = new Intent(this, QuestionActivity.class);
            nextIntent.putExtra("UserName", userName);
            nextIntent.putExtra("QuestionNumber", questionNumber + 1);
            nextIntent.putExtra("TotalQuestions", totalQuestions);
            nextIntent.putExtra("UserScore", currentUserScore);
            startActivity(nextIntent);
        }
        else
        {
            Intent resultIntent = new Intent(this, ScoreActivity.class);
            resultIntent.putExtra("UserName", userName);
            resultIntent.putExtra("UserScore", currentUserScore);
            resultIntent.putExtra("TotalQuestions", totalQuestions);
            startActivity(resultIntent);
        }
        finish();
    }

    private void handleFirstQuestionWelcome()
    {
        if(questionNumber == 1)
        {
            Snackbar welcomeMsg = Snackbar.make(findViewById(R.id.main_question_layout),
                    String.format("Welcome %s!", userName),
                    Snackbar.LENGTH_SHORT);
            View sView = welcomeMsg.getView();
            FrameLayout.LayoutParams par = (FrameLayout.LayoutParams) sView.getLayoutParams();
            par.gravity = Gravity.TOP;
            par.topMargin = 100;
            sView.setLayoutParams(par);
            welcomeMsg.show();
        }
    }
}