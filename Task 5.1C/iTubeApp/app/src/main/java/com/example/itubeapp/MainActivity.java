package com.example.itubeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.mindrot.jbcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {
    private UserDAO userDAO;
    private EditText usernameET, passwordET;

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

        initDatabase();
        setupUI();
    }
    private void initDatabase()
    {
        userDAO = AppDatabase.getInstance(this).userDAO();
    }

    private void setupUI()
    {
        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);


        findViewById(R.id.login).setOnClickListener(v -> validateLogin());
        findViewById(R.id.signup).setOnClickListener(v -> navigateToSignup());
    }
    private void validateLogin()
    {
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        new Thread(() -> {
           UserEntity user = userDAO.getUserByUsername(username);

           runOnUiThread(() -> {
               if(user != null && BCrypt.checkpw(password, user.password))
               {
                   Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                   intent.putExtra("USER_ENTITY", user);
                   startActivity(intent);
               } else {
                   Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
               }

           });
        }).start();
    }

    private void navigateToSignup()
    {
        String username = usernameET.getText().toString();
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        if(!username.isEmpty())
        {
            intent.putExtra("USERNAME", username);
        }

        startActivity(intent);
    }
}