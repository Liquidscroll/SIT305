package com.example.lostandfoundmapapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ItemDatabaseUtils {

    public static List<Item> getItemsFromDB(SQLiteDatabase db) {
        List<Item> items = new ArrayList<>();
        try (Cursor cursor = db.query("adverts", null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    items.add(new Item(
                                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("post_type")),
                                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("phone_number")),
                                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                                cursor.getString(cursor.getColumnIndexOrThrow("location")),
                                cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                                cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
                    ));
                } while (cursor.moveToNext());
            }
        }
        return items;
    }
}