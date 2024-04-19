package com.example.taskmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {
    private TextView taskTitle;
    private TextView taskDesc;
    private TextView taskDue;
    private ActivityResultLauncher<Intent> addTaskLauncher;
    private ActivityResultLauncher<Intent> editTaskLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.task_detail_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        setupActivityLaunchers();

        taskTitle = findViewById(R.id.task_detail_title);
        taskDesc = findViewById(R.id.task_detail_desc);
        taskDue = findViewById(R.id.task_detail_due);

        int taskId = getIntent().getIntExtra("Id", -1);
        if(taskId != -1) {
            loadTaskDetails(taskId);
        }

        setupBottomNavMenu(taskId);
    }
    private void setupActivityLaunchers()
    {
        addTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
        editTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                });
    }
    private void loadTaskDetails(int taskId)
    {
        new Thread(() -> {
            Task task = TaskData.getDatabase(getApplicationContext()).taskDAO().getTask(taskId);
            if(task != null) {
                runOnUiThread(() -> {
                    taskTitle.setText(task.getTitle());
                    taskDesc.setText(task.getDescription());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    taskDue.setText(String.format("Due: %s", sdf.format(task.getDueDate())));
                });
            }
        }).start();
    }
    private void setupBottomNavMenu(int taskId)
    {
        BottomNavigationView menu = findViewById(R.id.bottom_nav);

        menu.setOnItemSelectedListener(item -> {
            int menuId = item.getItemId();
            if (menuId == R.id.nav_tasks) {
                finish(); // Exit detail view and go back to the task list
            } else if(menuId == R.id.nav_add_tasks)
            {
                Intent intent = new Intent(TaskDetailActivity.this, AddEditTaskActivity.class);
                addTaskLauncher.launch(intent); // Launch activity to add a new task
                return true;
            } else if(menuId == R.id.nav_edit_task && taskId != -1)
            {
                Intent intent = new Intent(TaskDetailActivity.this, AddEditTaskActivity.class);
                intent.putExtra("Id", taskId);
                editTaskLauncher.launch(intent); // Launch activity to edit the current task
                return true;
            }
            return false;
        });
    }
}
