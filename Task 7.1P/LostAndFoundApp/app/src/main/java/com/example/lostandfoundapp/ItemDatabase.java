package com.example.lostandfoundapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ItemDatabase  extends SQLiteOpenHelper {
    private static final String DB_NAME = "LostAndFound.db";
    private static final int DB_VERSION = 1;

    public ItemDatabase(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL (
                "CREATE TABLE adverts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "post_type TEXT, " +
                "name TEXT, " +
                "phone_number TEXT, " +
                "description TEXT, " +
                "date DATE, " +
                "location TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer)
    {
        db.execSQL("DROP TABLE IF EXISTS adverts");
        onCreate(db);
    }

    public void deleteItem(int itemID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("adverts", "id = ?", new String[]{String.valueOf(itemID)});
        db.close();
    }
}
