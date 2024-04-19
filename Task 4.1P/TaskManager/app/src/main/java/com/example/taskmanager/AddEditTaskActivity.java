package com.example.taskmanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class AddEditTaskActivity extends AppCompatActivity {
    private EditText taskTitle, taskDesc;
    private TextView taskDue;
    boolean editing = false;
    private Task currentTask;
    private TaskData db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_edit_task_main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Dismiss the keyboard when touching outside of input fields
        findViewById(R.id.add_edit_task_main_layout).setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                v.performClick();
                hideKeyboard();
            }
            return true;
        });

        taskTitle = findViewById(R.id.add_task_title);
        taskDesc = findViewById(R.id.add_task_desc);
        taskDue = findViewById(R.id.add_task_due);

        // Check if this activity was started for editing a task
        Bundle data = getIntent().getExtras();
        if(data != null && data.containsKey("Id"))
        {
            editing = true;
            int taskId = data.getInt("Id");
            loadTask(taskId);
        }
        setupBottomNavMenu(); // Setup the bottom navigation menu
        findViewById(R.id.save_task_button).setOnClickListener(view -> saveTask(getCurrentFocus()));
        taskDue.setOnClickListener(this::showDatePicker); // Setup date picker dialog for due date
    }

    void setupBottomNavMenu()
{
    BottomNavigationView menu = findViewById(R.id.bottom_nav);
    Menu navMenu = menu.getMenu();
    MenuItem addTask = navMenu.findItem(R.id.nav_add_tasks);
    MenuItem editTask = navMenu.findItem(R.id.nav_edit_task);

    addTask.setVisible(false);
    editTask.setVisible(false);

    menu.setOnItemSelectedListener(item -> {
        if (item.getItemId() == R.id.nav_tasks) {
            Intent intent = new Intent(this, MainActivity.class);
            // Clears all other activities on top of MainActivity and brings it to the top
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }
        return false;
    });

}

    private void loadTask(int Id)
    {
        new Thread(() -> {
            db = TaskData.getDatabase(getApplicationContext());
            currentTask = db.taskDAO().getTask(Id);
            runOnUiThread(() -> {
                if(currentTask != null)
                {
                    taskTitle.setText(currentTask.getTitle());
                    taskDesc.setText(currentTask.getDescription());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    taskDue.setText(sdf.format(currentTask.getDueDate()));
                }
            });
        }).start();
    }

    private void saveTask(View view) {
        String title = taskTitle.getText().toString();
        String desc = taskDesc.getText().toString();
        String due = taskDue.getText().toString();

        if(title.isEmpty() || desc.isEmpty() || due.isEmpty())
        {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date dueDate;
        try {
            sdf.setLenient(false);
            dueDate = sdf.parse(due);
        }
        catch(ParseException e)
        {
            Toast.makeText(this, "Please enter valid date in correct format.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentTask == null)
        {
            currentTask = new Task(title, desc, dueDate);
        }
        else
        {
            currentTask.setTitle(title);
            currentTask.setDescription(desc);
            currentTask.setDueDate(dueDate);
        }

        new Thread(() -> {
            if(db == null)
            {
                db = TaskData.getDatabase(getApplicationContext());
            }

            if(currentTask.getId() == 0)
            {
                db.taskDAO().insert(currentTask);
            }
            else
            {
                db.taskDAO().update(currentTask);
            }
            setResult(Activity.RESULT_OK);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            finish();
        }).start();
    }

    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if(currentFocus != null && imm != null)
        {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showDatePicker(View view)
    {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        // Display the date picker dialog
        DatePickerDialog dpg = new DatePickerDialog(this,
                (date_view, year, month, day) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, day);
                    Calendar yesterday = Calendar.getInstance();
                    yesterday.add(Calendar.DAY_OF_MONTH, -1);
                    if (!selectedDate.before(yesterday)) {
                        // Formatting the date as "dd/MM/yyyy"
                        taskDue.setText(String.format(Locale.getDefault(), "%d/%d/%d", day, month + 1, year));
                    } else {
                        // Show toast if the selected date is before today
                        Toast.makeText(getApplicationContext(), "Selected date is before today. Please select a valid date.", Toast.LENGTH_LONG).show();
                    }
                }, y, m, d);

        dpg.getDatePicker().setMinDate(c.getTimeInMillis()); // Set the minimum date to today
        dpg.show();
    }

}