package com.example.itubeapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {UserEntity.class}, version=1)
public abstract class AppDatabase extends RoomDatabase
{
    public abstract UserDAO userDAO();
    private static volatile AppDatabase DB_INSTANCE;

    public static AppDatabase getInstance(Context context)
    {
        if(DB_INSTANCE == null)
        {
            synchronized(AppDatabase.class)
            {
                if(DB_INSTANCE == null)
                {
                    DB_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                                       AppDatabase.class, "app-database").build();
                }
            }
        }
        return DB_INSTANCE;
    }
}
