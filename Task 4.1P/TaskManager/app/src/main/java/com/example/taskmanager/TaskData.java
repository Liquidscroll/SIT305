package com.example.taskmanager;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

@Database(entities = {Task.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class TaskData extends RoomDatabase {
    private static volatile TaskData database;
    public abstract TaskDAO taskDAO();
    // Method to get the singleton instance of the database
    public static TaskData getDatabase(final Context context)
    {
        if(database == null)
        {
            synchronized (TaskData.class) {
                if(database == null)
                {
                    database = Room.databaseBuilder(context.getApplicationContext(),
                                                    TaskData.class, "task-database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return database;
    }
}

class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp)
    {
        return timestamp == null ? null : new Date(timestamp);
    }
    @TypeConverter
    public static Long toTimeStamp(Date date)
    {
        return date == null ? null : date.getTime();
    }
}
