package com.example.taskmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends ArrayAdapter<Task> {
    // Constant for truncating long descriptions
    private static final int MAX_DESC_LENGTH = 50;
    private List<Task> tasks;
    private Context context;
    private ActivityResultLauncher<Intent> editTaskLauncher;
    public TaskAdapter(Context context, List<Task> tasks, ActivityResultLauncher<Intent> editTaskLauncher) {
        super(context, 0, tasks);
        this.context = context;
        this.tasks = tasks;
        this.editTaskLauncher = editTaskLauncher;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Task task = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        TextView taskName = convertView.findViewById(R.id.list_task_name);
        TextView taskDesc = convertView.findViewById(R.id.list_task_desc);
        TextView taskDue = convertView.findViewById(R.id.list_task_due);
        Button deleteButton = convertView.findViewById(R.id.list_delete_task);
        Button editButton = convertView.findViewById(R.id.list_edit_task);

        taskName.setText(task.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        taskDue.setText(String.format("Due: %s", sdf.format(task.getDueDate())));

        String desc = task.getDescription();

        if(desc.length() > MAX_DESC_LENGTH)
        {
            desc = desc.substring(0, MAX_DESC_LENGTH) + "...";
        }
        taskDesc.setText(desc);

        deleteButton.setOnClickListener(v -> deleteTask(position));
        editButton.setOnClickListener(v -> editTask(task.getId()));
        convertView.setOnClickListener(v -> taskDetail(task.getId()));

        return convertView;
    }
    // Start the TaskDetailActivity when a task item is clicked
    private void taskDetail(int Id)
    {
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra("Id", Id);
        getContext().startActivity(intent);
    }
    // Launch the AddEditTaskActivity to edit a task
    private void editTask(int Id)
    {
        Intent intent = new Intent(this.context, AddEditTaskActivity.class);
        intent.putExtra("Id", Id);
        editTaskLauncher.launch(intent);
    }
    // Delete a task from the database and update the list
    private void deleteTask(int position)
    {
        Task task = tasks.get(position);
        new Thread(() -> {
            TaskData db = TaskData.getDatabase(context);
            db.taskDAO().delete(task);
            ((Activity) context).runOnUiThread(() -> {
                tasks.remove(position);
                notifyDataSetChanged();
            });
        }).start();
    }
}
