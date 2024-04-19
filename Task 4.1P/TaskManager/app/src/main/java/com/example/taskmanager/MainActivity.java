package com.example.taskmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> addTaskLauncher, editTaskLauncher;
    private TaskData db;
    private ListView taskList;
    private TaskAdapter taskAdapter;
    private List<Task> tasks = new ArrayList<>();

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

        setupActivityLaunchers(); // Setup the activity launchers for adding and editing tasks
        loadTasks(); // Load tasks from the database
        setupAdapter(); // Setup the adapter for the ListView
        setupBottomNavMenu(); // Setup the bottom navigation menu
    }
    private void setupActivityLaunchers()
    {
        addTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        loadTasks(); // Reload tasks if the add task activity returns with
                                     // a new task
                    }
                });
        editTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        loadTasks(); // Reload tasks if the edit task activity returns with
                                     // an edited task.
                    }
                });
    }
    private void loadTasks()
    {
        taskList = findViewById(R.id.taskList);
        db = Room.databaseBuilder(getApplicationContext(), TaskData.class, "task-database").build();

        new Thread(() -> {
            // Clear the current list of tasks
            tasks.clear();
            // Fetch all tasks from the database and add to the list
            tasks.addAll(db.taskDAO().getAllTasks());
            // Notify the adapter on the UI thread to refresh the list view
            runOnUiThread(() -> taskAdapter.notifyDataSetChanged());
        }).start();
    }
    private void setupAdapter()
    {
        taskAdapter = new TaskAdapter(this, tasks, this.editTaskLauncher);
        taskList.setAdapter(taskAdapter);
    }
    private void setupBottomNavMenu()
    {
        BottomNavigationView menu = findViewById(R.id.bottom_nav);

        Menu navMenu = menu.getMenu();
        MenuItem taskList = navMenu.findItem(R.id.nav_tasks);
        MenuItem editTask = navMenu.findItem(R.id.nav_edit_task);
        taskList.setVisible(false);
        editTask.setVisible(false);

        // Handle menu selection
        menu.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_add_tasks)
            {
                Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                addTaskLauncher.launch(intent);
                return true;
            }
            return false;
        });
    }
}

