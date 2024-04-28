package com.example.itubeapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.mindrot.jbcrypt.BCrypt;

public class SignupActivity extends AppCompatActivity {
    private UserDAO userDAO;
    EditText fullnameET, usernameET,  passwordET,  confirmPassET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_layout), (v, insets) -> {
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
        fullnameET = findViewById(R.id.fullName);
        usernameET = findViewById(R.id.signupUsername);
        passwordET = findViewById(R.id.signupPassword);
        confirmPassET = findViewById(R.id.confirmPassword);

        String userName = getIntent().getStringExtra("USERNAME");
        if(userName != null && !userName.isEmpty()) { usernameET.setText(userName); }

        findViewById(R.id.createAccount).setOnClickListener(v -> handleSignup());
    }
    private void handleSignup()
    {
        String fullName = fullnameET.getText().toString().trim();
        String username = usernameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String confirmPassword = confirmPassET.getText().toString().trim();

        // Check if any field is empty
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(SignupActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(SignupActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else {
            // Call the method to handle user registration
            registerUser(fullName, username, password);
        }
    }
    private void registerUser(String fullName, String username, String password) {
        new Thread(() -> {
           String hashedPass = BCrypt.hashpw(password, BCrypt.gensalt());

           UserEntity newUser = new UserEntity();
           newUser.fullName = fullName;
           newUser.username = username;
           newUser.password = hashedPass;

           userDAO.insert(newUser);

           runOnUiThread(() -> Toast.makeText(SignupActivity.this,
                                        "User registered successfully!",
                                               Toast.LENGTH_SHORT).show());
        }).start();


        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
