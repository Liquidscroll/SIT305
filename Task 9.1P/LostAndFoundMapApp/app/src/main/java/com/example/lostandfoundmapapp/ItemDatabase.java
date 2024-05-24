package com.example.lostandfoundmapapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemDatabase  extends SQLiteOpenHelper {
    private static final String DB_NAME = "LostAndFound.db";
    private static final int DB_VERSION = 2;

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
                "location TEXT," +
                "latitude REAL, " +
                "longitude REAL)"
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
