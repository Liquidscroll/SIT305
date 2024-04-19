package com.example.taskmanager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDAO {
    @Insert
    void insert(Task task);
    @Update
    void update(Task task);
    @Delete
    void delete(Task task);
    @Query("SELECT * FROM tasks WHERE id = :Id")
    Task getTask(int Id);
    @Query("SELECT * FROM tasks ORDER BY due_date")
    List<Task> getAllTasks();


}
